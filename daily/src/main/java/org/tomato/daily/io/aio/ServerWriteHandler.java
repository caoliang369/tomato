package org.tomato.daily.io.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
	
	private AsynchronousSocketChannel channel;
	
	public ServerWriteHandler(AsynchronousSocketChannel channel){
		this.channel = channel;
	}
	
	public void completed(Integer result, ByteBuffer buffer) {
		// send whole message
		if (buffer.hasRemaining()) {
			channel.read(buffer, buffer, new ServerReadHandler(channel));
		} else {
			// read from client if necessary
			ByteBuffer readBuffer = ByteBuffer.allocate(1024);
			channel.read(readBuffer, readBuffer, new ServerReadHandler(channel));
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

}
