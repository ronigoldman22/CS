#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <linux/limits.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdbool.h>



void debugPrint(char * msg ,bool debug){
    if(debug)
        fprintf(stderr,"%s",msg);
}

int main(int argc, char * argv[]){
    bool debug= false;
    pid_t pid1,pid2;
    int fd[2]; //pipe for child1
    char *ls[] = { "ls", "-l", 0 }; //ls command
    char *tail[] = { "tail", "-n","2", 0 }; //tail command
    for(int i=1;i<argc;i++){
        if(strncmp(argv[i],"-d",2)==0){
            debug=true;
        }
    }
    pipe(fd);
    debugPrint("(parent_process>forking…)\n", debug);
    pid1=fork();
    if(debug)fprintf(stderr,"(parent_process>created process with id: %d)\n",pid1);
    if(pid1 ==0){
        //first child block
        debugPrint("(child1>redirecting stdout to the write end of the pipe…)\n", debug);
        fclose(stdout); //close stdout
        dup(fd[1]); //dup write end
        close(fd[1]); //close write end
        debugPrint("(child1>going to execute cmd: …)\n", debug);
        execvp(ls[0],ls); //execute
    }
    debugPrint("(parent_process>closing the write end of the pipe…)\n", debug);
    close(fd[1]); //parent closes write end
    debugPrint("(parent_process>forking…)\n", debug);
    pid2=fork();
    if(debug)fprintf(stderr,"(parent_process>created process with id: %d)\n",pid2);
    if(pid2 == 0){
        //second child block
        debugPrint("(child2>redirecting stdin to the read end of the pipe…)\n", debug);
        fclose(stdin); //close stdin
        dup(fd[0]); //duplicate read end
        close(fd[0]);
        debugPrint("(child2>going to execute cmd: …)\n", debug);
        execvp(tail[0],tail);
    }
    debugPrint("(parent_process>closing the read end of the pipe…)\n", debug);
    close(fd[0]); //close read end
    debugPrint("(parent_process>waiting for child processes to terminate…)\n", debug);
    waitpid(pid1,NULL,0);
    waitpid(pid2,NULL,0);
    debugPrint("(parent_process>exiting…)\n", debug);

}
