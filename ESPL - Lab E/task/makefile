all: task1

task1: task1.o
	gcc -g -m32 task1.o -o task1

task1.o: task1.c
	gcc -g -m32 -c -o task1.o task1.c

.PHONY: clean
clean:
	rm -r task1.o task1