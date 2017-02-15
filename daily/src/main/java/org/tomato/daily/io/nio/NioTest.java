package org.tomato.daily.io.nio;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NioTest {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException, IOException {
		NioServer.start();
		TimeUnit.SECONDS.sleep(1L);
		NioClient.start();
		TimeUnit.SECONDS.sleep(1L);
		while(NioClient.sendMsg(new Scanner(System.in).nextLine()));
	}
}
