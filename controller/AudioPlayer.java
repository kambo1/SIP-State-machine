package controller;

import java.io.IOException;
import java.net.InetAddress;

public class AudioPlayer{
	public static AudioStreamUDP audioStreamUDP;
	public static int port;
	public static String ipAddress;
	
	public static int my_port;
	public static String my_ipAddress;

    
	static void init() {
    	try {
			audioStreamUDP = new AudioStreamUDP();

			my_port = audioStreamUDP.getLocalPort();
			
			my_ipAddress = InetAddress.getLocalHost().getHostAddress();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void close() {
		if(audioStreamUDP!=null) {
			audioStreamUDP.stopStreaming();
			audioStreamUDP.close();	
		}		
		init();
	}
	
	public static void startPlayer() {
		try {
			audioStreamUDP.connectTo(InetAddress.getByName(ipAddress), port);
			audioStreamUDP.startStreaming();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
