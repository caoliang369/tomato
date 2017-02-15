package org.tomato.daily.io.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class ClientWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
	
	private CountDownLatch latch;
	private AsynchronousSocketChannel channel;
	
	public ClientWriteHandler(AsynchronousSocketChannel channel, CountDownLatch latch){
		this.latch =latch;
		this.channel = channel;
	}
	
	public void completed(Integer result, ByteBuffer buffer) {
		if(buffer.hasRemaining()){
			channel.write(buffer, buffer, this);
		}else{
			ByteBuffer readBuffer = ByteBuffer.allocate(1024);
			channel.read(readBuffer, readBuffer, new ClientReadHandler(channel, latch));
		}
	}

	public void failed(Throwable exc, ByteBuffer buffer) {
		System.err.println("数据发送失败...");  
        try {  
        	channel.close();  
            latch.countDown();  
        } catch (IOException e) {  
        }  
	}

}
