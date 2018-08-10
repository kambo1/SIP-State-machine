package states;

import controller.AudioPlayer;

public class MessageTranslator {
	public static String TranslateToSend(SIPMessages msg) {
		String s = null;
		
		switch (msg) {			
			case SEND_ACK:
				s = "ack";
				break;
			case SEND_INVITE:
				s = "invite";
				break;
			case SEND_BYE:
				s = "bye";
				break;
			case SEND_OK:
				s = "ok";
				break;
			case SEND_TRO:
				s = "tro"+ " "+AudioPlayer.my_port;
				break;
			case SEND_BUSY:
				s = "busy";
				break;
				
			default:
				break;
		}				
		
		return s;
	}
}
