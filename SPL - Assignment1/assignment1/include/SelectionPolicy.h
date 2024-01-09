#pragma once

class Graph;
class Agent;
class Party;
class Simulation;

class SelectionPolicy {
    public:
    virtual int select(Agent &agent,  Simulation &sim)=0;
    bool isLegalSelect(Agent &agent, Party &neighbor, Simulation &sim);
    virtual  SelectionPolicy* clonee()=0;
    virtual ~SelectionPolicy()=0;
};

class MandatesSelectionPolicy: public SelectionPolicy{
    public:
     int select(Agent &agent, Simulation &sim);
     SelectionPolicy* clonee();     
};

class EdgeWeightSelectionPolicy: public SelectionPolicy{
     public:
     int select(Agent &agent,  Simulation &sim);
     SelectionPolicy* clonee();
};