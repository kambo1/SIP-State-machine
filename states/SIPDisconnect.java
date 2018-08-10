package states;

import java.io.PrintWriter;

import controller.MessageReceiverAndTranslator;

public class SIPDisconnect extends SIPEngaged {

    public SIPDisconnect(){
    	System.out.println("in disconnect");

    }

    @Override
    public SIPState receiveOK(PrintWriter out) {
    	MessageReceiverAndTranslator.closeEveryThing();
    	
    	return new SIPWaiting();
    }
}
