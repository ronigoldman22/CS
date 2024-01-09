package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.Server;
import bgu.spl.net.api.StompMessagingProtocolImpl;
import bgu.spl.net.api.StompMessageEncoderDecoder;
import bgu.spl.net.srv.Control;


public class StompServer {

    public static void main(String[] args) {
        Control control = Control.getControl();
        if (args.length > 1) {
             if (args[1].equals("reactor")) {
                Server.reactor(
                        Runtime.getRuntime().availableProcessors(),
                        Integer.parseInt(args[0]), //port
                        () -> new StompMessagingProtocolImpl<String>(control), //protocol factory
                        StompMessageEncoderDecoder::new //message encoder decoder factory
                ).serve();
            } else if (args[1].equals("tpc")){
                Server.threadPerClient(Integer.parseInt(args[0]), //port
                        () -> new StompMessagingProtocolImpl<String>(control),
                        StompMessageEncoderDecoder::new
                ).serve();
           }
       }
    }

}
