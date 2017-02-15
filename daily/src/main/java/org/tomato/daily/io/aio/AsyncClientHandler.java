package org.tomato.daily.io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsyncClientHandler implements Runnable {

	private String host;
	private int port;
	private AsynchronousSocketChannel asynchronousSocketChannel;
	private CountDownLatch countDownLatch;

	public AsyncClientHandler(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() {
		try {
			asynchronousSocketChannel = AsynchronousSocketChannel.open();
			asynchronousSocketChannel.connect(new InetSocketAddress(host, port), this,
					new CompletionHandler<Void, AsyncClientHandler>() {

						public void completed(Void result, AsyncClientHandler attachment) {
							System.out.println("after three handshake! success connect server!");
						}

						public void failed(Throwable exc, AsyncClientHandler attachment) {
							exc.printStackTrace();
							try {
								asynchronousSocketChannel.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
			// 监听handler做countDown，控制结束
			countDownLatch = new CountDownLatch(1);
			countDownLatch.await();
			asynchronousSocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	public void sendMsg(String msg){
		byte[] bytes = msg.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.put(bytes);
		buffer.flip();
		asynchronousSocketChannel.write(buffer, buffer, new ClientWriteHandler(asynchronousSocketChannel, countDownLatch));
	}

}
