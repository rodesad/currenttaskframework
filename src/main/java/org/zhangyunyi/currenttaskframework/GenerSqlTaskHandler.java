package org.zhangyunyi.currenttaskframework;

import java.util.concurrent.*;
import java.util.*;
import java.util.logging.*;
/**
 * 实际任务处理类，实现TaskHandle接口
 */
 public class GenerSqlTaskHandler implements JobHandler{
	//获取Logger
	private static final Logger log= Logger.getLogger("TaskPiplelineManager");
	 private final String handlerName="生成SQL脚本handler";
	 public String getName(){
		 return this.handlerName;
	 }
	 public HashMap<ConstantClass.HandleResultKey,Object> handle(Object job,BlockingQueue reslutQueue){
		String errMsg="";
		StackTraceElement[] stackTrace=null;

		log.info(getThreadInfo()+"[handle] 进入【"+handlerName+"】;"+ this.getClass().getName());
		 //从队列中取数据，每次加1。
		 try{
			 Thread.sleep((new Random()).nextInt(1000));
			 int rt=Integer.parseInt(job.toString())+1;
			 reslutQueue.put(rt);
				log.info(getThreadInfo()+"【"+handlerName+"】;处理结果："+rt);
				
//			throw new RuntimeException("执行异常");
		 }catch(InterruptedException e){
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
