all: task1
task1:
	nasm -g -f elf32 start.s -o start.o 
	gcc -m32 -Wall -g -ansi -c -nostdlib -fno-stack-protector Util.c -o Util.o
	gcc -m32 -Wall -g -o task1 start.o Util.o


.PHONY: clean
clean:
	rm -f *.o task1