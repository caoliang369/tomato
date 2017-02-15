package org.tomato.daily.agent;

import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class MyTransformer implements ClassFileTransformer {

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		className = className.replace("/", ".");
		if(!className.contains("Hello")){
			return null;
		}
		try {
			CtClass ctClass = ClassPool.getDefault().get(className);
//			Class<?> clazz = ctClass.toClass();
//			Annotation[] anns =  clazz.getAnnotations();
			Object anns = ctClass.getAnnotation(MyAgentAnnotation.class);
			System.out.println("className:"+className);
			if(anns!=null){
//			for(int i=0;i<anns.length;i++){
//				Annotation annotation = (Annotation) anns[i];
//				System.out.println("是不是我的："+(annotation.getClass()));
//				System.out.println("anns:"+anns[i]);
//			}
				Annotation annotation = (Annotation) anns;
				System.out.println(annotation.toString());
			}
				// 开搞
				for (CtMethod method : ctClass.getMethods()) {
					if(!method.toString().contains("hello")){
						continue;
					}
					System.out.println("method:"+method.getName());
					String outputStr = "\nSystem.out.println(\"this method " + method.getName()
							+ " cost:\" +(endTime - startTime) +\"ms.\");";
					String originName = method.getName();
					String newMethodName = method.getName() + "$old";// 新定义一个方法叫做比如***$old
					method.setName(newMethodName);// 将原来的方法名字修改
					// 创建新的方法，复制原来的方法，名字为原来的名字
					CtMethod newMethod = CtNewMethod.copy(method, originName, ctClass, null);
					StringBuilder bodyStr = new StringBuilder();
					bodyStr.append("{");
					bodyStr.append("\nlong startTime = System.currentTimeMillis();\n");
					bodyStr.append(newMethodName + "($$);\n");
					bodyStr.append("\nlong endTime = System.currentTimeMillis();\n");
					bodyStr.append(outputStr);
					bodyStr.append("}");

					newMethod.setBody(bodyStr.toString());// 替换老方法
					ctClass.addMethod(newMethod);// 增加新方法

				}
				return ctClass.toBytecode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
