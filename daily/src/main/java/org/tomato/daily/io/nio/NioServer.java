package org.tomato.daily.io.nio;

public class NioServer {
	
	public static int PORT = 23456;
	private static ServerHandler serverHandler;
	
	public static void main(String[] args) {
		start();
	}

	public static synchronized void start(){
		if(serverHandler != null){
			serverHandler.stop();
		}
		serverHandler = new ServerHandler(PORT);
		new Thread(serverHandler).start();
	}
}
