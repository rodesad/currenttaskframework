package org.zhangyunyi.currenttaskframework;

import java.util.concurrent.*;
import java.lang.*;
import java.util.*;
import java.util.logging.*;


/**
 * 实际任务处理类，实现TaskHandle接口；根据队列中作业的类型，定义handle
 * 参数类型。
 */
 public class LastTaskHandler implements JobHandler{
	//获取Logger
	private static final Logger log= Logger.getLogger("LastTaskHandler");
	private final String handlerName="LastTaskHandler";
	//执行过程中错误信息
	private String errMsg;
	
	public String getName(){
		 return this.handlerName;
	 }

	 //处理结果
	public HashMap<ConstantClass.HandleResultKey,Object> handle(Object job,BlockingQueue reslutQueue){
		
		log.info(getThreadInfo()+"[handle] 进入【"+handlerName+"】;"+ this.getClass().getName());
		String errMsg="";
		StackTraceElement[] stackTrace=null;
		//测试，随机将数据添加到队列中。
		try{
			while(true){
				Integer i = new Integer(job.toString());
				log.info(getThreadInfo()+"【"+handlerName+"】;处理结果："+i);
				break;
			}
		}catch(Exception e){
			errMsg=e.getMessage();
			stackTrace=e.getStackTrace();
			e.printStackTrace();
		}
		//将执行的统计信息保存hashmap中，返回
		HashMap<ConstantClass.HandleResultKey,Object> rtMap= new HashMap<ConstantClass.HandleResultKey,Object>();
		//执行结果
		rtMap.put(ConstantClass.HandleResultKey.STATUS,ConstantClass.HandleResultStatusValue.SUCESS);
		rtMap.put(ConstantClass.HandleResultKey.ERRORMESSAGE,errMsg);
		rtMap.put(ConstantClass.HandleResultKey.RESULTSIZE,"1");
		rtMap.put(ConstantClass.HandleResultKey.ERRORMESSAGE,errMsg);
		if(stackTrace!=null){
			rtMap.put(ConstantClass.HandleResultKey.ERRORSTACKTRACE,stackTrace);
		}
		return rtMap;
	 }
 	private String getThreadInfo(){
		return "线程号："+Thread.currentThread().getId()+",线程名："+Thread.currentThread().getName()+"。";
	}
}
