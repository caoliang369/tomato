package org.tomato.daily.io.bio;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BioTest {

	public static void main(String[] args) throws IOException, InterruptedException {
		new Thread(new Runnable() {
			
			public void run() {
				try {
					BioServer.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();;
		TimeUnit.MILLISECONDS.sleep(500L);
		new Thread(new Runnable() {
			
			public void run() {
				for(int i=0;i<10;i++){
					BioClient.send("test"+i);
				}
			}
		}).start();
		CountDownLatch latch = new CountDownLatch(1);
		latch.await();
	}

}
