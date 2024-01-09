#include <elf.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>
#define ON 1
#define OFF 0
#define NO_FILE -1

struct fun_desc {
    char *name;
    void (*fun)();
};

int debug_mode = OFF; //indicates debug mode
int current_fd[2] = {NO_FILE, NO_FILE};
void *map_start[2] = {NULL, NULL};
off_t current_fd_size[2] = {0, 0};
int num_files = 0;
size_t file_size[2];

//Declare all functions here!
void quit();
void examineElfFile();
void toggleDebugMode();
void printSectionNames();
void mergeFiles();
void CheckMerge();
void printSymbols();


int main(int argc, char * argv[]){
    int selected = 0;
    struct fun_desc menu[] = {{"Toggle Debug Mode",toggleDebugMode},{"Examine ELF File",examineElfFile},
    {"Print Section Names",printSectionNames}, 
   {"Print Symbols", printSymbols},{"Check Files for Merge", CheckMerge}, 
   {"Merge ELF Files", mergeFiles},
    {"Quit",quit},{NULL,NULL}};
    printf("Please choose a function:\n");
    int bounds = sizeof(menu)/sizeof(struct fun_desc) - 1;
    for (int i = 0; i<bounds ; i++){
        printf("%d-%s\n",i,menu[i].name);
    }
    printf("Option: ");
    scanf("%d", &selected);
    while (selected<bounds && selected >=0) //in bounds
    {
        getchar();
        menu[selected].fun();
        for (int i = 0; i<bounds ; i++){
            printf("%d-%s\n",i,menu[i].name);
        }
        printf("Option: ");
        scanf("%d", &selected);
    }
    return 0;
}


void quit(){
    exit(0);
}

void toggleDebugMode(){
    debug_mode = ON;
}

void examineElfFile() {
    if (num_files<2){    
        char file_name[100];
        struct stat fd_stat;
        printf("Enter file name: ");
        scanf("%s", file_name);
        getchar();

        FILE *toOpen = fopen(file_name, "r");
        if (!toOpen) {
            printf("Couldn't open file");
            return;
        }

        current_fd[num_files] = fileno(toOpen); 

        if (fstat(current_fd[num_files], &fd_stat) != 0) {
            perror("stat failed");
            return;
        }
        current_fd_size[num_files] = fd_stat.st_size;

        if ((map_start[num_files] = mmap(NULL, fd_stat.st_size, PROT_READ, MAP_SHARED, current_fd[num_files], 0)) == MAP_FAILED) {
            perror("mmap failed");
            return;
        }

        Elf32_Ehdr *header = (Elf32_Ehdr *)map_start[num_files];

        printf("Information for file: %s\n", file_name);
        printf("First three bytes of file: %02X %02X %02X\n", header->e_ident[1], header->e_ident[2], header->e_ident[3]);
        if (header->e_ident[EI_DATA] == ELFDATA2LSB){
            printf("Data content: Two's complement, little-endian.\n");
        }
        else if (header->e_ident[EI_DATA]== ELFDATA2MSB) {
            printf("Data content: Two's complement, big-endian.\n");
        }
        else{
            printf("Data content: Unknown data format.\n");
        }
        printf("Entry point: 0x%08X\n", header->e_entry);
        printf("File offset in section header: 0x%08X\n", header->e_shoff);
        printf("Number of section headers: %d\n", header->e_shnum);
        printf("Sections headers size: 0x%X\n", header->e_shentsize);
        printf("File offset in program header: 0x%08X\n", header->e_phoff);
        printf("Number of program header entries: %d\n", header->e_phnum);
        printf("Program headers size: 0x%X\n", header->e_phentsize);

        num_files++;
        fclose(toOpen);
    }
    else{
        printf("Maximum number of files is 2.\n");
    }
}

void printSectionNames() {
    if (num_files != 0){
        for (int i = 0; i < num_files; i++) {
            printf("File ELF-file-%d\n", i);
            Elf32_Ehdr *header = (Elf32_Ehdr *)map_start[i];
            Elf32_Shdr *sectionHeaders = (Elf32_Shdr *)(map_start[i] + header->e_shoff);
            char *stringTable = (char *)(map_start[i] + sectionHeaders[header->e_shstrndx].sh_offset);

            printf("[index] section_name section_address section_offset section_size section_type\n");
            for (int j = 0; j < header->e_shnum; j++) {
                printf("[%d] %s 0x%08X 0x%06X 0x%06X 0x%X\n", j, stringTable + sectionHeaders[j].sh_name,
                sectionHeaders[j].sh_addr, sectionHeaders[j].sh_offset, sectionHeaders[j].sh_size,
                sectionHeaders[j].sh_type);
            }
            printf("\n");
        }
    }
    else {
        printf("No ELF files opened.\n");
    }
}


