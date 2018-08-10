package states;

import java.io.PrintWriter;


public class SIPTryCall extends SIPEngaged{

    public SIPTryCall(){
    	System.out.println("in trycall");
    }

    @Override
    public SIPState receiveTRO(PrintWriter out) {
    	out.println(MessageTranslator.TranslateToSend(SIPMessages.SEND_ACK));
    	out.flush();
    	return new SIPTalking();
    }
}