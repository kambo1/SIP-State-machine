package controller;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import states.SIPMessages;

public class PeerStarter {
	
	public static void main(String[] args) {
		new PeerStarter();
	}
	
	PrintWriter outForTerminal;

	public PeerStarter(){
		Semaphores.semToSysIn = new Semaphore(0);
		
		SIPController sipController = new SIPController();
        new Thread(new Listener(sipController)).start();
        new Thread(new Connecter(sipController)).start();
    }
    
    private class Listener implements Runnable {
        private SIPController sipController;
        public Listener (SIPController sipController){
        	this.sipController = sipController;
        }
        @Override
        public void run() {

            ServerSocket listenerSocket = null;
			try {
				listenerSocket = new ServerSocket(5061);
			} catch (IOException e1) {
				System.out.println("port is busy");
				System.exit(0);
			}

            while (true){
            	Socket connecterSocket = null;
                try {
                	connecterSocket = listenerSocket.accept();
                    

                	PrintWriter out = new PrintWriter(connecterSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(connecterSocket.getInputStream()));
                                        
                    if(!sipController.currentState.busy()) {
                		AudioPlayer.init();
                    	connecterSocket.setSoTimeout(5000);
                    	establishConnection(sipController, out, in,connecterSocket);
                    	
                    }else {
                		Semaphores.semToSysIn = new Semaphore(0);
                    	sipController.triggerState(SIPMessages.RECEIVE_INTERPTION, out);
                    	try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
                    }
                    
                } catch (IOException e) {
                	
                	AudioPlayer.close();
                	
					try {
						connecterSocket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					e.printStackTrace();
                	
                }
            }
        }
    }
    
    
    public class Connecter implements Runnable {

        private SIPController sipController;
        
        public Connecter(SIPController sipController){    	
        	this.sipController = sipController;
        }

        @Override
        public void run() {
        	System.out.println();
            System.out.print("Connecter is waiting for input: ");
            Scanner scanner = new Scanner(System.in);

            String commandFromUser = "";
            PrintWriter out = null;
            BufferedReader in = null;

            
            while(true) {
            	commandFromUser = scanner.nextLine();
            	String[] possibleCommands = commandFromUser.split(" ");
            	Socket connecterSocket = null;
            	try {
	            	if (possibleCommands[0].equals("invite") && possibleCommands.length == 3){
	            		
	                    AudioPlayer.init();
	            		
            			String ipAddress = possibleCommands[1];
	            		int listenerSocPort = Integer.parseInt(possibleCommands[2]);
	            		
	            		AudioPlayer.ipAddress = ipAddress;
	            		
	                    connecterSocket = new Socket( possibleCommands[1], Integer.parseInt(possibleCommands[2]));
	                    
	                    out = new PrintWriter(connecterSocket.getOutputStream(), true);
	                    in = new BufferedReader(new InputStreamReader(connecterSocket.getInputStream()));
	                    
	                    sipController.triggerState(SIPMessages.SEND_INVITE,out);
	                    
	                    connecterSocket.setSoTimeout(5000);


	                    establishConnection(sipController, out, in,connecterSocket);
	            		
	
	            	}else if (commandFromUser.equals("bye")) {
	                    sipController.triggerState(SIPMessages.SEND_BYE,outForTerminal);
	                }else if(commandFromUser.equals("tro")) {
	                	sipController.currentState.answerOfCall = "tro";
	                	Semaphores.semToSysIn.release();
	                }else if(commandFromUser.equals("busy")) {
	                	sipController.currentState.answerOfCall = "busy";
                		Semaphores.semToSysIn.release();
	                }
        		}catch(SocketTimeoutException ste) {
        			System.out.println("catched1");
        			sipController.triggerState(SIPMessages.RECEIVE_INTERPTION,out);            			
        			AudioPlayer.close();
        			
					try {
						if(connecterSocket!=null)
							connecterSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

//					if(keepingConnection!=null)
//						keepingConnection.interrupt();
					MessageReceiverAndTranslator.closeEveryThing();

        		}catch(SocketException e) {
        			System.out.println("catched2");

        			sipController.triggerState(SIPMessages.RECEIVE_INTERPTION,out);
        			AudioPlayer.close();
        			
					try {
						if(connecterSocket!=null)
							connecterSocket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

//					if(keepingConnection!=null)
//						keepingConnection.interrupt();
					MessageReceiverAndTranslator.closeEveryThing();

        		}catch (IOException e) {
        			System.out.println("catched3");
        			sipController.triggerState(SIPMessages.RECEIVE_INTERPTION,out);

        			AudioPlayer.close();
        			
					try {
						if(connecterSocket!=null)
							connecterSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
//					if(keepingConnection!=null)
//						keepingConnection.interrupt();
					MessageReceiverAndTranslator.closeEveryThing();
				}        		            	
    		}
        }
    }
    
    static void killConnection() {
    	if(threadForMsgRecAndTrans!=null) {
    		threadForMsgRecAndTrans.interrupt();
    	}
    }
    static Thread threadForMsgRecAndTrans;
    private void establishConnection(SIPController sipController, PrintWriter out, BufferedReader in,Socket connecterSocket) {
        outForTerminal = out;
        
        MessageReceiverAndTranslator msgRecAndTrans = new MessageReceiverAndTranslator(in, out, sipController, connecterSocket);
        threadForMsgRecAndTrans = new Thread(msgRecAndTrans);
        threadForMsgRecAndTrans.start();        
        
        
//    	keepingConnection = new Thread(new Runnable() {
//    		@Override
//			public void run() {	
//				try {
//					Semaphores.semToKillRecveier.acquire();
//					Semaphores.semToKillRecveier = new Semaphore(0);
//					
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}finally {
//					if(threadForMsgRecAndTrans!=null)
//						threadForMsgRecAndTrans.interrupt();
//			    	AudioPlayer.close();
//					try {
//						connecterSocket.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}});
//		keepingConnection.start();
    }
    
//    Thread keepingConnection;
}