void printSymbols() {
    if (num_files != 0){
        for (int fileIndex = 0; fileIndex < num_files; fileIndex++) {
            printf("File ELF-file-%d\n", fileIndex);
            Elf32_Ehdr *header = (Elf32_Ehdr *)map_start[fileIndex];
            Elf32_Shdr *sectionHeaders = (Elf32_Shdr *)(map_start[fileIndex] + header->e_shoff);
            char *stringTable = (char *)(map_start[fileIndex] + sectionHeaders[header->e_shstrndx].sh_offset);
            char *sectionHeadersStringTable = (char *)(map_start[fileIndex] + sectionHeaders[header->e_shstrndx].sh_offset);
            for (int i = 0; i < header->e_shnum; ++i) {
                if (strcmp(sectionHeadersStringTable + sectionHeaders[i].sh_name, ".strtab") == 0) {
                    stringTable = (char *)(map_start[fileIndex] + sectionHeaders[i].sh_offset);
                }
            }
            for (int i = 0; i < header->e_shnum; ++i) {
                if (sectionHeaders[i].sh_type == SHT_SYMTAB || sectionHeaders[i].sh_type == SHT_DYNSYM) {
                    Elf32_Sym *sym = (Elf32_Sym *)(map_start[fileIndex] + sectionHeaders[i].sh_offset);
                    if (debug_mode == ON) {
                        printf("symbol table size:(hex) %X\n", sectionHeaders[i].sh_size);
                        printf("Number of symbols:(hex) %X\n", (sectionHeaders[i].sh_size / sectionHeaders[i].sh_entsize));
                    }
                    printf("idx\t\t\tvalue\t\tsection_index\t\tsection_name\t\tsymbol_name\n");
                    for (int j = 0; j < (sectionHeaders[i].sh_size / sectionHeaders[i].sh_entsize); j++) {
                        if (sym[j].st_shndx == 0) {
                            printf("%d.\t\t\t%-15X\t%-15hd\t\t%-15s\t\t%-15s\n",
                           j, sym[j].st_value,
                           sym[j].st_shndx,
                           "UND",
                           stringTable + sym[j].st_name);
                        }
                        else if (sym[j].st_shndx < header->e_shnum){
                            printf("%d.\t\t\t%-15X\t%-15hd\t\t%-15s\t\t%-15s\n",
                           j, sym[j].st_value,
                           sym[j].st_shndx,
                           sectionHeadersStringTable + sectionHeaders[sym[j].st_shndx].sh_name,
                           stringTable + sym[j].st_name);
                        }
                        else if (sym[j].st_shndx == SHN_ABS)
                            printf("%d.\t\t\t%-15X\t%-15hd\t\t%-15s\t\t%-15s\n",
                           j, sym[j].st_value,
                           sym[j].st_shndx,
                           "ABS",
                           stringTable + sym[j].st_name);
                        else
                            printf("%d.\t\t\t%-15X\t%-15hd\t\t%-15s\t\t%-15s\n",
                           j, sym[j].st_value,
                           sym[j].st_shndx,
                           "COM",
                           stringTable + sym[j].st_name);
                }
            }
        }
        printf("\n");
    }
    }
    else {
        printf("No ELF files opened.\n");
    }

}

