package bgu.spl.net.srv.ClientFrames;

import java.util.List;
import java.util.ArrayList;
import bgu.spl.net.srv.Control;
import bgu.spl.net.api.StompMessagingProtocolImpl;
import bgu.spl.net.srv.Frame;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.ServerFrames.Receipt;
import bgu.spl.net.srv.ServerFrames.Error;




public class Disconnect extends Frame{

    //fields
    private String expectedReceipt = "receipt:";

    public Disconnect(String command, List<String> headers, String body) {
        super(command, headers, body);
        setTerminate(true);
    }

    public void execute(int connectionId, ConnectionsImpl<String> connections, StompMessagingProtocolImpl protocolImpl) {
        Control control = Control.getControl();
        String receiptHeader = getHeaders().get(0);
        if (receiptHeader.length()>8 && expectedReceipt.equals(receiptHeader.substring(0,8))) {
        String command = "RECEIPT";
        List<String> myHeaders = new ArrayList<>();
        myHeaders.add(receiptHeader);
        String body = "";
        Receipt frame = new Receipt(command, myHeaders, body);
        Integer myMessageId = control.getAndIncMessageCounter();
        frame.setMessageId(myMessageId);
        connections.send(connectionId, frame.toString());
        protocolImpl.setShouldTerminate(true);
        connections.disconnect(connectionId);
    } else { // error - problem in writing receipt
            String command = "ERROR";
            List<String> myHeaders = new ArrayList<>();
            myHeaders.add("message: malformed frame received");
            String body = "Did not contain a receipt header which is REQUIRED for message propagation.";
            Error frame = new Error(command, myHeaders, body);
            Integer myMessageId = control.getAndIncMessageCounter();
            frame.setMessageId(myMessageId);
            connections.send(connectionId, frame.toString());
            connections.disconnect(connectionId);
            protocolImpl.setShouldTerminate(true);
        }
    }



}