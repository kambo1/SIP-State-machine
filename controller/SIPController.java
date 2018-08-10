package controller;

import java.io.PrintWriter;
import states.*;

public class SIPController{

    static public SIPState currentState;
    
    public SIPController() {
    	SIPController.currentState = new SIPWaiting();
    }

    public void triggerState(SIPMessages msg, PrintWriter out) {
        switch(msg) {

            case SEND_INVITE: currentState = currentState.sendINVITE(out); break;
            case SEND_ACK: currentState = currentState.sendACK(out); break;
            case SEND_BYE: currentState = currentState.sendBYE(out); break;
            case SEND_OK: currentState = currentState.sendOK(out); break;
            case SEND_TRO: currentState = currentState.sendTRO(out); break;
            case SEND_BUSY: currentState = currentState.sendBUSY(out); break;
            
            case RECEIVE_INVITE: currentState = currentState.receiveINVITE(out); break;
            case RECEIVE_ACK: currentState = currentState.receiveACK(out); break;
            case RECEIVE_BYE: currentState = currentState.receiveBYE(out); break;
            case RECEIVE_OK: currentState = currentState.receiveOK(out); break;
            case RECEIVE_TRO: currentState = currentState.receiveTRO(out); break;
            case RECEIVE_BUSY: currentState = currentState.receiveBUSY(out); break;
            case RECEIVE_UNKOWN_MESSAGE: currentState = currentState.receiveUNKOWN(out); break;
            case RECEIVE_INTERPTION: currentState = currentState.receiveINTERPTION(out); break;
        }
    }
}
