package bgu.spl.net.srv.ServerFrames;

import bgu.spl.net.srv.Frame;
import java.util.List;

public class Message extends Frame{
    public Message(String command, List<String> headers, String body) {
        super(command, headers, body);
    }
}