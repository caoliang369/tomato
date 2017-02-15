package org.tomato.daily.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerHandler implements Runnable {
	
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private volatile boolean started;
//  打开ServerSocketChannel，监听客户端连接
//  绑定监听端口，设置连接为非阻塞模式
//  创建Reactor线程，创建多路复用器并启动线程
//  将ServerSocketChannel注册到Reactor线程中的Selector上，监听ACCEPT事件
//  Selector轮询准备就绪的key
//  Selector监听到新的客户端接入，处理新的接入请求，完成TCP三次握手，简历物理链路
//  设置客户端链路为非阻塞模式
//  将新接入的客户端连接注册到Reactor线程的Selector上，监听读操作，读取客户端发送的网络消息
//  异步读取客户端消息到缓冲区
//  对Buffer编解码，处理半包消息，将解码成功的消息封装成Task
//  将应答消息编码为Buffer，调用SocketChannel的write将消息异步发送给客户端
	public ServerHandler(int port){
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			started = true;
			System.out.println("Server start port:"+port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public void stop(){
		started = false;
	}
	
	public void run() {
		while(started){
			try {
				selector.select(1000L);
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				SelectionKey key = null;
				while(it.hasNext()){
					key = it.next();
					it.remove();
					try{
						handleInput(key);
					}catch(Exception e){
						if(key != null){
							key.cancel();
							if(key.channel() != null){
								key.channel().close();
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		if(selector != null){
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleInput(SelectionKey key) throws IOException {
		if(key.isValid()){
			// new coming
			if(key.isAcceptable()){
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
				SocketChannel socketChannel = serverSocketChannel.accept();
				socketChannel.configureBlocking(false);
				socketChannel.register(selector, SelectionKey.OP_READ);
			}
			if(key.isReadable()){
				SocketChannel socketChannel = (SocketChannel) key.channel();
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				int readBytes = socketChannel.read(buffer);
				if(readBytes > 0){
					buffer.flip();
					byte[] bytes = new byte[buffer.remaining()];
					buffer.get(bytes);
					String message = new String(bytes,"UTF-8");
					System.out.println("Server receive message:"+message);
					replyClient(socketChannel, message+" has been read!");
				}
//				key.cancel();
//				socketChannel.close();
			}
		}
	}

	private void replyClient(SocketChannel socketChannel, String message) throws IOException {
		byte[] bytes = message.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		socketChannel.write(buffer);
	}

}
