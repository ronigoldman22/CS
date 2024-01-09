all: hexeditplus digitCounter

hexeditplus: hexeditplus.o
	gcc -g -m32 -Wall -o hexeditplus hexeditplus.o

hexeditplus.o: hexeditplus.c
	gcc -m32 -g -Wall -c -o hexeditplus.o hexeditplus.c

digitCounter.o: digitCounter.c
	gcc -m32 -g -Wall -fno-pie -fno-stack-protector -c -o digitCounter.o digitCounter.c

digitCounter: digitCounter.o
	gcc -g -m32 -Wall -o digitCounter digitCounter.o

.PHONY: 
	clean

clean:
	rm -f *.o hexeditplus