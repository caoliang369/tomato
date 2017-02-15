package org.tomato.daily.io.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class ClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel channel;
	private CountDownLatch latch;

	public ClientReadHandler(AsynchronousSocketChannel channel, CountDownLatch latch) {
		this.channel = channel;
		this.latch = latch;
	}

	public void completed(Integer result, ByteBuffer buffer) {
		buffer.flip();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		String msg = null;
		try {
			msg = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("client receive msg from server:" + msg);
	}

	public void failed(Throwable exc, ByteBuffer buffer) {
		System.err.println("数据读取失败...");
		try {
			channel.close();
			latch.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
