package org.tomato.daily.agent;

import java.lang.instrument.Instrumentation;


public class MyAgent {
	public static void premain(String args, Instrumentation inst){
		inst.addTransformer(new MyTransformer());
	}
}
