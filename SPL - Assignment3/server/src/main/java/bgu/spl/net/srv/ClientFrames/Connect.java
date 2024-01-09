package bgu.spl.net.srv.ClientFrames;

import bgu.spl.net.srv.Frame;
import java.util.List;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.User;
import java.util.ArrayList;
import bgu.spl.net.api.StompMessagingProtocolImpl;
import bgu.spl.net.srv.ServerFrames.Error;
import bgu.spl.net.srv.ServerFrames.Connected;
import bgu.spl.net.srv.ServerFrames.Message;
import bgu.spl.net.srv.ServerFrames.Receipt;
import bgu.spl.net.srv.Control;
import java.io.IOException;



public class Connect extends Frame{

    //fields
    private String expectedVersion = "accept-version:1.2";
    private String expectedHost ="host:stomp.cs.bgu.ac.il";
    private String expectedLogin = "login:";
    private String expectedPasscode = "passcode:";
    private String expectedReceipt = "receipt:";
    private String receipt = "";


    public Connect(String command, List<String> headers, String body) {
        super(command, headers, body);
    }

    public boolean organizeHeadersInOrder(int receiptIndex, int connectionId, ConnectionsImpl<String> connections,StompMessagingProtocolImpl protocolImpl, Control control) { //return true if success to organize
        String version = "";
        String host = "";
        String login = ""; //username
        String passcode = "";
        if (receiptIndex != -1) {
            receipt = getHeaders().get(receiptIndex);
            getHeaders().remove(receiptIndex); //remove the receipt line from header
        }
        if (getHeaders().size() == 4) {
            String header1 = getHeaders().get(0);
            String header2 = getHeaders().get(1);
            String header3 = getHeaders().get(2);
            String header4 = getHeaders().get(3);
            //check version:
            if (expectedVersion.equals(header1)) {
                version = header1;
            } else if (expectedVersion.equals(header2)) {
                version = header2;
            } else if (expectedVersion.equals(header3)) {
                version = header3;
            } else if (expectedVersion.equals(header4)) {
                version = header4;
            } else { //error - problem in version
                String command = "ERROR";
                List<String> myHeaders = new ArrayList<String>();
                if (receiptIndex!=-1) { myHeaders.add(receipt);}
                myHeaders.add("message: malformed frame received");
                String body = "Did not contain an acceptable version header which is REQUIRED for message propagation.";
                Error frame = new Error(command, myHeaders, body);
                Integer myMessageId = control.getAndIncMessageCounter();
                frame.setMessageId(myMessageId);
                connections.send(connectionId, frame.toString());
                protocolImpl.setShouldTerminate(true);
                connections.disconnect(connectionId);
                
                return false;
            }
            //check host :
            if (expectedHost.equals(header1)) {
                host = header1;
            } else if (expectedHost.equals(header2)) {
                host = header2;
            } else if (expectedHost.equals(header3)) {
                host = header3;
            } else if (expectedHost.equals(header4)) {
                host = header4;
            } else { //error - problem in host
                String command = "ERROR";
                List<String> myHeaders = new ArrayList<>();
                if (receiptIndex!=-1) { myHeaders.add(receipt);}
                myHeaders.add("message: malformed frame received");
                String body = "Did not contain an acceptable host header which is REQUIRED for message propagation.";
                Error frame = new Error(command, myHeaders, body);
                Integer myMessageId = control.getAndIncMessageCounter();
                frame.setMessageId(myMessageId);
                connections.send(connectionId, frame.toString());
                protocolImpl.setShouldTerminate(true);
                connections.disconnect(connectionId);
                
                return false;
            }
            //check login:
            if (header1.length() > 6 && expectedLogin.equals(header1.substring(0, 6))) {
                login = header1;
            } else if (header2.length() > 6 && expectedLogin.equals(header2.substring(0, 6))) {
                login = header2;
            } else if (header3.length() > 6 && expectedLogin.equals(header3.substring(0, 6))) {
                login = header3;
            } else if (header4.length() > 6 && expectedLogin.equals(header4.substring(0, 6))) {
                login = header4;
            } else { //error - problem in login (username)
                String command = "ERROR";
                List<String> myHeaders = new ArrayList<>();
                if (receiptIndex!=-1) { myHeaders.add(receipt);}
                myHeaders.add("message: malformed frame received");
                String body = "Did not contain a login header which is REQUIRED for message propagation.";
                Error frame = new Error(command, myHeaders, body);
                Integer myMessageId = control.getAndIncMessageCounter();
                frame.setMessageId(myMessageId);
                connections.send(connectionId, frame.toString());
                protocolImpl.setShouldTerminate(true);
                connections.disconnect(connectionId);
                
                return false;
            }
            //check passcode:
            if (header1.length() > 9 && expectedPasscode.equals(header1.substring(0, 9))) {
                passcode = header1;
            } else if (header2.length() > 9 && expectedPasscode.equals(header2.substring(0, 9))) {
                passcode = header2;
            } else if (header3.length() > 9 && expectedPasscode.equals(header3.substring(0, 9))) {
                passcode = header3;
            } else if (header4.length() > 9 && expectedPasscode.equals(header4.substring(0, 9))) {
                passcode = header4;
            } else { //error - problem in passcode
                String command = "ERROR";
                List<String> myHeaders = new ArrayList<>();
                if (receiptIndex!=-1) { myHeaders.add(receipt);}
                myHeaders.add("message: malformed frame received");
                String body = "Did not contain a passscode header which is REQUIRED for message propagation.";
                Error frame = new Error(command, myHeaders, body);
                Integer myMessageId = control.getAndIncMessageCounter();
                frame.setMessageId(myMessageId);
                connections.send(connectionId, frame.toString());
                protocolImpl.setShouldTerminate(true);
                connections.disconnect(connectionId);
                
                return false;
            }
            List<String> headersInOrder = new ArrayList<>();
            headersInOrder.add(version);
            headersInOrder.add(host);
            headersInOrder.add(login);
            headersInOrder.add(passcode);
            if (receiptIndex!=-1) { headersInOrder.add(receipt);}
            setHeaders(headersInOrder);
            return true;
        } else {//error - some headers are missing or more headers then needed
            String command = "ERROR";
            List<String> myHeaders = new ArrayList<>();
            if (receiptIndex!=-1) { myHeaders.add(receipt);}
            myHeaders.add("message: malformed frame received");
            String body = "Cannot identify all required headers. make sure you write: accept-version, host, login and passcode.";
            Error frame = new Error(command, myHeaders, body);
            Integer myMessageId = control.getAndIncMessageCounter();
            frame.setMessageId(myMessageId);
            connections.send(connectionId, frame.toString());
            protocolImpl.setShouldTerminate(true);
            connections.disconnect(connectionId);
            
            return false;
        }
    }

