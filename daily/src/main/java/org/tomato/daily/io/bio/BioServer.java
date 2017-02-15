package org.tomato.daily.io.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class BioServer {
	private static int PORT = 12345;
	private static ServerSocket serverSocket;
	
	public synchronized static void start() throws IOException{
		if(serverSocket != null){
			return;
		}
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("server start! port="+PORT);
			Socket socket;
			while (true) {
				socket = serverSocket.accept();
				new Thread(new ServerHandler(socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(!serverSocket.isClosed()){
				serverSocket.close();
				System.out.println("server closed!");
			}
		}
		
	}
}
