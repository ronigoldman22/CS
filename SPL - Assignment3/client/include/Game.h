#pragma once
#ifndef GAME_H
#define GAME_H
#include <map>
#include <vector>
#include "../include/event.h"
using namespace std;

class Game{
    private:
    map<string, map<string, string>> reports;
    map<string, map<string, vector<map<string, string>>>> stats;
    
    public:
    Game();
    virtual ~Game();
    void addNewReport(string user, string game,  string event);
    void updateStats(string user,  string game, vector<string>& statsToUpdate);
    string getSummaryStats(string user, string game, int i);
    vector <string> split(string &st, char delimeter);
    void summary(string topic, string username, string file);
    };
    #endif