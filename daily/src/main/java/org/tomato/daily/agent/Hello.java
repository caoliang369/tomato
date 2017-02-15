package org.tomato.daily.agent;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

@MyAgentAnnotation
public class Hello {
	public void hello1(String a){
		System.out.println("hello1");
		try {
			TimeUnit.SECONDS.sleep(2L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void hello2(String b){
		System.out.println("hello2");
		try {
			TimeUnit.SECONDS.sleep(5L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		DateTime dateTime = new DateTime(new Date());
		System.out.println(dateTime.toString());
		System.out.println(dateTime.plusWeeks(1).toDate());
		
	}
}
