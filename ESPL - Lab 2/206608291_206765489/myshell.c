#include "LineParser.h"
#include <linux/limits.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <stdbool.h>
#include <signal.h>
#include <fcntl.h>

char input[2048];
pid_t pid;

void execute(cmdLine *pCmdLine){
    pid = fork(); //creates a new process
    if (pid<0){
        error();
    }
    if(pCmdLine->blocking == 1){
        int status;
        waitpid(pid,&status,0);
    }
    if(pid == 0){ //child process
    if (pCmdLine->inputRedirect != NULL) {
            int input_fd = open(pCmdLine->inputRedirect, O_RDONLY);
            if (input_fd == -1) {
                perror("open input redirect");
                _exit(EXIT_FAILURE);
            }
            dup2(input_fd, STDIN_FILENO); 
            close(input_fd);
        }
        if (pCmdLine->outputRedirect != NULL) {
            int output_fd = open(pCmdLine->outputRedirect, O_WRONLY | O_CREAT | O_TRUNC);
            if (output_fd == -1) {
                perror("open output redirect");
                _exit(EXIT_FAILURE);
            }
            dup2(output_fd, STDOUT_FILENO);
            close(output_fd);
        }
       if  (execvp(pCmdLine-> arguments[0],pCmdLine->arguments)==-1){
           error();
       }
    }
}

void error(){
        perror("execution failed");
        exit(1);
}

void dbg(bool debug, cmdLine* cmd){
    if(debug){
        fprintf(stderr, "PID: %d\n", pid);
        fprintf(stderr, "Executing command: %s\n",cmd->arguments[0]);
    }
}

int main(int argc, char * argv[]){
    bool debug = false;
    char path[PATH_MAX];
    cmdLine * cmd;
    int killsucc = 0;
    for(int i=1;i<argc;i++){ //check if need to do debug
        if(strncmp("-d",argv[i],2)==0)
            debug=true;
    }
    while (true)
    {
        printf("%s> ",getcwd(path,PATH_MAX));
        fgets(input,2048,stdin);
        cmd = parseCmdLines(input);
        if(strcmp(cmd->arguments[0],"cd")==0){
            if(!cmd->arguments[1])
                perror("couldn't execute cd command without target directory!");
            int err = chdir(cmd->arguments[1]);
            if(err != 0) 
                perror("couldn't find the directory specified!");
            dbg(debug, cmd);
            freeCmdLines(cmd);
            continue;
        }
        if(strcmp(cmd->arguments[0],"quit")==0){
            dbg(debug, cmd);
            freeCmdLines(cmd);
            break;
        }
        bool failed = false;
        if(strcmp(cmd->arguments[0],"suspend")==0){
	            if(kill(atoi(cmd->arguments[1]),SIGTSTP)==-1)
                    failed = true;
                else
                    continue;
	        }
	        if(strcmp(cmd->arguments[0],"kill")==0){
	            if(kill(atoi(cmd->arguments[1]),SIGINT)==-1)
                    failed = true;
                else
                    continue;
	        }
	        if(strcmp(cmd->arguments[0],"wake")==0){
	            if(kill(atoi(cmd->arguments[1]),SIGCONT)==-1)
                    failed = true;
                else
                    continue;
            }
            if (failed){
                perror("failed");
                freeCmdLines(cmd);
	            continue;
            }
        execute(cmd);
        dbg(debug, cmd);
        freeCmdLines(cmd);
    }
}