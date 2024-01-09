#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

int main(int argc, char* argv[]) {
    FILE* input_file = stdin;
    FILE* output_file = stdout;
    bool debug = false; 
    char encodingKey[100];
    int sign =0;
    int keyPosition =0 ;
    int length =0;
    for (int i=0 ; i<argc ; ++i){
        if (debug){
            fprintf(stderr, argv[i]);
            fprintf(stderr, "\n");
        }
        if (strncmp(argv[i], "-i", 2)==0) {
            input_file = fopen(argv[i]+2, "r");
            if (input_file == NULL) {
                fprintf(stderr, "Failed to open input file.");
                return 1;
            }
        } 
        if (strncmp(argv[i], "-o", 2)==0) {
            output_file = fopen(argv[i]+2, "w");
            if (output_file == NULL) {
                fprintf(stderr, "Failed to open output file.");
                return 1;
            }
        }
        if (strcmp(argv[i], "+D")==0){
            debug = true;
        }
        if (strcmp(argv[i], "-D")==0){
            debug = false;
        }
        if (strncmp(argv[i], "+e", 2)==0){
            sign = 1; //add 
            length = stringLength(argv[i])-2;
            for (int j=2; j<length+2 ; j++){
                encodingKey[j-2] = argv[i][j]; 
            }
        }
        if (strncmp(argv[i], "-e", 2)==0){
            sign = -1; //subtruct
            length = stringLength(argv[i])-2;
            for (int j=2; j<length+2 ; j++){
                encodingKey[j-2] = argv[i][j]; 
            }
        }
    }
    int c;
    // encode input:
    while ((c = fgetc(input_file)) != EOF) {
        if ((47<c && c<58)||(64<c && c<91)||(96<c && c<123)) { //check that the input is number or alphabet
            int key_val = encodingKey[keyPosition] - '0'; 
            if (sign==-1) { //subtruct
                key_val = -key_val;
            }
            if (key_val != 0){
                if (47<c && c<58){ //number
                    c = '0' + ((c - '0' + key_val) % 10 + 10) % 10;
                }
                else if (64<c && c<91){ //is upper alphabet
                    c = 'A' + ((c - 'A' + key_val) % 26 + 26) % 26;
                }
                else if (96<c && c<123){ //is lower alphabet
                    c = 'a' + ((c - 'a' + key_val) % 26 + 26) % 26;
                }
                keyPosition = (keyPosition + 1) % (length);
            }
        }
        fputc(c, output_file);
    }
    // Close input and output files
    fclose(input_file);
    fclose(output_file);

    return 0;
}

int stringLength(char* str) {
    int len = 0;
    while (str[len] != '\0') {
        len++;
    }
    return len;
}