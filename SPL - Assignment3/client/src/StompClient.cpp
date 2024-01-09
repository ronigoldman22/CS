
#include <stdlib.h>
#include <exception>
#include <iostream>
#include <thread>
#include <string>
#include "../include/ConnectionHandler.h"
#include "../include/ClientThread.h"
using namespace std;

int main(int argc, char *argv[])
{
    StompProtocol protocol = StompProtocol();
    std::cout << "client started" << endl;
    while (!protocol.getIsConnected())
    {
        string command = "";
        try
        {
            std::getline(std::cin, command);
        }
        catch (exception &e)
        {
            std::cout << "error in reading output" << endl;
            continue;
        }
        if (command.substr(0, 5) == "login")
        {
            ConnectionHandler connectionHandler("127.0.0.1", 7777);
            if (!connectionHandler.connect())
            {
                std::cerr << "Cannot connect to "
                          << "127.0.0.1"
                          << ":"
                          << "7777" << std::endl;
                return 1;
            }
            bool ans = protocol.handleRequest(command, &connectionHandler);
            ClientThread clientThread(connectionHandler, protocol);
            std::thread serverSocketThread(&ClientThread::readFromServer, &clientThread); 
            while (ans)
            {
               
                string com = "";
                try
                {
                    std::getline(std::cin, com);
                }
                catch (exception &e)
                {
                    std::cout << "error in reading output" << endl;
                    continue;
                }
                ans = protocol.handleRequest(com, &connectionHandler);
                if (!ans)
                {

                    serverSocketThread.join();
                }
            }
        }
    }
}