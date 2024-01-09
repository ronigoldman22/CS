#pragma once
#include <string>
#include <vector>
#include "JoinPolicy.h"

using std::string;
using std::vector;

enum State
{
    Waiting,
    CollectingOffers,
    Joined
};

class Simulation;
class Agent;

class Party
{
public:
    Party(int id, string name, int mandates, JoinPolicy *); 

 //destructor
    ~Party();
    //copy constructor:
    Party(const Party &other);
    //move constructor:
    Party(Party &&other);
    //copy assignment:
    Party& operator = (const Party &other);
    //move assignment:
    Party& operator = (Party &&other);
    State getState() const;
    void setState(State state);
    int getMandates() const;
    void step(Simulation &s);
    const string &getName() const;
    vector<int> &getOffers();
    int getTimer();
    void setTimer(int i);
    void setOffer(int offer);
    int getId();
    int getId() const;
    int getIdCoalition();
    int getIdCoalition() const;
    void setIdCoalition(int i);
    
private:
    int mId;
    string mName;
    int mMandates;
    JoinPolicy *mJoinPolicy;
    State mState;
    int timer;
    vector<int> offers;  
    int IdCoalition;
};