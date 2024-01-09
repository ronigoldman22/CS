#include "../include/JoinPolicy.h"
#include "../include/Party.h"
#include "../include/Simulation.h"
#include "../include/Agent.h"

JoinPolicy:: ~JoinPolicy()
{

}

void MandatesJoinPolicy::join(Party &party, Simulation &sim) 
{
   int toJoin = party.getOffers()[0];
      for (unsigned int j=0 ; j<party.getOffers().size()  ; ++j) {
         if (sim.coalitions[party.getOffers()[j]] > sim.coalitions[toJoin])
           toJoin=party.getOffers()[j];
   }
   party.setState(Joined);
   sim.setNumOfMandates(party.getMandates(), toJoin);
   party.setIdCoalition(toJoin);
   sim.getAgents()[toJoin].cloneAgent(party.getId(),sim);
}

JoinPolicy* MandatesJoinPolicy:: clonee()
{
   return new MandatesJoinPolicy(); 
}


void LastOfferJoinPolicy::join(Party &party, Simulation &sim)
{
   unsigned int toJoin = party.getOffers()[party.getOffers().size()-1];
   party.setState(Joined);
   sim.setNumOfMandates(party.getMandates(),toJoin);
   sim.getAgents()[toJoin].cloneAgent(party.getId(),sim);
   party.setIdCoalition(toJoin);
}

JoinPolicy* LastOfferJoinPolicy:: clonee()
{
   return new LastOfferJoinPolicy(); 
}