void CheckMerge() {
    if (num_files != 2) {
        printf("Error: Exactly 2 ELF files should be opened.\n");
        return;
    }
    Elf32_Ehdr * header1 = (Elf32_Ehdr *) map_start[0];  //point to the header structure 
    int eShnum1 = header1->e_shnum;
    Elf32_Shdr * sectionHeaders1 = (Elf32_Shdr *)(map_start[0] + header1->e_shoff); //get a pointer to section headers
    Elf32_Ehdr * header2 = (Elf32_Ehdr *) map_start[1];  //point to the header structure 
    int eShnum2 = header2->e_shnum;
    Elf32_Shdr *sectionHeaders2 = (Elf32_Shdr *)(map_start[1] + header2->e_shoff); //get a pointer to section headers
    int counter1 = 0;
    int i=0;
    int indexOfTable1 =0;
    while (i<eShnum1) {
        if (sectionHeaders1[i].sh_type == SHT_SYMTAB){
            counter1++;
            indexOfTable1=i;
        }
        i++;
    }
    int counter2 = 0;
    int k=0;
    int indexOfTable2 = 0;
    while (k<eShnum2){
        if (sectionHeaders2[k].sh_type == SHT_SYMTAB){
            counter2++;
            indexOfTable2 = k;
        }
        k++;
    }
    if (counter2!=1 || counter1!=1){
        fprintf(stderr,"feature not supported");
    }
    else{
    Elf32_Shdr *symTabHdr1 = &sectionHeaders1[indexOfTable1];
    Elf32_Sym *sym1 = (Elf32_Sym *)(map_start[0] + sectionHeaders1[indexOfTable1].sh_offset);
    Elf32_Shdr *stringTableHdr1 = &sectionHeaders1[symTabHdr1->sh_link];
    int numOfSym1 = symTabHdr1->sh_size / symTabHdr1->sh_entsize;
    const char* stringTable1 = (char*) (map_start[0] + stringTableHdr1->sh_offset);

    Elf32_Shdr *symTabHdr2 = &sectionHeaders2[indexOfTable2];
    Elf32_Sym *sym2 = (Elf32_Sym *)(map_start[1] + sectionHeaders2[indexOfTable2].sh_offset);
    Elf32_Shdr *stringTableHdr2 = &sectionHeaders2[symTabHdr2->sh_link];
    const char* stringTable2 = (char*) (map_start[1] + stringTableHdr2->sh_offset);
    int numOfSym2 = symTabHdr2->sh_size / symTabHdr2->sh_entsize;

    int canMerge =0;
    for (int j=0; j<numOfSym1; j++){
        Elf32_Sym *tempSym1 = &sym1[j];
        if (tempSym1->st_name != 0) {// not dummy
            int found=0;
            if (tempSym1->st_shndx != 0){
                for (int t=1; t<numOfSym2 ; t++){
                    Elf32_Sym *tempSym2 = &sym2[t];
                    if ((tempSym2->st_shndx != 0) && !strcmp(&stringTable1[tempSym1->st_name],&stringTable2[tempSym2->st_name])) {
                        found=1;
                        break;
                    }
            }
            if (found){
                printf("symbol multiply defined\n");
                canMerge=1;
            } 
        }
        else {
            for (int t=1; t<numOfSym2 ; t++){
                Elf32_Sym *tempSym2 = &sym2[t];
                if (tempSym2->st_shndx != 0 && !strcmp(&stringTable2[tempSym2->st_name], &stringTable1[tempSym1->st_name])) {
                    found=1;
                    break;
                }
            }
            if (!found){
                printf("symbol undefined\n");
                canMerge=1;
            }
        }
    }
    }
    if (canMerge==0)
        printf("can merge!\n");
}
}

Elf32_Shdr* get_Section(Elf32_Shdr* sections, char* shstrtab, int size, char* name){
    for(int i = 0; i < size; i++){
        if(strcmp(&shstrtab[sections[i].sh_name], name) == 0){
            return sections + i;
        }
    }
    return NULL;
}

Elf32_Sym* get_Sym(Elf32_Sym* symbols, char* strtab, int size, char* name){
    for(int i = 0; i < size; i++){
        if(strcmp(&strtab[symbols[i].st_name], name) == 0){
            return symbols + i;
        }
    }
    return NULL;
}

