#include "LineParser.h"
#include <linux/limits.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/wait.h>
#include <stdbool.h>
#include <fcntl.h>
#define TERMINATED -1
#define RUNNING 1
#define SUSPENDED 0

char input[2048];
__pid_t pid;

typedef struct process{
    cmdLine* cmd;            /* the parsed command line*/
    pid_t pid; 		         /* the process id that is running the command*/
    int status;              /* status of the process: RUNNING/SUSPENDED/TERMINATED */
    struct process *next;	 /* next process in chain */
} process;

#define HISTLEN 20 /* The length of history queue */
process * head = NULL;
char *history[HISTLEN] = {NULL};
int newestIndex = 0;

void addProcess(process** process_list, cmdLine* cmd, pid_t pid, int status){ 

    process * add = (process*)calloc(1,sizeof(process));
    add->pid=pid;
    add->cmd=cmd;
    add->next=NULL;
    add->status = status;
    if(*process_list!=NULL){
        process * curr = *process_list;
        while (curr->next!=NULL)
            curr=curr->next;
        curr->next=add;
    }
    else{
        *process_list=add;
    }
}

void finishCommand(cmdLine * pCmdLine, bool debug, int status){
    if(debug)
        printf("executing: %s pid is: %d \n",input,pid);
    addProcess(&head,pCmdLine,pid, status);
}

void error(){
        perror("execution failed");
        exit(1);
}

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


void freeProcessList(process* process_list){ 
    if (process_list!=NULL){
        if(process_list->next!=NULL){
            freeProcessList(process_list->next);
            process_list->next = NULL;
        }
        if(process_list->cmd)
            freeCmdLines(process_list->cmd);
        free(process_list);
    }
}

void freeHistory(){ 
    for (int i=0 ; i<HISTLEN ; ++i){
          if (history[i]!=NULL){
            free(history[i]);
             history[i] = NULL;
        }
    }
    newestIndex = 0;
}

void updateProcessStatus(process* process_list, int pid, int status){ 
    process * curr = process_list;
    while (curr){
        if(curr->pid != pid)
            curr = curr->next;
        else
            break;   
    }
    curr->status=status;  
}

void updateProcessList(process **process_list){ 
    pid_t pid=0;
    int status=0;
    if(process_list){
        process * temp = *process_list;
        while (temp!=NULL){
            pid = waitpid(temp->pid,&status,WNOHANG  | WUNTRACED | WCONTINUED);
            if(pid==-1 || WIFSIGNALED(status)){
                updateProcessStatus(*process_list,temp->pid,TERMINATED);
            }else if(WIFCONTINUED(status)){         
                updateProcessStatus(*process_list,temp->pid,RUNNING);
            } else if(WIFSTOPPED(status)){
                updateProcessStatus(*process_list,temp->pid,SUSPENDED);
            }   
            temp = temp->next;
        }
    }
}

void deleteProc(process**process_list,process * toDelete){
    process * first = *process_list;
    if(first==toDelete){
        *process_list=(*process_list)->next;
        freeCmdLines(first->cmd);
        free(first);
        return;
    }
    while (first->next!=toDelete)
        first=first->next;
    first->next=toDelete->next; 
    toDelete->next=NULL; 
    freeCmdLines(toDelete->cmd); 
    free(toDelete); 
}

void printProcessList(process** process_list){ 
    updateProcessList(process_list);
    if(process_list){
        process * curr = *process_list;
        int i=0;
        while (curr != NULL){
            printf("PID:%d   ",curr->pid);
            printf("Command:%s   ",curr->cmd->arguments[0]);
            if (curr->status == RUNNING) 
                printf("STATUS:RUNNING   ");
            else if (curr->status == SUSPENDED) 
                printf("STATUS:SUSPENDED   ");
            else{
                printf("STATUS:TERMINATED   ");
                process * temp=curr;
                printf("ARGUMENTS: ");
                for(int i=1;i<curr->cmd->argCount;i++)
                    printf("%s ",curr->cmd->arguments[i]);
                printf("\n");
                curr=curr->next;
                deleteProc(process_list, temp);
                continue;
            }
            printf("ARGUMENTS: ");
            for(int i=1;i<curr->cmd->argCount;i++)
                printf("%s\t",curr->cmd->arguments[i]);
            printf("\n");
            curr=curr->next;
            i++;
    }
    }
}


