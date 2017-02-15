package org.tomato.daily.io.nio;


import java.io.IOException;

public class NioClient {
	
	private static String HOST = "127.0.0.1";
	private static int PORT = NioServer.PORT;
	private static ClientHandler clientHandler;
	
	public static void main(String[] args) {
		start();
	}
	
	public static synchronized void start(){
		if(clientHandler != null){
			clientHandler.stop();
		}
		clientHandler = new ClientHandler(HOST, PORT);
		new Thread(clientHandler).start();
	}
	
	public static boolean sendMsg(String msg) throws IOException{
		clientHandler.sendMsg(msg);
		return true;
	}
}
