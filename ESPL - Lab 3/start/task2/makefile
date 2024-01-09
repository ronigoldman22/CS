all: task2
task2:
	gcc -m32 -Wall -ansi -c -nostdlib -fno-stack-protector Util.c -o Util.o
	gcc -m32 -Wall -ansi -c -nostdlib -fno-stack-protector main.c -o main.o 
	nasm -f elf32 start.s -o start.o 
	ld -m elf_i386 start.o main.o Util.o -o task2
.PHONY: clean
clean:
	rm -f *.o task2