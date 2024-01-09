
#ifndef STOMPPROTOCOL_H
#define STOMPPROTOCOL_H
#pragma once
#include "../include/ConnectionHandler.h"
#include <vector>
#include <map>
#include <string>
#include "../include/event.h"
#include "../include/Game.h"
#include <mutex>
using namespace std;

// TODO: implement the STOMP protocol
class StompProtocol
{
private:
    string username;
    string passcode;
    int receipt;
    int subCounter;
    map<string,int> topicToSubIds;
    map<int, tuple<int, string, int>> receiptToCommand;
    bool isConnected;
    Game game;

public:

    bool handleRequest(string line, ConnectionHandler *connectionHandler);
    string handleAns(string line, ConnectionHandler *connectionHandler);
    string login(string &username,string &passcode);
    string join(string &destination);
    string exit(int subId, string topic);
    void report(string file, ConnectionHandler *connectionHandler);
    void summary(string topic, string username, string file);
    string logout();
    string handleReceipt(string str,ConnectionHandler *connectionHandler);
    void handleMessage(string str);
    const bool getIsConnected() const;
    StompProtocol();
};

#endif 

