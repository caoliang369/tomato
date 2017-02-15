package org.tomato.daily.io.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;


public class AsyncServerHandler implements Runnable {
	
	private CountDownLatch countDownLatch;
	private AsynchronousServerSocketChannel asynchronousServerSocketChannel;
	
	public AsyncServerHandler(int port){
		try {
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
			System.out.println("Server start!port:"+port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		countDownLatch = new CountDownLatch(1);
		// accept handler
		asynchronousServerSocketChannel.accept(this, new CompletionHandler<AsynchronousSocketChannel, AsyncServerHandler>() {

			public void completed(AsynchronousSocketChannel channel, AsyncServerHandler serverHandler) {
				// continue accept other client request
				AioServer.clientCount++;
				System.out.println("Connected client count:"+AioServer.clientCount);
				serverHandler.asynchronousServerSocketChannel.accept(serverHandler, this);
				
				// handle this request
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				// read from channel
				channel.read(buffer, buffer, new ServerReadHandler(channel));
			}

			public void failed(Throwable exc, AsyncServerHandler attachment) {
				exc.printStackTrace();
				attachment.countDownLatch.countDown();
			}
		});
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