    public void execute(int connectionId, ConnectionsImpl<String> connections, StompMessagingProtocolImpl protocolImpl){
        Control control = Control.getControl();
        int indexOfReceiptHeader = thereIsReceiptHeader();
        if (organizeHeadersInOrder(indexOfReceiptHeader,connectionId,connections,protocolImpl,control)) {
            //the order of headers : version, host, login, passcode, receipt
                String login = getHeaders().get(2).substring(6); //the username
                String passcode = getHeaders().get(3).substring(9); //the passcode
                if (connections.usernameIsAlreadyConnected(login)) { //already connected user
                    String command = "ERROR";
                            List<String> myHeaders = new ArrayList<>();
                            if (indexOfReceiptHeader!=-1) { myHeaders.add(receipt);}
                            myHeaders.add("message:connecting error");
                            String body = "user already logged in";
                            Frame frame = new Error(command, myHeaders, body);
                            Integer myMessageId = control.getAndIncMessageCounter();
                            frame.setMessageId(myMessageId);
                            connections.send(connectionId, frame.toString());
                            protocolImpl.setShouldTerminate(true);
                            connections.disconnect(connectionId);
                } else { //is not connected yet
                    if (control.usernameIsExists(login)){ 
                        if (control.getPasscodeOfUsername(login) == passcode) { //right password
                            control.getUserFromUsername(login).setConnected(true);
                            String command = "CONNECTED";
                            List<String> myHeaders = new ArrayList<>();
                            if (indexOfReceiptHeader!=-1) { myHeaders.add(receipt);}
                            myHeaders.add("version:" + control.getUserFromUsername(login).getAccept_version());
                            String body = "";
                            Integer myMessageId = control.getAndIncMessageCounter();
                            Connected frame = new Connected(command, myHeaders, body);
                            frame.setMessageId(myMessageId);
                            connections.send(connectionId, frame.toString());
                        }
                        else{ //wrong password
                            String command = "ERROR";
                            List<String> myHeaders = new ArrayList<>();
                            if (indexOfReceiptHeader!=-1) { myHeaders.add(receipt);}
                            myHeaders.add("message:connecting error");
                            String body = "wrong password";
                            Integer myMessageId = control.getAndIncMessageCounter();
                            Frame frame = new Error(command, myHeaders, body);
                            frame.setMessageId(myMessageId);
                            connections.send(connectionId, frame.toString());
                            protocolImpl.setShouldTerminate(true);
                            connections.disconnect(connectionId);

                        }
                    }else { //new user
                        User user = connections.getUserFromConnectionId(connectionId);
                        user.setLogin(login);
                        user.setPasscode(passcode);
                        control.users.add(user);
                        String command = "CONNECTED";
                        List<String> myHeaders = new ArrayList<>();
                        if (indexOfReceiptHeader!=-1) { myHeaders.add(receipt);}
                        myHeaders.add("version:" + user.getAccept_version());
                        String body = "";
                        Integer myMessageId = control.getAndIncMessageCounter();
                        Frame frame = new Connected(command, myHeaders, body);
                        frame.setMessageId(myMessageId);
                        connections.send(connectionId, frame.toString()); //send frame to client
                    
                    }
                }
            }
            if (!protocolImpl.shouldTerminate() && indexOfReceiptHeader!=-1) { //receipt was required
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
        