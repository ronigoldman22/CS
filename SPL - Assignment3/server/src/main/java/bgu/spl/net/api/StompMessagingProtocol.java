package bgu.spl.net.api;

import bgu.spl.net.srv.ConnectionsImpl;

public interface StompMessagingProtocol<T> extends MessagingProtocol<T>{
	/**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
	**/
    void start(int connectionId, ConnectionsImpl<T> connections);
    
    T process(T message);
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}
