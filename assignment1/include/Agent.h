#pragma once

#include <vector>
#include "SelectionPolicy.h"

class Graph;
class Party;
class Simulation;
using std::vector;


class Agent
{
public:
    Agent(int agentId, int partyId, SelectionPolicy *selectionPolicy);
    
    //destructor
    ~Agent();
    //copy constructor:
    Agent(const Agent &other);
    //move constructor:
    Agent(Agent &&other);
    //copy assignment:
    Agent& operator = (Agent& other);
    //move assignment:
    Agent& operator = (Agent &&other);

    int getPartyId() const;
    int getId() const;
    void step(Simulation &);
    SelectionPolicy* getSelectionPolicy();
    void cloneAgent(int partyId, Simulation &sim) const;
    int getIdCoalition();
    int getIdCoalition() const;
    void setIdCoalition(int i);
    void setId(unsigned int i);
    void setPartyId(int i);

private:
    int mAgentId;
    int mPartyId;
    SelectionPolicy *mSelectionPolicy;
    int idCoalition;
};