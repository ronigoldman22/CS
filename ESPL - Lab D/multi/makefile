all: multi
multi:
	nasm -g -f elf32 -W+all -o multi.o multi.s
	gcc -m32 -g -Wall -o multi multi.o

.PHONY: clean
clean:
	rm -f multi.o multi