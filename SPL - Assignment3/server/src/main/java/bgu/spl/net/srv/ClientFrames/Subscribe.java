package bgu.spl.net.srv.ClientFrames;

import bgu.spl.net.srv.Frame;
import java.util.List;
import java.util.ArrayList;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.api.StompMessagingProtocolImpl;
import bgu.spl.net.srv.ServerFrames.Error;
import bgu.spl.net.srv.ServerFrames.Receipt;
import bgu.spl.net.srv.Control;
import bgu.spl.net.srv.User;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Subscribe extends Frame {

    // fields
    private String expectedReceipt = "receipt:";
    private String expectedDestination = "destination:";
    private String expectedId = "id:";
    private String receipt = "";

    public Subscribe(String command, List<String> headers, String body) {
        super(command, headers, body);
    }

    public boolean organizeHeadersInOrder(int receiptIndex, int connectionId, ConnectionsImpl<String> connections,
            StompMessagingProtocolImpl protocolImpl, Control control) {
        String destination = "";
        String id = "";
        if (receiptIndex != -1) {
            receipt = getHeaders().get(receiptIndex);
            getHeaders().remove(receiptIndex);
        } else { // error - problem in writing receipt
            String command = "ERROR";
            List<String> myHeaders = new ArrayList<>();
            myHeaders.add("message: malformed frame received");
            String body = "Did not contain a receipt header which is REQUIRED for message propagation.";
            Error frame = new Error(command, myHeaders, body);
            Integer myMessageId = control.getAndIncMessageCounter();
            frame.setMessageId(myMessageId);
            connections.send(connectionId, frame.toString());
            protocolImpl.setShouldTerminate(true);
            connections.disconnect(connectionId);
            return false;
        }
        if (getHeaders().size() == 2) {
            String header1 = getHeaders().get(0);
            String header2 = getHeaders().get(1);
            // check destination:
            if (header1.length() > 12 && expectedDestination.equals(header1.substring(0, 12))) {
                destination = header1;
            } else if (header2.length() > 12 && expectedDestination.equals(header2.substring(0, 12))) {
                destination = header2;
            } else { // error - problem with destination
                String command = "ERROR";
                List<String> myHeaders = new ArrayList<>();
                if (receiptIndex != -1) {
                    myHeaders.add(receipt);
                }
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
            if (header1.length() > 3 && expectedId.equals(header1.substring(0, 3))) {
                id = header1;
            } else if (header2.length() > 3 && expectedId.equals(header2.substring(0, 3))) {
                id = header2;
            } else { // error - problem with id
                String command = "ERROR";
                List<String> myHeaders = new ArrayList<>();
                if (receiptIndex != -1) {
                    myHeaders.add(receipt);
                }
                myHeaders.add("message: malformed frame received");
                String body = "Did not contain an id header which is REQUIRED for message propagation.";
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
            headersInOrder.add(id);
            if (receiptIndex != -1) {
                headersInOrder.add(receipt);
            }
            setHeaders(headersInOrder);
            return true;
        } else {// error - some headers are missing
            String command = "ERROR";
            List<String> myHeaders = new ArrayList<>();
            if (receiptIndex != -1) {
                myHeaders.add(receipt);
            }
            myHeaders.add("message: malformed frame received");
            String body = "Some headers are missing. make sure you write: destination and id.";
            Frame frame = new Error(command, myHeaders, body);
            Integer myMessageId = control.getAndIncMessageCounter();
            frame.setMessageId(myMessageId);
            connections.send(connectionId, frame.toString());
            protocolImpl.setShouldTerminate(true);
            connections.disconnect(connectionId);
            return false;
        }
    }

    public void execute(int connectionId, ConnectionsImpl<String> connections,
            StompMessagingProtocolImpl protocolImpl) {
        Control control = Control.getControl();
        User user = connections.getUserFromConnectionId((Integer) connectionId);
        int indexOfReceiptHeader = thereIsReceiptHeader();
        if (organizeHeadersInOrder(indexOfReceiptHeader, connectionId, connections, protocolImpl, control)) {
            // order : destination , id , receipt
            String[] gameSplit = getHeaders().get(0).split(":");
            String game = gameSplit[1];
            String[] subSplit = getHeaders().get(1).split(":");
            String subId = subSplit[1];
            if (control.topicIsExists(game)) {
                control.addSubscriber(game, user, subId);
            } else {
                // no such topic - new topic
                ConcurrentHashMap<User, Integer> concur = new ConcurrentHashMap<>();
                Integer subId1 = (Integer) Integer.parseInt(subId);
                concur.put(user, subId1);
                control.addTopic(game, concur);
                control.addSubId((Integer)connectionId, subId1);
            }
            if (indexOfReceiptHeader != -1) { // receipt was required
                String command1 = "RECEIPT";
                List<String> myHeaders1 = new ArrayList<>();
                Integer myMessageId1 = control.getAndIncMessageCounter();
                myHeaders1.add(receipt);
                String body1 = "";
                Frame frame1 = new Receipt(command1, myHeaders1, body1);
                frame1.setMessageId(myMessageId1);
                connections.send(connectionId, frame1.toString());
            }
        }
    }
}