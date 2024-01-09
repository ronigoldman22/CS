#include "../include/SelectionPolicy.h"
#include "../include/Graph.h"
#include "../include/Party.h"
#include "../include/Agent.h"
#include "../include/Simulation.h"

bool SelectionPolicy::isLegalSelect(Agent &agent, Party &party, Simulation &sim) //check if already get offer from this coalition of if already joined
{
    if (sim.getGraph().getEdgeWeight(agent.getPartyId(), party.getId()) > 0) { //check if is neighbor
       if ((party).getState() == Joined) //check if already joined
           return false;
       for (unsigned int i=0 ; i< party.getOffers().size() ; ++i) { //already got an offer from this coalition
          if (party.getOffers()[i] == agent.getIdCoalition())
             return false;
    }
    return true;
}
return false;
}

SelectionPolicy:: ~SelectionPolicy()
{

}


int MandatesSelectionPolicy::select(Agent &agent,Simulation &sim) 
{
  bool found = false;
  int i=0;
  while (!found && i<sim.getGraph().getNumVertices()) {
      if (isLegalSelect(agent,sim.getGraph().getParty(i),sim))
          found=true;
       ++i;
  }
  if (found==true){
      int partyToSelect = i-1;
  for (int j=i-1 ; j<sim.getGraph().getNumVertices() ; ++j){
      if (sim.getGraph().getParty(j).getMandates() > sim.getGraph().getParty(partyToSelect).getMandates() && isLegalSelect(agent,sim.getGraph().getParty(j), sim))
         partyToSelect = j;
  }
  return partyToSelect;
  }
  else
    return -1;
}

SelectionPolicy* MandatesSelectionPolicy::clonee()
{
  return new MandatesSelectionPolicy();
}


int EdgeWeightSelectionPolicy::select(Agent &agent,Simulation &sim )
{
  bool found = false;
  int i=0;
  while (!found && i<sim.getGraph().getNumVertices()) {
      if (isLegalSelect(agent,sim.getGraph().getParty(i),sim))
          found=true;
       ++i;
  }
  if (found==true){
      int partyToSelect = i-1;
  for (int j=i-1 ; j<sim.getGraph().getNumVertices() ; ++j){
      if (sim.getGraph().getEdgeWeight(agent.getPartyId(), j) > sim.getGraph().getEdgeWeight(agent.getPartyId(), partyToSelect) && isLegalSelect(agent,sim.getGraph().getParty(j), sim))
         partyToSelect = j;
  }
  return partyToSelect;
  }
  else
    return -1;
}

SelectionPolicy* EdgeWeightSelectionPolicy::clonee(){
  return new EdgeWeightSelectionPolicy();
}

