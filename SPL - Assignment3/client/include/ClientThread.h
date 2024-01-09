#pragma once
#ifndef CLIENTTHREAD_H
#define CLIENTTHREAD_H
#include "../include/ConnectionHandler.h"
#include "../include/StompProtocol.h"


class ClientThread {
    private:
    ConnectionHandler &conHandler;
    StompProtocol &protocol;

    public:
    ClientThread(ConnectionHandler& ch, StompProtocol& prot);
    void readFromServer();
};
#endif