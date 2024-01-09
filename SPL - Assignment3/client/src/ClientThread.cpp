#include "../include/ClientThread.h"
#include "../include/StompProtocol.h"

ClientThread::ClientThread(ConnectionHandler &ch, StompProtocol &prot) : conHandler(ch), protocol(prot)
{
}

void ClientThread::readFromServer()
{
    std::string answer;
    while (answer != "stop")
    {
        if (conHandler.getFrameAscii(answer, '\0')){
            answer = protocol.handleAns(answer, &conHandler);
        }
        else{
            answer = "stop";

            
            
        }
    }
    conHandler.close();
}