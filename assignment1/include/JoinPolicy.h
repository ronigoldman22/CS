#pragma once

class Party;
class Simulation;
class Agent;

class JoinPolicy {
    public:
    virtual void join(Party &party, Simulation &sim)=0;
    virtual JoinPolicy* clonee()=0;
    virtual ~JoinPolicy()=0;
};

class MandatesJoinPolicy : public JoinPolicy {
   public:

    void join(Party &party, Simulation &sim);
    JoinPolicy* clonee();  
};

class LastOfferJoinPolicy : public JoinPolicy {
     public:

    void join(Party &party, Simulation &sim);
    JoinPolicy* clonee();
};