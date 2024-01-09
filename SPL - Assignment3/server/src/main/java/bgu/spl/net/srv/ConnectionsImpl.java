package bgu.spl.net.srv;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl.net.srv.Connections;
import java.io.IOException;

public class ConnectionsImpl<T> implements Connections<T>{

    //fields:
    private Integer numOfConnections;
    private ConcurrentHashMap<Integer, ConnectionHandler> connectionIds_connectionHandlers;
    private ConcurrentHashMap<Integer, User> connectionIds_users;
    private static ConnectionsImpl dif = null;

    private ConnectionsImpl(){
        this.connectionIds_connectionHandlers =  new ConcurrentHashMap<>();
        this.connectionIds_users =  new ConcurrentHashMap<>();
        this.numOfConnections = 0;
    }

    public static ConnectionsImpl getConnections(){
        if (dif==null) {
            dif = new ConnectionsImpl();
        }
        return dif;
    }

    public boolean send(int connectionId, T msg){
       ConnectionHandler ch = getConnectionHandlerFromConnectionId(connectionId); 
       try {
           synchronized (ch) {
               ch.send(msg);
           }
           return true;
       } catch (Exception e){
           return false;
       }
    }

    //send to all subscribers of the game the message
    public void send(String game, T msg){
        List<User> users = Control.getControl().getSubscribersOfGame(game);
        for (User u : users) {
            send(u.getConnectionId().intValue(), msg);
        
    }
}

    //the user with the connectionId wants to disconnect
    public void disconnect(int connectionId){
        connectionIds_users.remove((Integer)connectionId);
        Control.getControl().disconnect((Integer)connectionId);
             connectionIds_connectionHandlers.remove((Integer)connectionId);
    }

    public Integer connect(ConnectionHandler ch){
        synchronized (numOfConnections) {
            Integer output = numOfConnections;
            connectionIds_connectionHandlers.put(numOfConnections, ch);
            User user = new User("","",false);
            user.setConnectionId(numOfConnections);
            connectionIds_users.put(numOfConnections, user);
            Control.getControl().addconnectionId(numOfConnections);
            numOfConnections++;
            return output;
        }
    }

    public void setConnectionIds_users(User user){
        connectionIds_users.put(numOfConnections, user);
    }

    public User getUserFromConnectionId(Integer connectionId){
        if (connectionIds_users.containsKey(connectionId)) {
            return connectionIds_users.get(connectionId);
        }
        return null;
    }

    public ConnectionHandler getConnectionHandlerFromConnectionId(int connectionId){
        return connectionIds_connectionHandlers.get((Integer)connectionId);
    }

    public User getUserFromUsername(String username) {
        for (User u : connectionIds_users.values()) {
            if (u.getLogin().equals(username)) {
                return u;
            }
        }
        return null;
    }
    public User getUserFromConnectionId(int connectionId){
        return connectionIds_users.get(((Integer)connectionId));
    }


    public boolean usernameIsAlreadyConnected(String username) {
        boolean out = false;
        for (User u : connectionIds_users.values()) {
            if (u.getLogin().equals(username)) {
                out = true;
                break;
            }
        }
        return out;
    }

}