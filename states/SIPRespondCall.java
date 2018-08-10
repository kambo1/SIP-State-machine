package states;

import java.io.PrintWriter;


public class SIPRespondCall extends SIPEngaged {

    public SIPRespondCall(){
    	System.out.println("in respond");

    }

    @Override
    public SIPState receiveACK(PrintWriter out) {
    	
        return new SIPTalking();
    }
}