void printHistory(){ 
    if (history[newestIndex]!=NULL){
        printf("Number of Valid Entry : %d\n", HISTLEN);
        for (int i=newestIndex ; i<HISTLEN ; ++i){
            printf("%s ", history[i]);
        }
        for (int i=0; i<newestIndex ; ++i){
            printf("%s ", history[i]);
        }
    }
    else{
        printf("Number of Valid Entry : %d\n", newestIndex);
        for (int i=0; i<newestIndex ; ++i){
            printf("%s ", history[i]);
        }
    }
}

void addCmdToHistory(char* cmd){ 
    if (strncmp(cmd, "!!", 2) != 0 && strncmp(cmd, "!n", 2) != 0){
        if (history[newestIndex]!=NULL){
            free(history[newestIndex]);
        }
        history[newestIndex] = (char*)malloc(strlen(cmd) + 1);
        strcpy(history[newestIndex], cmd);
        newestIndex = newestIndex+1;
        if (newestIndex==HISTLEN){
            newestIndex = 0;
        }
    }
}

void debugPrint(char * msg ,bool debug){
    if(debug)
        fprintf(stderr,"%s",msg);
}

void mypipe(cmdLine* cmd, bool debug){
    pid_t pid1,pid2;
    int fd[2]; //pipe for child1
    cmdLine * first = cmd;
    cmdLine * second = cmd->next; 
    cmd->next = NULL;
    pipe(fd);
    debugPrint("(parent_process>forking…)\n", debug);
    pid1=fork();
    if(debug)
        fprintf(stderr,"(parent_process>created process with id: %d)\n",pid1);
    if(pid1 ==0){
        //first child block
        debugPrint("(child1>redirecting stdout to the write end of the pipe…)\n", debug);
        fclose(stdout); //close stdout
        dup(fd[1]); //dup write end
        close(fd[1]); //close write end
        debugPrint("(child1>going to execute cmd: …)\n", debug);
        execvp(first->arguments[0],first->arguments); //execute
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
        execvp(second->arguments[0],second->arguments);
    }
    debugPrint("(parent_process>closing the read end of the pipe…)\n", debug);
    close(fd[0]); //close read end
    debugPrint("(parent_process>waiting for child processes to terminate…)\n", debug);
    waitpid(pid1,NULL,0);
    waitpid(pid2,NULL,0);
    debugPrint("(parent_process>exiting…)\n", debug);
    finishCommand(first, debug, TERMINATED);
    finishCommand(second, debug, TERMINATED);
}


int main(int argc, char * argv[]){
    bool debug = false;
    char path[PATH_MAX];
    cmdLine * cmd;
    for(int i=1;i<argc;i++){
        if(strncmp("-d",argv[i],2)==0)
            debug=true;
    }
    while (true)
    {
        printf("%s> ",getcwd(path,PATH_MAX));
        fgets(input,2048,stdin);
        addCmdToHistory(input);
        cmd = parseCmdLines(input);
        if(strcmp(cmd->arguments[0],"!!")==0){
                if (history[(newestIndex-1)%HISTLEN]!=NULL){
                    addCmdToHistory(history[newestIndex-1]);
                    cmd = parseCmdLines(history[newestIndex-1]);
                }
                else
                {
                    printf("history is empty\n");
                    continue;
                }    
        }
        if(strcmp(cmd->arguments[0],"!n")==0){
            int n = atoi(cmd->arguments[1]);
            if (n>=1 && n<=HISTLEN){
                if (history[n-1]!=NULL){
                    addCmdToHistory(history[n-1]);
                    cmd = parseCmdLines(history[n-1]);
                }
                else{
                    printf("Failed\n");
                    continue;
                }
            }
            else{
                    printf("Failed\n");
                    continue;
            }     
        }
        if(strcmp(cmd->arguments[0],"cd")==0){
             if(!cmd->arguments[1])
                perror("couldn't execute cd command without target directory!");
            int err = chdir(cmd->arguments[1]);
            if(err != 0) 
                perror("couldn't find the directory specified!");
            freeCmdLines(cmd);
            continue;
        }
         if(strcmp(cmd->arguments[0],"quit")==0){
            freeCmdLines(cmd);
            break;
        }
        if(strcmp(cmd->arguments[0],"procs")==0){
            printProcessList(&head);
            freeCmdLines(cmd);
            continue;
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
            if(strcmp(cmd->arguments[0],"history")==0){
	            printHistory();
                continue;
            }
            if (failed){
                perror("failed");
                freeCmdLines(cmd);
	            continue;
            }
            if (cmd->next != NULL){
                mypipe(cmd, debug);
                continue;
            }
        execute(cmd);
        finishCommand(cmd, debug, RUNNING);
    }
    freeProcessList(head);
    freeHistory;

    return 0;
    
}




