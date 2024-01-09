#include "../include/Simulation.h"

Simulation::Simulation(Graph graph, vector<Agent> agents) : mGraph(graph), mAgents(agents), coalitions()
{
    for (unsigned int i=0 ; i<agents.size() ; ++i){ //build the coalitions vector
        coalitions.push_back(graph.getParty(agents[i].getPartyId()).getMandates());
        //agents[i].setIdCoalition(i);
        graph.getParty(agents[i].getPartyId()).setIdCoalition(i);
    }
}

void Simulation::step()
{
for (int i = 0; i< mGraph.getNumVertices(); ++i)
     mGraph.getParty(i).step(*this);

for (unsigned int i = 0; i< mAgents.size(); ++i)
     mAgents[i].step(*this);
}


bool Simulation::shouldTerminate() const
{
    unsigned int m = mGraph.getNumVertices();
    if (mAgents.size() == m) //all the parties joined a coalitions
         return true;
    for (unsigned int i=0 ; i<coalitions.size() ; ++i) {
        if (coalitions[i] > 60)
            return true;
    }
    return false;
}

const Graph &Simulation::getGraph() const
{
    return mGraph;
}

Graph &Simulation::getGraph() 
{
    return mGraph;
}

const vector<Agent> &Simulation::getAgents() const
{
    return mAgents;
}

void Simulation::addAgent(Agent& a)
{
    mAgents.push_back(a);
}

const Party &Simulation::getParty(int partyId) const
{
    return mGraph.getParty(partyId);
}



/// This method returns a "coalition" vector, where each element is a vector of party IDs in the coalition.
/// At the simulation initialization - the result will be [[agent0.partyId], [agent1.partyId], ...]
const vector<vector<int>> Simulation::getPartiesByCoalitions() const
{
    vector<vector<int>> PartiesByCoalitions(coalitions.size());
    for (unsigned int j=0; j<mAgents.size(); ++j) 
        PartiesByCoalitions[mAgents[j].getIdCoalition()].push_back(mAgents[j].getPartyId());
    return PartiesByCoalitions;
}

void Simulation::setNumOfMandates(int mandates, int coalition)
{
   coalitions[coalition] = coalitions[coalition] + mandates;
}
