all: clean task2

task2: start.o startup.o loader.o
	ld -o task2 loader.o startup.o start.o -L/usr/lib32 -lc -T linking_script -dynamic-linker /lib32/ld-linux.so.2

loader.o:
	gcc -g -m32 -c loader.c -o loader.o

start.o: start.s
	nasm -g -f elf32 start.s -o start.o

startup.o: startup.s
	nasm -g -f elf32 startup.s -o startup.o

.PHONY:
clean:
	rm -f *.o task2
