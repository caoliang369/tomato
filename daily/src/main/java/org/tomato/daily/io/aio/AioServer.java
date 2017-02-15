package org.tomato.daily.io.aio;


public class AioServer {
	public static final int PORT = 34567;
	private static AsyncServerHandler asyncServerHandler;
	public volatile static int clientCount = 0;
	
	public static synchronized void start(){
		if(asyncServerHandler != null){
			return;
		}
		asyncServerHandler = new AsyncServerHandler(PORT);
		new Thread(asyncServerHandler).start();
	}
}
