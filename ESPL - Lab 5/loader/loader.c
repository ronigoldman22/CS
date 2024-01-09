#include <stdio.h>
#include <stdint.h>
#include <elf.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

extern int startup(int argc, char **argv, void (*start)()); 

int foreach_phdr(void *map_start, void (*func)(Elf32_Phdr *, int), int fd) {
    Elf32_Ehdr *elf_header = (Elf32_Ehdr *)map_start;
    Elf32_Phdr *phdr_table = (Elf32_Phdr *)(map_start + elf_header->e_phoff);

    for (int i = 0; i < elf_header->e_phnum; i++) {
        Elf32_Phdr *phdr = &phdr_table[i];
        func(phdr, fd);
    }

    return 0;
}

void print_phdr(Elf32_Phdr *phdr, int index) {

    // Determine the appropriate mapping flags
    int map_flags = MAP_PRIVATE | MAP_FIXED;
    if (phdr->p_flags & PF_X)
        map_flags |= MAP_EXECUTABLE;

    printf("Type\tOffset\tVirtAddr\tPhysAddr\tFileSiz\tMemSiz\tFlg\tAlign\n");
    printf("%#x\t%#x\t%#x\t%#x\t%#x\t%#x\t%#x\t%#x\n", phdr->p_type, phdr->p_offset, phdr->p_vaddr, phdr->p_paddr, phdr->p_filesz, phdr->p_memsz, phdr->p_flags, phdr->p_align);
    printf("The protection flags: %d\n", phdr->p_flags);
    printf("The map flags: %d\n", map_flags);
}


void load_phdr(Elf32_Phdr *phdr, int fd) {
    if (phdr->p_type == PT_LOAD) {
        // Determine the appropriate protection flags
        int prot_flags = 0;
        if (phdr->p_flags & PF_R)
            prot_flags |= PROT_READ;
        if (phdr->p_flags & PF_W)
            prot_flags |= PROT_WRITE;
        if (phdr->p_flags & PF_X)
            prot_flags |= PROT_EXEC;
        void *segment_addr = mmap(phdr->p_vaddr&0xfffff000, phdr->p_memsz + (phdr->p_vaddr & 0xfff), prot_flags, MAP_PRIVATE | MAP_FIXED, fd, phdr->p_offset&0xfffff000);
        if (segment_addr == MAP_FAILED) {
            perror("mmap");
            close(fd);
            return;
        }
        print_phdr(phdr, fd);
    }
}

 int main(int argc, char *argv[]) {
    const char *file_name = argv[1];

    // Open the file
    int fd = open(file_name, O_RDONLY);
    if (fd == -1) {
        perror("open");
        return 1;
    }

    // Get file size
    struct stat file_info;
    if (fstat(fd, &file_info) == -1) {
        perror("fstat");
        close(fd);
        return 1;
    }
    off_t file_size = file_info.st_size;

    // Map the file into memory
    void *map_start = mmap(NULL, file_size, PROT_READ, MAP_PRIVATE, fd, 0 & 0xfffff000);
    if (map_start == MAP_FAILED) {
        perror("mmap");
        close(fd);
        return 1;
    }
    foreach_phdr(map_start, load_phdr, fd);
    Elf32_Ehdr *elf_header = (Elf32_Ehdr *)map_start;
    startup(argc-1, argv+1, (void*)elf_header->e_entry);

    // Unmap the memory and close the file
    munmap(map_start, file_size);
    close(fd);

    return 0;
}