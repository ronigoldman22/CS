#include <stdlib.h>
#include <stdio.h>
#include <string.h>

char my_get(char c);
char cprt(char c);
char encrypt(char c);
char decrypt(char c);
char xprt(char c);
 
char* map(char *array, int array_length, char (*f) (char)){
  char* mapped_array = (char*)(malloc(array_length*sizeof(char)));
  /* TODO: Complete during task 2.a */
  for (int i=0; i<array_length ; ++i){
    mapped_array[i] = f(array[i]);
  }
  return mapped_array;
}

/* Ignores c, reads and returns a character from stdin using fgetc. */
char my_get(char c){
  return fgetc(stdin);
}

/* If c is a number between 0x20 and 0x7E, cprt prints the character of ASCII value c followed by a new line. Otherwise, cprt prints the dot ('.') character. After printing, cprt returns the value of c unchanged. */
char cprt(char c){
  if (0x20<c && c<0x7E){
    printf("%c\n", c);
  }
  else
  {
    printf("%c\n", '.');
  }
  return c;
}

/* Gets a char c and returns its encrypted form by adding 1 to its value. If c is not between 0x20 and 0x7E it is returned unchanged */
char encrypt(char c){
  if (0x20<=c && c<=0x7E){
    return c+1;
  }
  else
  {
    return c;
  }
}

/* Gets a char c and returns its decrypted form by reducing 1 from its value. If c is not between 0x20 and 0x7E it is returned unchanged */
char decrypt(char c){
  if (0x20<=c && c<=0x7E){
    return c-1;
  }
  else
  {
    return c;
  }
}

/* xprt prints the value of c in a hexadecimal representation followed by a new line, and returns c unchanged. */ 
char xprt(char c){
  printf("%x\n", c);
  return c;
}

struct fun_desc {
char *name;
char (*fun)(char);
}; 

int main(int argc, char **argv){
  printf("%s\n", "Select operation from the following menu:"); //task 3a
  char* carray = (char*)(malloc(5*sizeof(char))); //task 3b(1)
  struct fun_desc menu[] = { { "Get String", my_get }, { "Print String", cprt }, {"Print Hex", xprt}, { "Encrypt", encrypt }, { "Decrypt", decrypt },{ NULL, NULL } }; //task 3b(2)
  int bound=sizeof(menu)/sizeof(struct fun_desc)-1;
  for (int i=0 ; i<bound ; ++i){ //task 3b(3)
    printf("%d%s %s\n", i, ")", menu[i].name);
  }
  char in[30];
  while (fgets(in, sizeof(in), stdin) != NULL){
    //task 3b(4,5):
      int num = in[0]-'0';
      if (num<0 || num>=bound){
        printf("%s\n", "Not Within bounds");
        break;
      }
      else{
        printf("%s\n", "Within bounds");
        char* t = carray;
        carray = map(carray,5, menu[in[0]-'0'].fun);
        free(t);
        printf("%s\n", "Select operation from the following menu:");
        for (int i=0 ; i<bound ; ++i){ 
          printf("%d%s %s\n", i, ")", menu[i].name);
        }
      }
  }
  printf("exit..\n");
  free(carray);
}




 

  
