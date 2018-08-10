package states;

import java.io.PrintWriter;
import java.util.concurrent.Semaphore;


public abstract class SIPState{
	public String answerOfCall;
	
	public SIPState(){

	}
	
	public SIPState sendINVITE(PrintWriter out) { 
		return new SIPWaiting(); 
	}

    public  SIPState sendACK(PrintWriter out){
        return new SIPWaiting(); 
    }

    public  SIPState sendBYE(PrintWriter out)
    {
        return new SIPWaiting(); 
    }

    public  SIPState sendOK(PrintWriter out){
        return new SIPWaiting(); 
    }

    public SIPState sendTRO(PrintWriter out) {
    	return new SIPWaiting(); 
    }
    
    public SIPState sendBUSY(PrintWriter out) {
    	out.print(MessageTranslator.TranslateToSend(SIPMessages.SEND_BUSY));
    	out.flush();

    	return new SIPWaiting(); 
    }

    public  SIPState sendUNKOWN(PrintWriter out){
    	return new SIPWaiting();
    }

    
    public  SIPState receiveINVITE(PrintWriter out) { 
    	return new SIPWaiting(); 
    }

    public  SIPState receiveACK(PrintWriter out){
        return new SIPWaiting(); 
    }

    public  SIPState receiveBYE(PrintWriter out){
        return new SIPWaiting(); 
    }

    public  SIPState receiveOK(PrintWriter out){
        return new SIPWaiting(); 
    }
    
    public  SIPState receiveTRO(PrintWriter out){
        return new SIPWaiting(); 
    }
    
    public  SIPState receiveBUSY(PrintWriter out){
//    	out.print(MessageTranslator.TranslateToSend(SIPMessages.SEND_BUSY));
//    	out.flush();
    	return new SIPWaiting();
    }
    
    public  SIPState receiveUNKOWN(PrintWriter out){
    	return new SIPWaiting();
    }

    public  SIPState receiveINTERPTION(PrintWriter out){
    	return new SIPWaiting();
    }
    
    
    public boolean busy() {
    	return false;
    }
}