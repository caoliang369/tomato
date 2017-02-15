package org.tomato.daily.io.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerReadHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel channel;

	ServerReadHandler(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}

	public void completed(Integer result, ByteBuffer buffer) {
		buffer.flip();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		try {
			String message = new String(bytes,"UTF-8");
			System.out.println("server receive message:" + message);
			// send message to client
			doWrite("Server hahaha!");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void failed(Throwable exc, ByteBuffer attachment) {
		exc.printStackTrace();
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doWrite(String message) {
		byte[] bytes = message.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		channel.write(buffer, buffer, new ServerWriteHandler(channel));
	}

}
