#pragma once

#include <vector>
#include "Graph.h"
#include "Agent.h"

using std::string;
using std::vector;


class Simulation
{
public:
Simulation(Graph graph, vector<Agent> agents);
    void step();
    bool shouldTerminate() const;

    const Graph &getGraph() const;
     Graph &getGraph() ;
    const vector<Agent> &getAgents() const;

    void addAgent(Agent& a);
    const Party &getParty(int partyId) const;
   // Party &getPartyA(int partyId);

    const vector<vector<int>> getPartiesByCoalitions() const;
    
    void setNumOfMandates(int mandates, int coalition);

private:
    Graph mGraph;
    vector<Agent> mAgents;
 
public:
    vector<int> coalitions;
};