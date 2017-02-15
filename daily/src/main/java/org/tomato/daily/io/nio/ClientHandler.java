package org.tomato.daily.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ClientHandler implements Runnable {

	private String host;
	private int port;
	private Selector selector;
	private SocketChannel socketChannel;
	private volatile boolean started;

	public ClientHandler(String ip, int port) {
		this.port = port;
		this.host = ip;
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			started = true;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	public void stop() {
		started = false;
	}

	private void doConnect() throws IOException {
		if (socketChannel.connect(new InetSocketAddress(host, port))) {
			// do nothing
		} else {
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}
	
	public void sendMsg(String msg) throws IOException{
		socketChannel.register(selector, SelectionKey.OP_READ);
		doWrite(socketChannel, msg);
	}
	

	private void doWrite(SocketChannel socketChannel, String msg) throws IOException {
		byte[] bytes = msg.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		socketChannel.write(buffer);
	}

	public void run() {
		try {
			doConnect();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		while(started){
			try {
				selector.select(1000L);
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();
				while(iterator.hasNext()){
					SelectionKey key = iterator.next();
					iterator.remove();
					try{
						handleInput(key);
					} catch(Exception e){
						e.printStackTrace();
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
			if(key.isConnectable()){
				if(socketChannel.finishConnect()){
					
				}else{
					System.exit(1);
				}
			}
			if(key.isReadable()){
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				int readBytes = socketChannel.read(buffer);
				if(readBytes>0){
					buffer.flip();
					byte[] bytes = new byte[buffer.remaining()];
					buffer.get(bytes);
					String msg = new String(bytes,"UTF-8");
					System.out.println("Client receive response from server!msg:"+msg);
				}
//				key.cancel();
//				socketChannel.close();
			}
		}
	}

}
