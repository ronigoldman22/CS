#include "../include/Agent.h"
#include "../include/Party.h"
#include "../include/Graph.h"
#include "../include/Simulation.h"


Agent::Agent(int agentId, int partyId, SelectionPolicy *selectionPolicy)
: mAgentId(agentId), mPartyId(partyId), mSelectionPolicy(selectionPolicy), idCoalition(agentId)
{
} 

//copy constructor:
Agent::Agent(const Agent &other): mAgentId(other.mAgentId),mPartyId(other.mPartyId), mSelectionPolicy(other.mSelectionPolicy->clonee()), idCoalition(other.idCoalition){

}

//destructor
    Agent::~Agent(){
        if (mSelectionPolicy != nullptr)
          delete mSelectionPolicy; 
    }

    //move constructor:
    Agent::Agent(Agent &&other): mAgentId(other.mAgentId), mPartyId(other.mPartyId), mSelectionPolicy(other.mSelectionPolicy), idCoalition(other.idCoalition){
      other.mSelectionPolicy= nullptr;
    }

    //copy assignment:
    Agent& Agent:: operator= (Agent& other){
        if (this != &other) {
          delete mSelectionPolicy;
          mAgentId = other.mAgentId;
          mPartyId = other.mPartyId;
          mSelectionPolicy = other.mSelectionPolicy->clonee();
          idCoalition = other.idCoalition;
    }
    return *this;
    }
    
    //move assignment:
    Agent& Agent:: operator= (Agent &&other)
    {
       if (this != &other) {
          delete mSelectionPolicy;
          mAgentId = other.mAgentId;
          mPartyId = other.mPartyId;
          mSelectionPolicy = other.mSelectionPolicy;
          idCoalition = other.idCoalition;
    }
       other.mSelectionPolicy= nullptr;
       return *this;
    }



int Agent::getId() const
{
    return mAgentId;
}

int Agent::getPartyId() const
{
    return mPartyId;
}

void Agent::step(Simulation &sim)
{
    int selectedPartyId = mSelectionPolicy->select(*this, sim);
    if (selectedPartyId != -1) {
    Party &selectedParty = sim.getGraph().getParty(selectedPartyId);
    selectedParty.setOffer(idCoalition);
    if (selectedParty.getState() == Waiting){
        selectedParty.setState(CollectingOffers);
        selectedParty.setTimer(1);
    }
}
}

SelectionPolicy* Agent::getSelectionPolicy(){
    return mSelectionPolicy;
}

void Agent:: cloneAgent(int partyId, Simulation &sim) const
{
    Agent copy(*this);
    copy.setId(sim.getAgents().size());
    copy.setPartyId(partyId);
    sim.addAgent(copy);
}

    void Agent::setPartyId(int i)
    {
        mPartyId = i;
    }

    int Agent::getIdCoalition() 
    {
        return idCoalition;
    }

    int Agent::getIdCoalition() const
    {
        return idCoalition;
    }


    void Agent::setIdCoalition(int i) 
    {
      idCoalition=i;
    }

    void Agent::setId(unsigned int i) 
    {
        mAgentId = i;
    }


