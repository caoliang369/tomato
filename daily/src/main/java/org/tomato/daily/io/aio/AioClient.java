package org.tomato.daily.io.aio;


public class AioClient {
	private static String HOST = "127.0.0.1";
	private static int PORT = AioServer.PORT;
	private static AsyncClientHandler asyncClientHandler;
	
	public static synchronized void start(){
		if(asyncClientHandler != null){
			return;
		}
		asyncClientHandler = new AsyncClientHandler(HOST, PORT);
		new Thread(asyncClientHandler).start();
	}
	
	public static boolean sendMsg(String msg){
		if(msg.equals("q")){
			return false;
		}
		asyncClientHandler.sendMsg(msg);
		return true;
	}
}
