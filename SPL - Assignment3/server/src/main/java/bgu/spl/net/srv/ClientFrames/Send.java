package bgu.spl.net.srv.ClientFrames;
import java.util.List;
import java.util.ArrayList;
import bgu.spl.net.srv.ServerFrames.Error;
import bgu.spl.net.srv.ServerFrames.Message;
import bgu.spl.net.srv.ServerFrames.Receipt;
import bgu.spl.net.srv.Control;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.api.StompMessagingProtocolImpl;

import bgu.spl.net.srv.User;

import bgu.spl.net.srv.Frame;

public class Send extends Frame{

    //fields
    private String expectedReceipt = "receipt:";
    private String expectedDestination = "destination:";
    private String receipt = "";

    public Send(String command, List<String> headers, String body) {
        super(command, headers, body);
    }

    public boolean organizeHeadersInOrder(int receiptIndex, int connectionId, ConnectionsImpl<String> connections,StompMessagingProtocolImpl protocolImpl, Control control) {
        String destination = "";
        if (receiptIndex != -1) {
            receipt = getHeaders().get(receiptIndex);
            getHeaders().remove(receiptIndex);
        }
        if (getHeaders().size() == 1) {
            String header1 = getHeaders().get(0);
            //check destination:
            if (header1.length() > 12 && expectedDestination.equals(header1.substring(0, 12))) {
                destination = header1;
            } else { // error - problem with destination
                String command = "ERROR";
                List<String> myHeaders = new ArrayList<>();
                if (receiptIndex!=-1) { myHeaders.add(receipt);}
                myHeaders.add("message: malformed frame received");
                String body = "Did not contain a destination header which is REQUIRED for message propagation.";
                Error frame = new Error(command, myHeaders, body);
                Integer myMessageId = control.getAndIncMessageCounter();
                frame.setMessageId(myMessageId);
                connections.send(connectionId, frame.toString());
                protocolImpl.setShouldTerminate(true);
                connections.disconnect(connectionId);
                return false;
            }
        } else {//error - some headers are missing or more headers then needed
            String command = "ERROR";
            List<String> myHeaders = new ArrayList<>();
            if (receiptIndex!=-1) { myHeaders.add(receipt);}
            myHeaders.add("message: malformed frame received");
            String body = "Cannot identify all required headers. make sure you write destination.";
            Frame frame = new Error(command, myHeaders, body);
            Integer myMessageId = control.getAndIncMessageCounter();
            frame.setMessageId(myMessageId);
            connections.send(connectionId, frame.toString());
            protocolImpl.setShouldTerminate(true);
            connections.disconnect(connectionId);
            return false;
        }
        List<String> headersInOrder = new ArrayList();
        headersInOrder.add(destination);
        if (receiptIndex != -1) { headersInOrder.add(receipt); }
        setHeaders(headersInOrder);
        return true;
    }

    public void execute(int connectionId, ConnectionsImpl<String> connections, StompMessagingProtocolImpl protocolImpl) {
        Control control = Control.getControl();
        int indexOfReceiptHeader = thereIsReceiptHeader();
        if (organizeHeadersInOrder(indexOfReceiptHeader,connectionId,connections,protocolImpl,control)) {
            //order : destination , receipt
            String[] gameSplit = getHeaders().get(0).split(":");
            String game = gameSplit[1];
            if (control.topicIsExists(game)) {
                if (control.IsUserSubscriberOfGame(game, connectionId)) {
                    String command = "MESSAGE";
                    List<String> myHeaders = new ArrayList<>();
                    if (indexOfReceiptHeader != -1) {myHeaders.add(receipt);}
                    //loop to send to all the subscribers:
                    ConcurrentHashMap<User, Integer> subscribers = control.getUser_SubId(game);
                    for (User u : subscribers.keySet()) {
                        Integer myMessageId = control.getAndIncMessageCounter();
                        Integer conId = u.getConnectionId(); 
                        Integer i = subscribers.get(u);
                        myHeaders.add("subscription:" + i);
                        myHeaders.add("message-id:" + myMessageId);
                        myHeaders.add("destination:" + game);
                        String body = getBody();
                        Message frame = new Message(command, myHeaders, body);
                        frame.setMessageId(myMessageId);
                        connections.send(conId, frame.toString());
                    }
                    if (!protocolImpl.shouldTerminate() && indexOfReceiptHeader != -1) { //receipt was required
                        String command1 = "RECEIPT";
                        List<String> myHeaders1 = new ArrayList<>();
                        Integer myMessageId1 = control.getAndIncMessageCounter();
                        myHeaders1.add(receipt);
                        String body1 = "";
                        Receipt frame1 = new Receipt(command1, myHeaders1, body1);
                        frame1.setMessageId(myMessageId1);
                        connections.send(connectionId, frame1.toString());
                    }
                } else { //error - user is not subscriber
                    String command = "ERROR";
                    List<String> myHeaders = new ArrayList<>();
                    if (indexOfReceiptHeader!=-1) { myHeaders.add(receipt);}
                    myHeaders.add("message: user is not subscriber");
                    String body = "make sure you subscribe the topic before send commands.";
                    Frame frame = new Error(command, myHeaders, body);
                    Integer myMessageId = control.getAndIncMessageCounter();
                    frame.setMessageId(myMessageId);
                    connections.send(connectionId, frame.toString());
                    protocolImpl.setShouldTerminate(true);
                    connections.disconnect(connectionId);
                }
           } else { //error - no such topic
            String command = "ERROR";
            List<String> myHeaders = new ArrayList<>();
            if (indexOfReceiptHeader!=-1) { myHeaders.add(receipt);}
            myHeaders.add("message: topic doesn't exists");
            String body = "No such topic.";
            Frame frame = new Error(command, myHeaders, body);
            Integer myMessageId = control.getAndIncMessageCounter();
            frame.setMessageId(myMessageId);
            connections.send(connectionId, frame.toString());
            protocolImpl.setShouldTerminate(true);
            connections.disconnect(connectionId);
        }
}
    }
}