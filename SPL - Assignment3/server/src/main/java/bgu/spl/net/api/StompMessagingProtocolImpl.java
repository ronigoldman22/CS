package bgu.spl.net.api;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Control;
import bgu.spl.net.srv.Frame;
import bgu.spl.net.srv.ClientFrames.Connect;
import bgu.spl.net.srv.ClientFrames.Disconnect;
import bgu.spl.net.srv.ClientFrames.Send;
import bgu.spl.net.srv.ClientFrames.Subscribe;
import bgu.spl.net.srv.ClientFrames.Unsubscribe;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import bgu.spl.net.srv.ServerFrames.Error;

public class StompMessagingProtocolImpl<T> implements StompMessagingProtocol<String>{

    //fields:
    private int connectionId;
    private ConnectionsImpl<String> connections;
    private Control control;
    private boolean shouldTerminate;
  

    public StompMessagingProtocolImpl(Control control){
        this.connectionId = -1;
        this.connections = null;
        this.control = control;
        this.shouldTerminate = false;
       
    }

    /**
     * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     **/

    @Override
    public void start(int connectionId, ConnectionsImpl<String> connections){
        this.connectionId = connectionId;
        this.connections = connections;
       
    }

    public String process(String message) {
        String[] messageArray = message.split("\n");
        String messageType = messageArray[0];
        //get the headers of the frame:
        int i = 1;
        String line = messageArray[1]; 
        List<String> headers = new ArrayList<>(); //list of the headers of the frame
        while (!(line.equals(""))) { //not empty line
            // add the header:
            headers.add(line);
            if (i==messageArray.length-1){ //end of message
                break;
            }
            i=i+1;
            line = messageArray[i]; //next line
        }
        //get the body of the frame:
        String body = "";
        i=i+1;
        while ((i<messageArray.length)){ //body of the frame
            body = body + messageArray[i] + "\n";
            i=i+1;
        }
        shouldTerminate = "DISCONNECT".equals(messageType);
        if (shouldTerminate) {
            Disconnect frame = new Disconnect(messageType, headers, body);
            frame.execute(connectionId, connections, this);
        } else if ("SEND".equals(messageType)) {
            Send frame = new Send(messageType, headers, body);
            frame.execute(connectionId, connections, this);
        } else if ("CONNECT".equals(messageType)) {
            Connect frame = new Connect(messageType, headers, body);
            frame.execute(connectionId, connections, this);
        } else if ("SUBSCRIBE".equals(messageType)) {
            Subscribe frame = new Subscribe(messageType, headers, body);
            frame.execute(connectionId, connections, this);
        } else if ("UNSUBSCRIBE".equals(messageType)) {
            Unsubscribe frame = new Unsubscribe(messageType, headers, body);
            frame.execute(connectionId, connections, this);
        } else { //undefined command
            Frame frame1 = new Frame(messageType ,headers ,"");
            int receiptIndex = frame1.thereIsReceiptHeader();
            String command = "ERROR";
            List<String> myHeaders = new ArrayList<>();
            if (receiptIndex != -1) {
                String receipt = frame1.getHeaders().get(receiptIndex);
                myHeaders.add(receipt);
            }
            myHeaders.add("message: malformed frame received");
            String body1 = "the message:" + "\n" +  "-----------" + "\n" ;
            body1 = body1 + frame1.toStringWithoutNullChar() + "-----------" + "\n";
            body1 = body1 + "Undefined command.";
            Error frame = new Error(command, myHeaders, body1);
            Integer myMessageId = control.getAndIncMessageCounter();
            frame.setMessageId(myMessageId);
            connections.getConnectionHandlerFromConnectionId(connectionId).send(frame.toString());
            setShouldTerminate(true);
            connections.disconnect(connectionId);
        }
        return null;
    }



    public void setShouldTerminate(boolean shouldTerminate) {
        this.shouldTerminate = shouldTerminate;
    }


    /**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){
        return shouldTerminate;
    }
}