#include "../include/Party.h"
#include "../include/Simulation.h"
#include "../include/Agent.h"


Party::Party(int id, string name, int mandates, JoinPolicy *jp) : mId(id), mName(name), mMandates(mandates), mJoinPolicy(jp), mState(Waiting), timer(0), offers(), IdCoalition(-1)
{ 
}

//copy constructor:
Party::Party(const Party &other):  mId(other.mId), mName(other.mName), mMandates(other.mMandates), mJoinPolicy(other.mJoinPolicy->clonee()), mState(other.mState), timer(other.timer),offers(other.offers), IdCoalition(other.IdCoalition)
{

}

//destructor
    Party::~Party()
    {
        if (mJoinPolicy != nullptr)
           delete mJoinPolicy;
    }

    //move constructor:
    Party::Party(Party &&other): mId(other.mId), mName(other.mName), mMandates(other.mMandates), mJoinPolicy(other.mJoinPolicy), mState(other.mState) ,timer(other.timer), offers(other.offers), IdCoalition(other.IdCoalition) {
      other.mJoinPolicy= nullptr;
    }

    //copy assignment:
    Party& Party:: operator= (const Party &other)
    {
        if (this != &other) {
          delete mJoinPolicy;
          mId = other.mId;
          mName = other.mName;
          mMandates = other.mMandates;
          mJoinPolicy = other.mJoinPolicy->clonee();
          mState = other.mState;
          timer = other.timer;
          offers = other.offers;  
          IdCoalition = other.IdCoalition;
    }
    return *this;
    }
    
    //move assignment:
    Party& Party:: operator= (Party &&other)
    {
       if (this != &other) {
          delete mJoinPolicy;
          mId = other.mId;
          mName = other.mName;
          mMandates = other.mMandates;
          mJoinPolicy = other.mJoinPolicy;
          mState = other.mState;
          timer = other.timer;
          offers = other.offers;  
          IdCoalition = other.IdCoalition;
       }
       other.mJoinPolicy= nullptr;
       return *this;
    }


State Party::getState() const
{
    return mState;
}

void Party::setState(State state)
{
    mState = state;
}

int Party::getMandates() const
{
    return mMandates;
}

const string & Party::getName() const
{
    return mName;
}

void Party::step(Simulation &s)
{
    if (timer == 3){
        mJoinPolicy->join(*this, s);
        timer=0;
    }
    
    if (mState == CollectingOffers)
        timer=timer+1;            
}

vector<int> &Party::getOffers()
{
    return offers;
}


int Party::getTimer()
{
    return timer;
}

void Party::setOffer(int offer)
{
    offers.push_back(offer);
}

int Party::getId()
{
    return mId;
}

int Party::getId() const
{
    return mId;
}

int Party:: getIdCoalition()
{
    return IdCoalition;
}

int Party:: getIdCoalition() const
{
    return IdCoalition;
}

void Party:: setTimer(int i)
{
    timer = i;
}

void Party::setIdCoalition(int i) 
{
   IdCoalition = i;
}