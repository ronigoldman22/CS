#include "../include/Game.h"
#include <map>
#include <vector>
#include <sstream>
#include <iostream>
#include <string>
#include <stdlib.h>
#include <fstream>
using namespace std;

Game::Game() : reports{}, stats{} {}

Game::~Game()
{
}

void Game::summary(string topic, string username, string file)
{
    if (reports.find(username) != reports.end())
    {
        if (reports.at(username).find(topic) != reports.at(username).end())
        {
            std::stringstream ss;
            string team_a_name = "";
            string team_b_name = "";
            if (topic.find("_")!=std::string::npos){
            team_a_name = split(topic, '_')[0];
            team_b_name = split(topic, '_')[1];
            }
            ss << team_a_name << " vs " << team_b_name << "\n";
            ss << "Game stats:\n";
            ss << "General stats:\n";
            // take the stats out of the map
            ss << getSummaryStats(username, topic, 0) << "\n";
            ss << team_a_name << " stats:\n";
            // take the stats out of the map
            ss << getSummaryStats(username, topic, 1) << "\n";
            ss << team_b_name << " stats:\n";
            // take the stats out of the map
            ss << getSummaryStats(username, topic, 2) << "\n";
            ss << "Game event reports:\n";
            ss << reports.at(username).at(topic);
            std::string summary = ss.str();
            fstream myFile;
            myFile.open(file, ios::out);
            if (!myFile)
                std::ofstream myFile(file);
            myFile << summary << std::endl;
        }
        else
            cout << "no reports exists" << endl;
    }
    else
        cout << "no reports exists" << endl;
}

void Game::addNewReport(string user, string game, string event)
{
    if (reports.find(user) != reports.end())
    {
        if (reports.at(user).find(game) != reports.at(user).end())
        {
            reports.at(user).at(game) = reports.at(user).at(game) + event;
        }
        else
        { // first report from this user for this game
            reports.at(user).insert(pair<string, string>(game, event));
            map<string, string> generalStats = map<string, string>();
            map<string, string> teamAStats = map<string, string>();
            map<string, string> teamBStats = map<string, string>();
            vector<map<string, string>> vec = vector<map<string, string>>();
            vec.push_back(generalStats);
            vec.push_back(teamAStats);
            vec.push_back(teamBStats);
            stats.at(user).insert(pair<string, vector<map<string, string>>>(game, vec));
        }
    }
    else
    { // first time this user is reporting
        map<string, string> generalStats  = map<string, string>();
        map<string, string> teamAStats= map<string, string>();
        map<string, string> teamBStats= map<string, string>();
        vector<map<string, string>> vec = vector<map<string, string>>();
        vec.push_back(generalStats);
        vec.push_back(teamAStats);
        vec.push_back(teamBStats);
        map<string, vector<map<string, string>>> mapi =  map<string, vector<map<string, string>>>();
        mapi.insert(pair<string, vector<map<string, string>>>(game, vec));
        stats.insert(pair<string, map<string, vector<map<string, string>>>>(user, mapi));
        map<string, string> gameEvents = map<string, string>();
        gameEvents.insert(pair<string, string>(game, event));
        reports.insert(pair<string, map<string, string>>(user, gameEvents));
    }
}

void Game::updateStats(string user, string game, vector<string> &statsToUpdate){
    unsigned i = 1;
    while (i < statsToUpdate.size() && statsToUpdate[i] != "team a updates:")
    {
        if (statsToUpdate[i].find(":") != std::string::npos)
        {
            std::string statName = split(statsToUpdate[i], ':')[0];
            std::string statVal = split(statsToUpdate[i], ':')[1];
            if (stats.at(user).at(game)[0].find(statName) != stats.at(user).at(game)[0].end())
            {
                stats.at(user).at(game)[0].at(statName) = statVal;
            }
            else
            {
 
                stats.at(user).at(game)[0].insert(pair<string, string>(statName, statVal));
            }
        }
        i = i + 1;
    }
    i = i + 1;
    while (i < statsToUpdate.size() && statsToUpdate[i] != "team b updates:")
    {
        if (statsToUpdate[i].find(":") != std::string::npos)
        {
            std::string statName = split(statsToUpdate[i], ':')[0];
            std::string statVal = split(statsToUpdate[i], ':')[1];
            if (stats.at(user).at(game)[1].find(statName) != stats.at(user).at(game)[1].end())
            {
                stats.at(user).at(game)[1].at(statName) = statVal;
            }
            else
            {
                stats.at(user).at(game)[1].insert(pair<string, string>(statName, statVal));
            }
        }
        i = i + 1;
    }
    i = i + 1;
    while (i < statsToUpdate.size())
    {
        if (statsToUpdate[i].find(":") != std::string::npos)
        {
            std::string statName = split(statsToUpdate[i], ':')[0];
            std::string statVal = split(statsToUpdate[i], ':')[1];
            if (stats.at(user).at(game)[2].find(statName) != stats.at(user).at(game)[2].end())
            {
                stats.at(user).at(game)[2].at(statName) = statVal;
            }
            else
            {
                stats.at(user).at(game)[2].insert(pair<string, string>(statName, statVal));
            }
        }
        i = i + 1;
    }
}

string Game::getSummaryStats(string user, string game, int i)
{
    std::stringstream ss;
    for (map<string, string>::iterator it = stats.at(user).at(game)[i].begin(); it != stats.at(user).at(game)[i].end(); it++)
    {
        ss << it->first << ": " << it->second << "\n";
    }
    std::string output = ss.str();
    return output;
}

std::vector<std::string> Game ::split(std::string &line, char delim)
{
    std::vector<std::string> tokens;
    char delimiter = delim;
    std::string token;
    std::string emptyString = "";

    for (unsigned int i = 0; i < line.length(); i++)
    {
        if (line.at(i) != delimiter)
        {
            token += line[i];
        }
        else
        {
            tokens.push_back(token);
            token.clear();
        }
    }

    if (token != emptyString)
    {
        tokens.push_back(token);
    }

    return tokens;
}
