package states;

import java.io.PrintWriter;
import java.util.concurrent.Semaphore;


public abstract class SIPEngaged extends SIPState{

	public SIPEngaged() {
	}
	
//	@Override
//    public  SIPState receiveINVITE(PrintWriter out) { 
//    	out.print(MessageTranslator.TranslateToSend(SIPMessages.SEND_BUSY));
//    	out.flush();
//
//    	return new SIPWaiting();
//    }
	
	@Override
	public boolean busy() {
		return true;
	}
}
