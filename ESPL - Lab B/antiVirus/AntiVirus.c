#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

typedef struct virus {
    unsigned short SigSize;
    char virusName[16];
    unsigned char* sig;
} virus;


void PrintHex(unsigned char buffer[], long length){
    int i;
    for (i = 0; i < length; i++) 
        printf ("%02X ", buffer[i]);
}

void printVirus(virus* virus, FILE* output){
    fprintf(output, "Virus name: %s \n", virus->virusName);
    fprintf(output, "Virus size: %d \n", virus->SigSize);
    fprintf(output, "signatue: ");
    PrintHex(virus -> sig, virus->SigSize);
    fprintf(output, "\n");
}

virus* readVirus(FILE* file) {
    virus* v = malloc(sizeof(virus));
    if(fread(v, sizeof(char), 18 ,file)==18){
        v->sig = (char*)malloc(v->SigSize*sizeof(char));
        fread(v->sig, sizeof(char), v->SigSize, file);
        return v;
    }
    else {
        free(v->sig);
        free(v);
        return NULL;
    }
    
}

typedef struct link {
    struct link* nextVirus;
    virus* vir;
} link;


void list_print(link* virus_list, FILE* file) {
    link* v = virus_list;
    while (v != NULL) {
        printVirus(v->vir, file);
        fprintf(file, "\n");
        v = v->nextVirus;
    }
}

link* list_append (link* virus_list, virus* data){
   link* newLink = calloc (1, sizeof(link));
   newLink -> vir = data;
   newLink -> nextVirus = NULL;
   link* beginning = virus_list;
   link* current = virus_list;
   link* prev = NULL;

   if(virus_list ==NULL){
       return newLink;
   }
   else
   {
       while (current!=NULL)
       {
           prev = current;
           current = current ->nextVirus;
       }
       prev ->nextVirus = newLink;
       return beginning;
       
   }
}

void list_free (link* virus_list){
    link* v = virus_list;
    while (v!=NULL)
    {
        free(v->vir->sig);
        free(v->vir);
        link* temp = v;
        v = v->nextVirus;
        free(temp);
    }
}

struct fun_desc {
char *name;
char (*fun)();
}; 


void load_signatures(link** virus_list, FILE* file) {
    char magic_number[5];
    fread(magic_number, sizeof(char), 4, file);
    magic_number[4] = '\0';
      if (memcmp(magic_number, "VISL", 4) != 0 && memcmp(magic_number, "VIRL", 4) != 0) {
        printf("Error: Invalid magic number in signature file\n");
        fclose(file);
        return;
    }
    virus* v;
    while ((v = readVirus(file)) != NULL) {
        virus_list = list_append(virus_list, v);
    }
    fclose(file);
}

void detect_virus(char *buffer, unsigned int size, link *virus_list){
    int index = 0;
    int isEqual = 0 ;
    while (virus_list!=NULL) {
        while (index <= size - virus_list->vir->SigSize) {
            isEqual = memcmp(virus_list->vir->sig, buffer+index, virus_list->vir->SigSize);
            if (isEqual == 0) {
                printf("Starting byte location: %d\nThe virus name is: %s\nThe size of virus signature is: %d\n",index,
                    virus_list->vir->virusName,virus_list->vir->SigSize);
                printf("\n");
                break;
            }
            index ++;
        }
        virus_list = virus_list -> nextVirus;
        index = 0;
    }
}

int detectForNeutralize(char *buffer, unsigned int size, link *virus_list, int from){
    int isEqual = 0 ;
    while (virus_list!=NULL) {
        while (from <= size - virus_list->vir->SigSize) {
            isEqual = memcmp(virus_list->vir->sig, buffer+from, virus_list->vir->SigSize);
            if (isEqual == 0) {
                return from;
            }
            from ++;
        }
        from = 0;
        virus_list = virus_list -> nextVirus;
    }
    return -1;
}
void neutralize_virus(char *fileName, int signatureOffset) {
    unsigned char c3 = 0xC3;
    FILE *f = fopen(fileName, "r+");
    if (f==NULL){
        fprintf(stderr, "Failed to open file\n");
    }
    else {
        fseek(f, signatureOffset, SEEK_SET);
        fwrite(&c3, 1, 1, f);
        fclose(f);
    }  
}

int main(int argc, char **argv){
  printf("%s\n", "Select operation from the following menu:"); 
  struct fun_desc menu[] = { { "Load Signature", load_signatures }, { "Print Signature", list_print}, {"detect virus", detect_virus} , {"fix file", neutralize_virus},{"quit", NULL}, { NULL, NULL } }; 
  int bound=sizeof(menu)/sizeof(struct fun_desc)-1;
  link* virus_list = NULL;
  char filename[256];
  char *buffer = calloc(2<<10, sizeof(char));
  FILE* file;
  int fileSize;
  unsigned int min; 
  for (int i=0 ; i<bound ; ++i){ 
    printf("%d%s %s\n", i+1, ")", menu[i].name);
  }
  char in[30];
  while (fgets(in, sizeof(in), stdin) != NULL){
      int num = in[0]-'0'-1;
      if (num<0 || num>=bound){
        printf("Not Within bounds\n\n");
        break;
      }
      else{
        printf("Within bounds\n\n");
        if (num==0){
            printf("Please enter the name of the signature file: ");
            fgets(filename, 256, stdin);
            filename[strcspn(filename, "\n")] = '\0';
            file = fopen(filename, "rb");
            if (!file) {
                printf("Failed to open file \n");
                return;
            }
            if (virus_list!=NULL){
                list_free(virus_list);
            }
            menu[num].fun(&virus_list, file);
        }
        if (num==1){
            menu[num].fun(virus_list, stdout);
        }
         else if (num == 2){
            file = fopen(argv[1], "rb");
            fseek(file, 0, SEEK_END); 
            fileSize = ftell(file); 
            fseek(file, 0, SEEK_SET); 
            min = fileSize <2<<10 ? fileSize : 2<<10;
            fread (buffer,1,min, file);
            menu[num].fun(buffer, fileSize , virus_list);
            fclose(file);
}
else if (num==3){
    file = fopen(argv[1], "rb");
            fseek(file, 0, SEEK_END); 
            fileSize = ftell(file); 
           fseek(file, 0, SEEK_SET); 
            min = fileSize <2<<10 ? fileSize : 2<<10;
            fread (buffer,1,min, file);
            fclose(file);
    int i = detectForNeutralize(buffer, fileSize , virus_list, 0);
    while (i!=-1){
        menu[num].fun(argv[1], i);
        i=i+1;
         file = fopen(argv[1], "rb");
            fseek(file, 0, SEEK_END); 
            fileSize = ftell(file); 
            fseek(file, 0, SEEK_SET); 
            min = fileSize <2<<10 ? fileSize : 2<<10;
            fread (buffer,1,min, file);
            fclose(file);
        i = detectForNeutralize(buffer, fileSize , virus_list, i);
    }
    
}
else if (num==4){
    break;
}
        printf("%s\n", "Select operation from the following menu:");
        for (int i=0 ; i<bound ; ++i){ 
          printf("%d%s %s\n", i+1, ")", menu[i].name);
        }
      }
  }
  printf("exit..\n");
  free(buffer);
    list_free(virus_list);
}


