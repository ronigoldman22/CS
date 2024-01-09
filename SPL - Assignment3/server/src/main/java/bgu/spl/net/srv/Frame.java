package bgu.spl.net.srv;
import java.util.List;

public class Frame{

    //fields:
    private String command;
    private List<String> headers;
    private String body;

    private Integer messageId=-1;
    private boolean terminate = false;

    public Frame(String command, List<String> headers, String body) {
        this.command = command;
        this.headers = headers;
        this.body = body;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isTerminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    public int thereIsReceiptHeader() {
        for (int i=0; i<headers.size() ; i++) {
            if (headers.get(i).length()>=8 && headers.get(i).substring(0,8).equals("receipt:"))
                return i;
        }
        return -1; //no receipt
    }

    public void setMessageId(Integer messageId){
        this.messageId = messageId;
    }

    public String toString() {
        String output = command + "\n";
        int numOfHeaders = getHeaders().size();
        for (int i=0; i<numOfHeaders ; i++) {
            output = output + getHeaders().get(i) + "\n";
        }
        output = output + "\n";
        if (getBody()!=""){
        output = output + getBody() + "\n";
        output = output + "\n";
        }
        output = output + "\u0000";
        return output;

    }

    public String toStringWithoutNullChar() {
        String output = command + "\n";
        int numOfHeaders = getHeaders().size();
        for (int i=0; i<numOfHeaders ; i++) {
            output = output + getHeaders().get(i) + "\n";
        }
        output = output + "\n";
        output = output + getBody() + "\n";
        return output;
    }



}