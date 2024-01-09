package bgu.spl.net.api;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.srv.Frame;
import bgu.spl.net.srv.ClientFrames.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
public class StompMessageEncoderDecoder implements MessageEncoderDecoder<String>{

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;

    public String decodeNextByte(byte nextByte) {
        if (nextByte == '\u0000') {
            String message = popString();
            return decodeWholeString(message).toString();
        }
        pushByte(nextByte);
        return null;
    }

    public byte[] encode(String message) {
        return (message + "\u0000").getBytes();
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }



    private Frame decodeWholeString(String message){
        //splits every line and insert every line to slot in array
        String[] messageArray = message.split("\n");
        //get the headers of the frame:
        int i = 1;
        String line = messageArray[1]; 
        List<String> headers = new ArrayList<String>(); //list of the headers of the frame
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
        while (i<messageArray.length){ //body of the frame
            body = body + messageArray[i] + "\n";
            i=i+1;
        }
        if (messageArray[0].equals("CONNECT")){
            return new Connect(messageArray[0], headers, body);
        } else if(messageArray[0].equals("SUBSCRIBE")){
            return new Subscribe(messageArray[0], headers, body);
        } else if (messageArray[0].equals("UNSUBSCRIBE")){
                return new Unsubscribe(messageArray[0], headers, body);
            } else if(messageArray[0].equals("SEND")) {
            return new Send(messageArray[0], headers, body);
        } else if (messageArray[0].equals("DISCONNECT")) {
                return new Disconnect(messageArray[0], headers, body);
            }
        return null;
    }

}
