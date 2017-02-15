package org.tomato.daily.io.aio;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class AioTest {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		AioServer.start();
		TimeUnit.SECONDS.sleep(1L);
		AioClient.start();
		while(AioClient.sendMsg(new Scanner(System.in).nextLine()));
	}

}
