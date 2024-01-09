

#include <stdio.h>
#include <string.h>
#include <ctype.h>

int digit_cnt(char *str) {
    int count = 0;
    for (int i = 0; str[i] != '\0'; i++) {
        if (str[i]>='0' && str[i]<='9')
            count++;
    }
    return count;
}

int main(int argc, char *argv[]) {
    if (argc < 2) {
        printf("No command-line argument provided.\n");
        return 1;
    }

    int digitCount = digit_cnt(argv[1]);
    printf("Number of digits: %d\n", digitCount);

    return 0;
}