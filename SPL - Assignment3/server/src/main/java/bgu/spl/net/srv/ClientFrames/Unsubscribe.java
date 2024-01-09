package bgu.spl.net.srv.ClientFrames;

import bgu.spl.net.srv.Frame;
import java.util.List;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.api.StompMessagingProtocolImpl;
import java.util.ArrayList;
import bgu.spl.net.srv.Control;
import java.io.IOException;
import bgu.spl.net.srv.ServerFrames.Error;
import bgu.spl.net.srv.ServerFrames.Receipt;

public class Unsubscribe extends Frame{

    //fields:
    private String expectedReceipt = "receipt:";
    private String expectedId = "id:";
    private String receipt = "";

    public Unsubscribe(String command, List<String> headers, String body) {
        super(command, headers, body);
    }

    public boolean organizeHeadersInOrder(int receiptIndex, int connectionId, ConnectionsImpl<String> connections,StompMessagingProtocolImpl protocolImpl, Control control) {
        String id = "";
        if (receiptIndex != -1) {
            receipt = getHeaders().get(receiptIndex);
            getHeaders().remove(receiptIndex);
        }
        else { // error - problem in writing receipt
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
        if (getHeaders().size() == 1) {
            String header1 = getHeaders().get(0);
            //check id:
            if (header1.length() > 3 && expectedId.equals(header1.substring(0, 3))) {
                id = header1;
            } else { // error - problem with id
                String command = "ERROR";
                List<String> myHeaders = new ArrayList<>();
                if (receiptIndex!=-1) { myHeaders.add(receipt);}
                myHeaders.add("message: malformed frame received");
                String body = "Did not contain an id header which is REQUIRED for message propagation.";
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
            String body = "Cannot identify all required headers. make sure you write id.";
            Frame frame = new Error(command, myHeaders, body);
            Integer myMessageId = control.getAndIncMessageCounter();
            frame.setMessageId(myMessageId);
            connections.send(connectionId, frame.toString());
            protocolImpl.setShouldTerminate(true);
            connections.disconnect(connectionId);
            return false;
        }
        List<String> headersInOrder = new ArrayList();
        headersInOrder.add(id);
        if (receiptIndex != -1) { headersInOrder.add(receipt); }
        setHeaders(headersInOrder);
        return true;
    }


    public void execute(int connectionId, ConnectionsImpl<String> connections, StompMessagingProtocolImpl protocolImpl) {
        Control control = Control.getControl();
        String id = "";
        int indexOfReceiptHeader = thereIsReceiptHeader();
        if (organizeHeadersInOrder(indexOfReceiptHeader,connectionId,connections,protocolImpl,control)) {
            //order : id, receipt
            id = getHeaders().get(0).substring(3);
            if (control.subIdIsExists(connectionId, id)) {
                control.removeSubId(connectionId, id);
                if (indexOfReceiptHeader != -1) { //receipt is required
                    String command1 = "RECEIPT";
                    List<String> myHeaders1 = new ArrayList<>();
                    Integer myMessageId1 = control.getAndIncMessageCounter();
                    myHeaders1.add(receipt);
                    String body1 = "";
                    Receipt frame1 = new Receipt(command1, myHeaders1, body1);
                    frame1.setMessageId(myMessageId1);
                    connections.send(connectionId, frame1.toString());
                }
            } else {
                String command = "ERROR";
                List<String> myHeaders = new ArrayList<>();
                if (indexOfReceiptHeader != -1) {
                    myHeaders.add(receipt);
                }
                myHeaders.add("message: id doesn't exists");
                String body = "Make sure you write correct id.";
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