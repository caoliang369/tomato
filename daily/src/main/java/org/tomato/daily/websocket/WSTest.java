package org.tomato.daily.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WSTest {
	final static CountDownLatch messageLatch = new CountDownLatch(1);
	public static void main(String[] args) {
		try {
//			for(int i=0;i<10000;i++){
//			String token = EncryptUtils.wsEncode("6666", "vzhibo_2", 1);
			String token = "123";
			// open websocket
			WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(
					new URI("ws://test-txcc.ctest.baijiahulian.com/ws/tx-normal?user_token="+token));

			// add listener
			clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
				public void handleMessage(String message) {
					System.out.println(message);
				}
			});
//			}

			// send message to websocket
			
			while(true){
				// wait 5 seconds for messages from websocket
				clientEndPoint.sendMessage("{\"type\":\"ping\"}");
				System.out.println("{\"type\":\"ping\"}");
				messageLatch.await(15, TimeUnit.SECONDS);
			}
			

		} catch (InterruptedException ex) {
			System.err.println("InterruptedException exception: " + ex.getMessage());
		} catch (URISyntaxException ex) {
			System.err.println("URISyntaxException exception: " + ex.getMessage());
		} catch (Exception e){
			System.err.println(e.getMessage());
		}
	}
}