void mergeFiles()
{
    FILE * out_file = fopen("out.ro", "wb");

    Elf32_Ehdr *header1 = (Elf32_Ehdr *)map_start[0];
    Elf32_Ehdr *header2 = (Elf32_Ehdr *)map_start[1];
    fwrite((char*)header1, 1, header1->e_ehsize, out_file); //change "e_shoff" at the end
    // Calculate the symbol table offset within the mapped memory
    Elf32_Shdr *sectionHeaders1 = (Elf32_Shdr *)(map_start[0] + header1->e_shoff);
    Elf32_Shdr *sectionHeaders2 = (Elf32_Shdr *)(map_start[1] + header2->e_shoff);
    //Elf32_Shdr *section_header_table_copy = (Elf32_Shdr *)(map_copy + elf_header1->e_shoff);

    Elf32_Shdr *shstrtab_header1 = &sectionHeaders1[header1->e_shstrndx];
    char *shstrtab1 = (char *)(map_start[0] + shstrtab_header1->sh_offset);
    Elf32_Shdr *shstrtab_header2 = &sectionHeaders2[header2->e_shstrndx];
    char *shstrtab2 = (char *)(map_start[1] + shstrtab_header2->sh_offset);
    int eShnum2 = header2->e_shnum;
    int indexOfTable2=0;
    while (indexOfTable2<eShnum2){
        if (sectionHeaders2[indexOfTable2].sh_type == SHT_SYMTAB){
            break;
        }
        indexOfTable2++;
    }

    Elf32_Shdr *symTabHdr2 = &sectionHeaders2[indexOfTable2];

    Elf32_Sym *sym2 = (Elf32_Sym *)(map_start[1] + sectionHeaders2[indexOfTable2].sh_offset);
    Elf32_Shdr *stringTableHdr2 = &sectionHeaders2[symTabHdr2->sh_link];
    char* stringTable2 = (char*) (map_start[1] + stringTableHdr2->sh_offset);
    int numOfSym2 = symTabHdr2->sh_size / symTabHdr2->sh_entsize;

    Elf32_Shdr copy_shdr[header1->e_shnum];
    int eShnum1 = header1->e_shnum;
    memcpy((char*)copy_shdr, (char*)sectionHeaders1, eShnum1 * header1->e_shentsize);
    
    for (int j = 1; j < eShnum1; j++)
    {
        copy_shdr[j].sh_offset = ftell(out_file);
        char *curr_section_name = &shstrtab1[sectionHeaders1[j].sh_name];
        if ((strcmp(curr_section_name, ".text")!=0) && (strcmp(curr_section_name, ".data")!=0) && (strcmp(curr_section_name, ".rodata")!=0))
        {
            if (strcmp(curr_section_name, ".symtab")!=0) {
                fwrite((char*)(map_start[0] + sectionHeaders1[j].sh_offset),1, sectionHeaders1[j].sh_size, out_file);
            }
            else{
                Elf32_Sym* symbols1 = (Elf32_Sym*)(map_start[0] + sectionHeaders1[j].sh_offset);
                Elf32_Shdr *strtab_header1 = &sectionHeaders1[sectionHeaders1[j].sh_link];
                char *strtab1 = (char *)(map_start[0] + strtab_header1->sh_offset);
                int size = sectionHeaders1[j].sh_size / sectionHeaders1[j].sh_entsize;
                Elf32_Sym copy_sym[size];
                memcpy((char*)copy_sym, (char*)symbols1, sectionHeaders1[j].sh_size);
                for(int i = 0; i < size;i++){
                    if(symbols1[i].st_shndx == SHN_UNDEF){
                        Elf32_Sym* symbol = get_Sym(sym2, stringTable2, numOfSym2, &strtab1[symbols1[i].st_name]);
                        if(symbol != NULL){
                            copy_sym[i].st_value = symbol->st_value;
                            Elf32_Shdr* section = get_Section(sectionHeaders1, shstrtab1, header1->e_shnum, 
                         &shstrtab2[sectionHeaders2[symbol->st_shndx].sh_name]);
                            copy_sym[i].st_shndx = section - sectionHeaders1;
                        }
                    }
                }
                fwrite((char*)(copy_sym),1, sectionHeaders1[j].sh_size, out_file);
            }
        }
        else {
            fwrite((char*)(map_start[0] + sectionHeaders1[j].sh_offset),1, sectionHeaders1[j].sh_size, out_file);
            Elf32_Shdr* section = get_Section(sectionHeaders2, shstrtab2, header2->e_shnum, curr_section_name);
            if(section != NULL){
                fwrite((char*)(map_start[1] + section->sh_offset),1, section->sh_size, out_file);
                copy_shdr[j].sh_size += section->sh_size;
        }
    }
    }
    int offset = ftell(out_file);
    fwrite((char*)(copy_shdr),1, header1->e_shnum * header1->e_shentsize, out_file);
    fseek(out_file, 32, SEEK_SET); //32 is the offset of e_shoff
    fwrite((char*)(&offset),1, sizeof(int), out_file);
    fclose(out_file);

}