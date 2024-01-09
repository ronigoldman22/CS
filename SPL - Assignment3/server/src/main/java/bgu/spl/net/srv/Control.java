package bgu.spl.net.srv;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayList;

public class Control {

    // fields:
    private ConcurrentHashMap<String, ConcurrentHashMap<User, Integer>> gameName_User_SubId;
    private ConcurrentHashMap<Integer, List<Integer>> connectionIds_subscriptionIds;
    private Integer messageCounter;
    public List<User> users;
    private static Control dif = null;

    private Control() {
        this.gameName_User_SubId = new ConcurrentHashMap<>();
        this.connectionIds_subscriptionIds = new ConcurrentHashMap<>();
        this.users = new ArrayList<>();
        this.messageCounter = 0;
    }

    public static Control getControl() {
        if (dif == null) {
            dif = new Control();
        }
        return dif;
    }

    public Integer getAndIncMessageCounter() {
        synchronized (messageCounter) {
            Integer output = messageCounter;
            messageCounter++;
            return output;
        }
    }

    public List<User> getSubscribersOfGame(String game) {
        ArrayList list = new ArrayList();
        gameName_User_SubId.get(game).forEach((i, k) -> list.add(i));
        return list;
    }

    public ConcurrentHashMap<User, Integer> getUser_SubId(String game) {
        return gameName_User_SubId.get(game);
    }

    public boolean IsUserSubscriberOfGame(String game, int connectionId) {
        boolean out = false;
        for (User u : gameName_User_SubId.get(game).keySet()) {
            if (u.getConnectionId() == (Integer) connectionId) {
                out = true;
                break;
            }
        }
        return out;
    }

    public void disconnect(Integer connectionId) {
        connectionIds_subscriptionIds.remove(connectionId);
        for (String game : gameName_User_SubId.keySet()) {
            for (User u : gameName_User_SubId.get(game).keySet()) {
                if (u.getConnectionId() == connectionId)
                    gameName_User_SubId.get(game).remove(u);
            }
        }
    }

    public void addSubscriber(String game, User user, String subId) {
        Integer connectionId = user.getConnectionId();
        Integer subId1 = (Integer) Integer.parseInt(subId);
        if (!gameName_User_SubId.get(game).containsKey(user)) {
            gameName_User_SubId.get(game).put(user, subId1);
            connectionIds_subscriptionIds.get(connectionId).add(subId1);
        }

    }

    public void addconnectionId(Integer connectionId) {
        List<Integer> subId = new LinkedList<>();
        connectionIds_subscriptionIds.put(connectionId, subId);
    }

    public void addSubId(Integer connectionId, Integer subId) {
        connectionIds_subscriptionIds.get(connectionId).add(subId);
    }

    public boolean subIdIsExists(int connectionId, String id) {
        Integer conId1 = (Integer) connectionId;
        Integer id1 = Integer.parseInt(id);
        return connectionIds_subscriptionIds.get(conId1).contains(id1);
    }

    public void removeSubId(int connectionId, String id) {
        Integer conId1 = (Integer) connectionId;
        Integer id1 = Integer.parseInt(id);
        connectionIds_subscriptionIds.get(conId1).remove(id1);
        User user = ConnectionsImpl.getConnections().getUserFromConnectionId((Integer) conId1);
        for (ConcurrentHashMap<User, Integer> concur : gameName_User_SubId.values()) {
            if (concur.containsKey(user) && concur.get(user) == id1) {
                concur.remove(user, id1);
                break;
            }
        }
        connectionIds_subscriptionIds.get(conId1).remove(id1);
    }

    public boolean topicIsExists(String topic) {
        boolean out = false;
        for (String game : gameName_User_SubId.keySet()) {
            if (game.equals(topic)) {
                out = true;
                break;
            }
        }
        return out;
    }

    public void addTopic(String game, ConcurrentHashMap<User, Integer> concur) {
        gameName_User_SubId.put(game, concur);
    }

    public Boolean usernameIsExists(String username) {
        Boolean output = false;
        for (User u : users) {
            if (u.getLogin().equals(username)) {
                output = true;
                break;
            }
        }
        return output;
    }

    public String getPasscodeOfUsername(String username) {
        String ans = "";
        for (User u : users) {
            if (u.getLogin().equals(username)) {
                ans = u.getPasscode();
                break;
            }
        }
        return ans;
    }

    public User getUserFromUsername(String username) {
        User user = null;
        for (User u : users) {
            if (u.getLogin().equals(username)) {
                user = u;
                break;
            }
        }
        return user;
    }
}
