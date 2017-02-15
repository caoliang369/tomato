package org.tomato.daily.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyCompletionService implements Callable<String> {

	private int id;
	
	public MyCompletionService( int id){
		this.id = id;
	}
	
	public String call() throws Exception {
		long time = (long) (Math.random()*100);
		System.out.println("id:["+this.id+"] start");
		TimeUnit.SECONDS.sleep(time);
		System.out.println("id:["+this.id+"] end");
		return "id:["+this.id+"] sleep "+time+" seconds";
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newCachedThreadPool();
		CompletionService<String> completionService = new ExecutorCompletionService<String>(executorService);
		for(int i=1;i<11;i++){
			completionService.submit(new MyCompletionService(i));
		}
		for(int i=1;i<11;i++){
			System.out.println(completionService.take().get());
		}
		executorService.shutdown();
	}
}
