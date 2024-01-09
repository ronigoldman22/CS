#include "../include/StompProtocol.h"
#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <utility>
#include <tuple>
#include <fstream>

using std::string;
using std::vector;
using namespace std;

StompProtocol ::StompProtocol() : username(""), passcode(""), receipt(0), subCounter(0), topicToSubIds(), receiptToCommand(), isConnected(false), game(Game())
{
}

// handle commands from the keyboard:
bool StompProtocol::handleRequest(string line, ConnectionHandler *connectionHandler)
{
    string ans;

    vector<string> words;
    std::istringstream iss(line);
    std::string word;

    while (iss >> word)
    {
        words.push_back(word);
    }

    if (words[0] == "login" && words.size() == 4)
    {
        if (!isConnected)
        {
            ans = login(words[2], words[3]);
            username = words[2];
            passcode = words[3];
            if (!connectionHandler->sendLine(ans))
            {
                cout << "Disconnected. Exiting...\n"
                     << endl;
            }
        }
        else if (username != words[2])
        {
            cout << "the client is already logged in, logout before trying again"
                 << endl;
        }
    }
    else if (words[0] == "join" && words.size() == 2)
    {
        ans = join(words[1]);
        if (!connectionHandler->sendLine(ans))
        {
            cout << "Disconnected. Exiting...\n"
                 << endl;
        }
    }
    else if (words[0] == "exit" && words.size() == 2)
    {
        int subscriptionId = -1;
        try // find subId if exists
        {
            subscriptionId = topicToSubIds.at(words[1]);
        }
        catch (std::out_of_range &e)
        {
        }
        ans = exit(subscriptionId, words[1]);
        if (!connectionHandler->sendLine(ans))
        {
            std::cout << "Disconnected. Exiting...\n"
                      << std::endl;
        }
    }
    else if (words[0] == "report" && words.size() == 2)
    {
        report(words[1], connectionHandler);
    }
    else if (words[0] == "summary" && words.size() == 4)
    {
        summary(words[1], words[2], words[3]);
    }
    else if (words[0] == "logout")
    {
        ans = logout();
        if (!connectionHandler->sendLine(ans))
        {
            std::cout << "Disconnected. Exiting...\n"
                      << std::endl;
        }
        else
        {
            return false;
        }
    }
    else
        std::cout << "no such command\n"
                  << std::endl;

    return true;
}

string StompProtocol::login(string &username, string &passcode)
{
    std::stringstream ss;
    ss << "CONNECT\n";
    ss << "accept-version:1.2\n";
    ss << "host:stomp.cs.bgu.ac.il\n";
    ss << "login:" << username << "\n";
    ss << "passcode:" << passcode << "\n\n";
    ss << "\0";

    std::string output = ss.str();
    return output;
}

string StompProtocol::join(string &destination)
{
    std::stringstream ss;
    ss << "SUBSCRIBE\n";
    ss << "destination:" << destination << "\n";
    ss << "id:" << subCounter << "\n";
    ss << "receipt:" << receipt << "\n";
    ss << "\n";
    ss << "\0";

    std::string output = ss.str();
    receiptToCommand[receipt] = {0, destination, subCounter}; //join

    subCounter++;
    receipt++;

    return output;
}

string StompProtocol::exit(int subId, string topic)
{
    std::stringstream ss;
    ss << "UNSUBSCRIBE\n";
    ss << "id:" << subId << "\n";
    ss << "receipt:" << receipt << "\n";
    ss << "\n";
    ss << "\0";
    std::string output = ss.str();
    receiptToCommand[receipt] = {1, topic, subId}; //exit.

    receipt++;
    return output;
}

void StompProtocol::report(string file, ConnectionHandler *connectionHandler)
{
    names_and_events nne = parseEventsFile("data/" + file);
    string team_a = nne.team_a_name;
    string team_b = nne.team_b_name;
    vector<Event> &events = nne.events;
    string topic = team_a + "_" + team_b;
    for (unsigned i = 0; i < events.size(); ++i)
    {
        std::stringstream ss;
        ss << "SEND\n";
        ss << "destination:" << topic << "\n";
        ss << "\n";
        ss << "user:" << username << "\n";
        ss << "team a:" << team_a << "\n";
        ss << "team b:" << team_b << "\n";
        ss << "event name:" << events[i].get_name() << "\n";
        ss << "time:" << events[i].get_time() << "\n";
        map<string, string> gameUpdates = events[i].get_game_updates();
        ss << "general game updates:\n";
        for (map<string, string>::iterator it = gameUpdates.begin(); it != gameUpdates.end(); it++)
        {
            ss << it->first << ":" << it->second << "\n";
        }
        ss << "team a updates:\n";
        map<string, string> aUpdates = events[i].get_team_a_updates();
        for (map<string, string>::iterator it = aUpdates.begin(); it != aUpdates.end(); it++)
        {
            ss << it->first << ":" << it->second << "\n";
        }

        ss << "team b updates:\n";
        map<string, string> bUpdates = events[i].get_team_b_updates();
        for (map<string, string>::iterator it = bUpdates.begin(); it != bUpdates.end(); it++)
        {
            ss << it->first << ":" << it->second << "\n";
        }
        ss << "description:\n"
           << events[i].get_discription() << "\n";
        ss << "\0";
        std::string event = ss.str();
        if (!connectionHandler->sendLine(event))
        {
            std::cout << "Disconnected. Exiting...\n"
                      << std::endl;
            break;
        }
    }
}

