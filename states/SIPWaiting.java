package states;

import java.io.PrintWriter;
import java.lang.Thread;
import controller.AudioPlayer;
import controller.MessageReceiverAndTranslator;
import controller.SIPController;
import controller.Semaphores;

public class SIPWaiting extends SIPState {
    public SIPWaiting(){
    	System.out.println("in waiting");
//    	Semaphores.semToKillRecveier = new Semaphore(0);
    }
    
    @Override
    public SIPState receiveINVITE(PrintWriter out) {
    	
    	
    	
    	System.out.print("you got call tro/busy?: ");
    	try {
			Semaphores.semToSysIn.acquire();
		}catch (InterruptedException e) {
		}
    	
//    	System.out.println("answer to call: "+this.answerOfCall);
    	if(this.answerOfCall==null) {
    		
        	out.println(MessageTranslator.TranslateToSend(SIPMessages.SEND_BUSY));
        	out.flush();
        	MessageReceiverAndTranslator.closeEveryThing();
        	return new SIPWaiting();	
    		
    	}else if(this.answerOfCall.equals("tro")) {
        	out.println(MessageTranslator.TranslateToSend(SIPMessages.SEND_TRO));
        	out.flush();

        	return new SIPRespondCall();    		
    	}else {
        	out.println(MessageTranslator.TranslateToSend(SIPMessages.SEND_BUSY));
        	out.flush();
        	MessageReceiverAndTranslator.closeEveryThing();
        	return new SIPWaiting();
    	}       
    }

    @Override
    public SIPState sendINVITE(PrintWriter out) {
    	out.println("invite " + AudioPlayer.my_ipAddress +" " + AudioPlayer.my_port);
        out.flush();
        
        return new SIPTryCall();
    }
}
