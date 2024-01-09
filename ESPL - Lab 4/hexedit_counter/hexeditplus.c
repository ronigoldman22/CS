

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define true 1
#define false 0

typedef struct {
  char debug_mode;
  char file_name[128];
  int unit_size;
  unsigned char mem_buf[10000];
  size_t mem_count;
  /*
   .
   .
   Any additional fields you deem necessary - char display_mode;
  */
  char display_mode;
} state;

struct fun_desc {
    char *name;
    void (*fun)(state* s);
};

void toggleDebugMode(state* s){
    if (!s->debug_mode) { //debug mode is false
        s->debug_mode = true;
        printf("Debug flag now on\n");
    } else{ //debug mode is true
        s->debug_mode = false;
        printf("Debug flag now off\n");
    }
}

void setFileName(state* s){
        printf("Enter File Name\n");
        scanf("%s", s->file_name);
        if(s->debug_mode){
            printf("Debug: filename set to:%s \n",s->file_name);
        }
}


void setUnitSize(state* s){
        printf("Enter size (1/2/4)\n");
        int in;
        scanf("%d", &in);
        if (in == 1 || in == 2 || in == 4) {
            s->unit_size = in;
            if (s->debug_mode) {
                printf("Debug: set size to %d\n", in);
            }
        } else {
            fprintf(stderr,"Invalid size\n");
        }
}

void quit (state* s){
    if (s->debug_mode){
        printf("quitting\n");
    }
    free(s);
    exit(0);
}

void loadIntoMemory (state* s){
    FILE* file = NULL;
    char input[100];
    int location;
    int length;
    if (s)
    {
        if(strcmp(s->file_name,"")==0){
            printf("empty filename!\n");
            return;
        }
        file = fopen(s->file_name,"rb");
        if (file)   //file opened successfully
        {
            printf("Please enter <location> <length>\n");
            fgets(input, 100, stdin);
            sscanf(input, "%x %d\n", &location, &length);
            if (s->debug_mode == true){
                printf("Debug filename: %s\n", s->file_name);
                printf("Debug location: %x\n", location);
                printf("Debug length: %d\n", s->mem_count);
            }
            s->mem_count = length * s->unit_size;
            fseek(file, location, SEEK_SET);
            fread(s->mem_buf, s->unit_size, s->mem_count, file);
            fclose(file);
            printf("Loaded %d units into memory\n",s->mem_count);
        }
        else
        {
            printf("error while opening the file\n");
        }
    }
    else
    {
        printf("state is null");
    }
}

void toggleDisplayMode (state* s){
    if (s->display_mode == 0) {
        s->display_mode = 1;
        printf("Display flag now on, hexadecimal representation\n");
    } else{
        s->display_mode = 0;
        printf("Display flag now off, decimal representation\n");
    }
}

void memoryDispaly (state* s){
    int units, addr;
    char input[100];
    char* mem;
    if (s->display_mode == 1){ 
        printf("Please enter <address> <length>\n");
        fgets(input, 100, stdin);
        sscanf(input, "%X %d\n", &addr, &units);
        printf("Hexadecimal\n");
        printf("===========\n");
        if (addr!=0){
            mem = (char*)addr;
        } else{     //speacial case
            mem = (char*)s->mem_buf;
        }
        displayUnits(mem, s, units,1);
    }
    if (s->display_mode == 0){
        printf("Please enter <address> <length>\n");
        fgets(input, 100, stdin);
        sscanf(input, "%X %d\n", &addr, &units);
        printf("Decimal\n");
        printf("===========\n");
        if (addr!=0){
            mem = (char*)addr; //check this case
        } else{     //speacial case
            mem = (char*)s->mem_buf;
        }
        displayUnits(mem, s, units, 0);
    }
}

void displayUnits(char *mem, state *s, int units, int mode) {
    int i;
    unsigned int toprint;
    for (i = 0; i < units; i=i+1) {
        toprint = *((int*)(mem+(i*s->unit_size)));
        if (mode==0){
            if (s->unit_size == 1){printf("%hhd\n", toprint);}
            else if (s->unit_size == 2){printf("%hd\n", toprint);}
            else    {printf("%d\n", toprint);}
        }   
        else if (mode==1) {
           if (s->unit_size == 1){printf("%hhx\n", toprint);}
            else if (s->unit_size == 2){printf("%hx\n", toprint);}
            else {printf("%x\n", toprint);}
        }
    }
}

void saveIntoFile (state* s){
    FILE* file = NULL;
    char input[100];
    unsigned long source_address; //hexa
    unsigned long target_location; //hexa
    int length; //decimal
    long fileLength = 0;
    printf("Please enter <source-address> <target-location> <length>\n");
    fgets(input, 100, stdin);
    sscanf(input, "%lx %lx %d\n", &source_address, &target_location, &length);
    //address = (char *) (source_address != 0 ? source_address : s->mem_buf);
    file = fopen(s->file_name, "rb+");
    if (!file){
        fprintf(stderr,"couldn't open file %s \n",s->file_name);
    }
    else{
        fseek(file, 0L, SEEK_END);
        fileLength = ftell(file);
        if (target_location > fileLength) {
            fprintf(stderr,"target location is greater than file size\n");
        }
        else { 
            fseek(file, target_location, SEEK_SET);
            if (source_address == 0) {
                fwrite(s->mem_buf, s->unit_size, length, file);
            }
            else{
                fwrite(&source_address, s->unit_size, length, file);
            }
        }
        fclose(file);
    }
}

void memoryModify (state* s){
    char input[100];
    int location=0; //hexa
    int value=0; //hexa
    printf("Please enter <location> <val>\n");
    fgets(input,100,stdin);
    sscanf(input,"%x %x",&location,&value);
    printf("value :%d\n",value);
    if(location > 10000-4)
        printf("location exceeds buffer!\n");
    else{
        if (s->unit_size == 4){
        (int*)(&s->mem_buf);
        int * changeValue = &s->mem_buf[location];
        *changeValue= value; // stores the wanted value 
        }
        else if (s->unit_size == 2){
            (short*)(&s->mem_buf);
            short * changeValue = &s->mem_buf[location];
            *changeValue= value; // stores the wanted value 
        }
        else{
            (char*)(&s->mem_buf);
            char * changeValue = &s->mem_buf[location];
            *changeValue= value; // stores the wanted value 
        }
    }
}

int main(int argc, char * argv[]){
    int option = 0;
    state* state = calloc(1,sizeof(state));
    state->debug_mode = false;
    state->unit_size = 1;
    state->display_mode = 0;
    struct fun_desc menu[] = { { "Toggle Debug Mode", toggleDebugMode }, { "Set File Name", setFileName }, { "Set Unit Size", setUnitSize },
                               {"Load Into Memory", loadIntoMemory},{"Toggle Display Mode", toggleDisplayMode},{"Memory Display", memoryDispaly},
                               {"Save Into File", saveIntoFile},{"Memory Modify", memoryModify},{ "Quit", quit },{ NULL, NULL } };
    int bound = sizeof(menu)/sizeof(struct fun_desc) - 1;
    printf("Please choose a function:\n");
    for (int i = 0; i<bound ; i++)
    {
        printf("%d-%s\n",i,menu[i].name);
    }
    scanf("%d", &option);
    while (option<bound && option >=0) //in bounds
    {
       getchar();
        menu[option].fun(state);
        printf("Please choose a function:\n");
        for (int i = 0; menu[i].name!=NULL; i++)
        {
            printf("%d) %s\n",i,menu[i].name);
        }
        scanf("%d", &option);
    }
    return 0;
}