void StompProtocol::summary(string topic, string username, string file)
{
    game.summary(topic, username, file);
}

string StompProtocol::logout()
{
    std::stringstream ss;
    ss << "DISCONNECT\n";
    ss << "receipt:" << receipt << "\n";
    ss << "\n";
    ss << "\0";
    std::string output = ss.str();
    receiptToCommand[receipt] = {2, "", -1}; //logout.

    receipt++;
    return output;
}

// handle answer got from the server:
string StompProtocol::handleAns(string line, ConnectionHandler *connectionHandler)
{
    string ans = "";

    vector<string> words;
    std::istringstream iss(line);
    std::string word;

    while (iss >> word)
    {
        words.push_back(word);
    }
    if (words.size() > 0)
    {
        if (words[0] == "CONNECTED")
        {
            ans = "Login successful";
            isConnected = true;
            cout << ans << endl;
        }
        else if (words[0] == "RECEIPT")
            ans = handleReceipt(words[1], connectionHandler);
        else if (words[0] == "MESSAGE")
        {
            handleMessage(line);
        }
        else if (words[0] == "ERROR")
        {
            ans = "stop";
            isConnected = false;
            game.~Game();
        }
    }
    return ans;
}

void StompProtocol ::handleMessage(string message)
{
    vector<string> lines = game.split(message, '\n');
    string gameName;
    if (lines[3].find(':') != std::string::npos)
    {
        vector<string> splited = game.split(lines[3], ':');
        gameName = splited[1];
    }
    string user;
    if (lines[5].find(':') != std::string::npos)
    {
        vector<string> splited = game.split(lines[5], ':');
        user = splited[1];
    }
    int time;
    if (lines[9].find(':') != std::string::npos)
    {
        vector<string> splited = game.split(lines[9], ':');
        time = stoi(splited[1]);
    }
    string eventName;
    if (lines[8].find(':') != std::string::npos)
    {
        vector<string> splited = game.split(lines[8], ':');
        eventName = splited[1];
    }
    unsigned i = 10;
    vector<string> stats = vector<string>();
    while (i < lines.size() && lines[i].substr(0, 12) != "description:")
    {
        stats.push_back(lines[i]);
        i=i+1;
    }
    i = i + 1;
    string description;
    while (i < lines.size())
    {
        description = description + lines[i];
        i = i + 1;
    }
     std::stringstream ss;
     ss << time << " - " << eventName << ":\n";
     ss << "\n";
     ss << description << "\n"  << "\n" << endl;;
     string event = ss.str();
    game.addNewReport(user, gameName, event);
    game.updateStats(user, gameName, stats);
}

string StompProtocol::handleReceipt(string str, ConnectionHandler *connectionHandler)
{
    int receiptId;
    string ans = "";
    if (str.find(':') != std::string::npos)
    {
        vector<string> splited = game.split(str, ':');
        receiptId = std::stoi(splited[1]);
    }

    std::tuple<int, string, int> tuple = receiptToCommand[receiptId];
    int commandType = std::get<0>(tuple);
    string channel = std::get<1>(tuple);
    int mySubId = std::get<2>(tuple);

    if (commandType == 0)
    {
        ans = "Joined channel " + channel;
        cout << ans << endl;
        topicToSubIds[channel] = mySubId; // add subId and topic
    }

    if (commandType == 1)
    {
        ans = "Exited channel " + channel;
        cout << ans << endl;
        topicToSubIds.erase(channel); // erase subId and topic
    }

    if (commandType == 2) // disconnect
    {
        ans = "stop";
        isConnected = false;
        game.~Game();
    }
    receiptToCommand.erase(receiptId); // erase receipt
    return ans;
}

const bool StompProtocol::getIsConnected() const
{
    return isConnected;
}
