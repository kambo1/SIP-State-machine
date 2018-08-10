package controller;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import states.SIPMessages;
import states.SIPWaiting;

public class MessageReceiverAndTranslator implements Runnable{

	private BufferedReader in;
	private PrintWriter out;
	private SIPController sipController;
	public Socket connecterSocket;
	public static Socket connecterSocketToClose;
	
	static Thread keepAliveThread;
	static Thread timeOut;

	static boolean keepSending;
	static boolean normalout;
	
	public MessageReceiverAndTranslator(BufferedReader in, PrintWriter out, SIPController sipController, Socket connecterSocket) {
		this.out = out;
		this.in = in;
		this.sipController = sipController;
		this.connecterSocket = connecterSocket;
		this.connecterSocketToClose = connecterSocket;
	}
	
	@Override
	public void run() {
		
		keepAliveThread = null;
		
		try {
			String receivedMessage = "";
			
			normalout = false;
			while ((receivedMessage =in.readLine()) != null){
				
				if(!receivedMessage.equals("alive")) {
					String []possibleArguments = receivedMessage.split(" ");

				    if (possibleArguments[0].equals("tro") && possibleArguments.length == 2){
				    	
//				    	if(sipController.currentState instanceof SIPTryCall) {
					        AudioPlayer.port = Integer.parseInt(possibleArguments[1]);
//				    	}
				        receivedMessage = "tro";
				        
				        
	                	keepSending = true;				        
				        (keepAliveThread = new Thread(new Runnable() {			
							@Override
							public void run() {
								while(keepSending) {
									
									try {
										out.println("alive");
										out.flush();
										System.out.println("sent alive");

									}catch (Exception e) {
										sipController.triggerState(receivedMessageToSIPMessage("busy"),out);
										try {
											connecterSocket.close();
										} catch (IOException ioe) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
								    	normalout = true;
								    	keepSending = false;
								    	if(timeOut!=null)
								    		timeOut.interrupt();
								    	
								    	PeerStarter.killConnection();

										break;

									}
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
									}
								}
							}
						})).start();
				        connecterSocket.setSoTimeout(1500);
				    }
				    else if (possibleArguments[0].equals("invite") &&  possibleArguments.length == 3){
//				    	if(sipController.currentState instanceof SIPWaiting) {
				    	
					    	AudioPlayer.port = Integer.parseInt(possibleArguments[2]);
					    	//System.out.println("port of kamil: "+AudioPlayer.port);
					    	AudioPlayer.ipAddress = possibleArguments[1];
					    	//System.out.println("ip of kamil: "+AudioPlayer.ipAddress);

//				    	}
				    	receivedMessage = "invite";
				    	
//	                	try {
				    	
					    	(timeOut =new Thread(new Runnable() {			
								@Override
								public void run() {
									try {
										Thread.sleep(5000);
										
										if(SIPController.currentState instanceof SIPWaiting) {
											
											System.out.println("catch7");
											normalout = true;
									    	keepSending = false;

											if(keepAliveThread!=null)
												keepAliveThread.interrupt();
											
											try {
												connecterSocket.close();
											} catch (IOException e) {
												e.printStackTrace();
											}
											PeerStarter.killConnection();
											
										}

									} catch (InterruptedException e) {
									}
								}
							})).start();

	            			connecterSocket.setSoTimeout(5000);
//	            			System.out.println("we set timeout here");
//	                	}catch (SocketException e) {
//	                		System.out.println("got time out");
//							sipController.triggerState(SIPMessages.RECEIVE_INTERPTION, out);
//						}

	                }else if(receivedMessage.equals("ack")){
	                	keepSending = true;
				        (keepAliveThread = new Thread(new Runnable() {			
							@Override
							public void run() {
								while(keepSending) {
									
									try {
										out.println("alive");
										out.flush();
										System.out.println("sent alive");

									}catch (Exception e) {
										sipController.triggerState(receivedMessageToSIPMessage("busy"),out);
										try {
											connecterSocket.close();
										} catch (IOException ioe) {
											e.printStackTrace();
										}
										
								    	normalout = true;
								    	keepSending = false;
								    	if(timeOut!=null)
								    		timeOut.interrupt();
								    	
								    	PeerStarter.killConnection();
										break;
									}
									
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
									}
								}
							}
						})).start();
				        
				        connecterSocket.setSoTimeout(1500);
	                }
                	//System.out.println("we got: "+receivedMessage);
				    
                	SIPMessages msg = receivedMessageToSIPMessage(receivedMessage);
				    sipController.triggerState(msg,out);
				    
				    if(msg.equals(SIPMessages.RECEIVE_BUSY) || msg.equals(SIPMessages.RECEIVE_BYE) || msg.equals(SIPMessages.RECEIVE_OK) || msg.equals(SIPMessages.RECEIVE_UNKOWN_MESSAGE)) {
				    	normalout = true;
				    	if(keepAliveThread!=null)
			    			keepAliveThread.interrupt();

				    	try {
							connecterSocket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}

				    	break;
				    }
				}else {
                	System.out.println("got alive");
                }
			}
			
	    	keepSending = false;
	    	if(timeOut!=null)
	    		timeOut.interrupt();
			if(keepAliveThread!=null)
				keepAliveThread.interrupt();
			
			if(!normalout) {
				System.out.println("catch8");

				sipController.triggerState(SIPMessages.SEND_BUSY,out);
				try {
					connecterSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
			
		}catch(SocketTimeoutException ste) {
			System.out.println("catch4");
	    	normalout = true;
	    	keepSending = false;
	    	if(timeOut!=null)
	    		timeOut.interrupt();
			if(keepAliveThread!=null)
				keepAliveThread.interrupt();

			sipController.triggerState(SIPMessages.SEND_BUSY,out);
			try {
				connecterSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}catch(SocketException se) {
			System.out.println("catch5");
	    	keepSending = false;
	    	if(timeOut!=null)
	    		timeOut.interrupt();
			if(keepAliveThread!=null)
				keepAliveThread.interrupt();
			try {
				connecterSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch ( NumberFormatException | IOException e) {
			System.out.println("catch6");
	    	normalout = true;
	    	keepSending = false;
	    	if(timeOut!=null)
	    		timeOut.interrupt();
			if(keepAliveThread!=null)
				keepAliveThread.interrupt();
			try {
				connecterSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
//    	keepSending = false;
//		if(keepAliveThread!=null)
//			keepAliveThread.interrupt();
//		sipController.triggerState(SIPMessages.SEND_BUSY,out);
//		try {
//			connecterSocket.close();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}

	}
	
    private static SIPMessages receivedMessageToSIPMessage(String message) {
    	switch (message) {
		case "ack":
			return SIPMessages.RECEIVE_ACK;
		case "ok":
			return SIPMessages.RECEIVE_OK;
		case "invite":
			return SIPMessages.RECEIVE_INVITE;
		case "bye":
			return SIPMessages.RECEIVE_BYE;
		case "tro":
			return SIPMessages.RECEIVE_TRO;
		case "busy":
			return SIPMessages.RECEIVE_BUSY;
		case "interption":
		    return SIPMessages.RECEIVE_INTERPTION;
    	default:
    		return SIPMessages.RECEIVE_UNKOWN_MESSAGE;
		}
    }
    
    static public void closeEveryThing() {

		if(keepAliveThread!=null)
			keepAliveThread.interrupt();
			
		if(connecterSocketToClose!=null)
			try {
				connecterSocketToClose.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    }
}
