#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

int main() {
    int fd[2];
    pid_t pid;
    char message[] = "hello";

    // Create a pipe
    if (pipe(fd) == -1) {
        perror("pipe");
        exit(EXIT_FAILURE);
    }

    // Fork a child process
    pid = fork();

    if (pid == -1) {
        perror("fork");
        exit(EXIT_FAILURE);
    } else if (pid == 0) {
        // Child process
        // Close the read end of the pipe
        close(fd[0]);
        // Write the message to the write end of the pipe
        write(fd[1], message, strlen(message) + 1);
        // Close the write end of the pipe
        close(fd[1]);
        // Exit the child process
        exit(EXIT_SUCCESS);
    } else {
        // Parent process
        // Close the write end of the pipe
        close(fd[1]);
        // Read the message from the read end of the pipe
        char received_message[100];
        ssize_t bytes_read = read(fd[0], received_message, sizeof(received_message));
        if (bytes_read > 0) {
            // Print the incoming message
            printf("Received message: %s\n", received_message);
        }
        // Close the read end of the pipe
        close(fd[0]);
        // Wait for the child process to complete
        wait(NULL);
        exit(EXIT_SUCCESS);
    }

    return 0;
}