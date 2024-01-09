package bgu.spl.net.srv.ServerFrames;

import java.util.List;

import bgu.spl.net.srv.Frame;

public class Error extends Frame{
    public Error(String command, List<String> headers, String body) {
        super(command, headers, body);
    }
}