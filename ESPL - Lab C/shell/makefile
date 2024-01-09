all: myshell Looper mypipeline

Looper: Looper.o
	gcc -m32 -g -Wall -o Looper Looper.o

Looper.o: Looper.c
	gcc -g -m32 -Wall -c Looper.c

mypipeline: mypipeline.o
	gcc -m32 -g -Wall -o mypipeline mypipeline.o

mypipeline.o: mypipeline.c
	gcc -g -m32 -Wall -c mypipeline.c

myshell : clean myshell.o LineParser.o 
	gcc -m32 -g -Wall -o myshell myshell.o LineParser.o 
	
myshell.o :myshell.c 
	gcc -g -m32 -Wall -c myshell.c 

LineParser.o : LineParser.c
	gcc -g -m32 -Wall -c LineParser.c

clean :
	rm -f *.o myshell Looper mypipeline