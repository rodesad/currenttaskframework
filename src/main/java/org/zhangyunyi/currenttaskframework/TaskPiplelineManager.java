package org.zhangyunyi.currenttaskframework;

import java.util.concurrent.*;
import java.util.logging.*;
import java.util.*;

/**
 * 逐个处理管道中的任务。
 * 核心类。piple中的任务并发执行。采用监听-分发模式，监听一个线程，分发给多个工作线程。
 * 一个管道管理器，对应一个管道，可以扩展一个管道管理器，包括多个管道，即管道保存在
 * 一个集合中。
 * @author 张立新
 * @date 2016-11-12
 */
public class TaskPiplelineManager  {
	//默认的管道
	private TaskPipleline tpl;
	//获取Logger
	private static final Logger log= Logger.getLogger("TaskPiplelineManager");
	//添加作业线程池
	private static final Executor addJobExec = Executors.newFixedThreadPool(ConfigParam.ADDJOBEXEC_THREADPOOL_SIZE);
	private static final Executor jobExec = Executors.newFixedThreadPool(ConfigParam.JOBEXEC_THREADPOOL_SIZE);


	public TaskPiplelineManager(){
	}
	
	public TaskPipleline createPipleline(String piplelineName){
		this.tpl=new TaskPiplelineImpl(piplelineName);
		// 添加结束任务,注意顺序不能变，先加last，再加first
		this.tpl.setEnd(new EndTaskValve("end task"));
		//添加初始化任务
		this.tpl.addTaskValve(new InitTaskValve("initial task"));

		return this.tpl;
	}

	/**
	 * 向管道中输入作业job
	 * 创建一个新的线程，负责添加初始作业。
	 */
	public void addJob(final Object job){
		log.info(getThreadInfo()+"向管道【"+ tpl.getName()+"】：添加作业：【"+job+"】");	
		try{
			Runnable r = new Runnable(){
				public void run(){
					try{
						//第一个任务是initial任务，为内嵌任务。
						tpl.getFirst().getTodoQueue().put(job);
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			};//Runnable
			Thread t = new Thread(r,"向管道中添加作业：【"+job+"】");
			addJobExec.execute(t);
		}catch (Exception e){
			e.printStackTrace();
		}
	} 
	
	/**
	 * 得到作业处理的统计信息。
	 */
	public String getExecStatistics(){
		String rt;
		//管道名称
		rt="作业执行统计信息："+System.getProperty("line.separator")+
			"============================================================"+System.getProperty("line.separator")+
			"管道名称:"+tpl.getName()+System.getProperty("line.separator")+
			"  任务节点数量："+tpl.getTaskValves().size()+System.getProperty("line.separator");

		for(int i=0;i<tpl.getTaskValves().size();i++){
			TaskValve tv = tpl.getTaskValves().get(i);
			//得到指定任务节点(taskvalve)的job执行统计对象，在该对象为hashMap,每个job统计信息为一个jobwrapper，
			ConcurrentHashMap<String,JobWrapper> statistics=tv.getJobExecStatistics();

			//遍历任务节点的作业job，得到成功的作业job数和失败的作业job数量,丢失的作业job数量
			int sucesses=0,falses=0,loses=0;
			
			//任务节点中，执行的异常信息
			String errmsg=""+System.getProperty("line.separator");
			
			for(JobWrapper jw:statistics.values()){
				
				HashMap<ConstantClass.HandleResultKey,Object>  jr=jw.getJobResult();

				if(jr==null || jr.size()==0){
					//如果JobHandler.handle()返回值为null，提示编写handler有误
					log.log(Level.WARNING,"任务的作业处理器(JobHanlder)【"+tv.getJobHandler()+"】的handle()方法的返回值为null，执行异常。");
					log.log(Level.WARNING,"作业job【"+tv.getName()+"-"+jw.getJobName()+"】的错误信息(Error Message)是："+
						jw.getErrorMessage());
						errmsg=errmsg+jw.getErrorMessage()+";"+System.getProperty("line.separator")+
							"   堆栈信息:"+System.getProperty("line.separator")+
							"   "+jw.getErrorStackTraceString()+System.getProperty("line.separator");
						sucesses=0;
						falses=0;
						loses=loses+1;//jobwrapper中的job执行返回结果为空，标记为作业丢失。
				}else{
					switch ((ConstantClass.HandleResultStatusValue) jr.get(ConstantClass.HandleResultKey.STATUS)){
						case SUCESS:
							sucesses=sucesses+1;
							break;
						case FALSE:
							falses=falses+1;
							break;
						default:
							loses=loses+1;
					}//swith
					//读取job执行过程中的异常信息
					if(jr.get(ConstantClass.HandleResultKey.ERRORMESSAGE)!="" || jr.get(ConstantClass.HandleResultKey.ERRORSTACKTRACE)!=null){
						errmsg=errmsg+"作业job【"+tv.getName()+"-"+jw.getJobName()+"】的异常(Exception)："
								+jr.get(ConstantClass.HandleResultKey.ERRORMESSAGE)+";"
								+System.getProperty("line.separator")+
								"   堆栈信息:"+System.getProperty("line.separator")+
								"   "+getErrorStackTraceString((StackTraceElement[])jr.get(ConstantClass.HandleResultKey.ERRORSTACKTRACE))
									+System.getProperty("line.separator");
					}
				}
				
			}//for
			//组装TaskValve的信息
			rt=rt+System.getProperty("line.separator")+
				"  任务节点["+i+"]名称:"+tv.getName()+";"+System.getProperty("line.separator")+
				"  作业数量:"+statistics.size()+";"+System.getProperty("line.separator")+
				"  成功数量:"+sucesses+";"+System.getProperty("line.separator")+
				"  失败数量:"+falses+";"+System.getProperty("line.separator")+
				"  丢失数量:"+loses+";"+System.getProperty("line.separator")+
				"  异常信息:"+errmsg+";"+System.getProperty("line.separator");
				
		}//for
		rt=rt+"============================================================";
		return rt;
	}
	
	//得到异常堆栈字符串
	private String getErrorStackTraceString(StackTraceElement[] ste){
		String rt="";
		if(ste!=null){
			for(int i=0;i<ste.length;i++){
				rt=rt+ste[i].toString()+System.getProperty("line.separator");
		}
		}
		return rt;

	}
	
	/**
	 * 该方法为每个任务建立一个监听线程，监听该任务TSource队列
	 */
	public void  start(){
		log.info(getThreadInfo()+"管道开始执行。");
		
		//得到管道中的第一个任务(taskvalve)
		TaskValve inital = tpl.getInital();
		TaskValve end = tpl.getEnd();
		try{
			if(inital==null){
				log.info(getThreadInfo()+"管道中没有任务执行");
				return;
			}else{
				//第一个任务，本线程处理
				log.info(getThreadInfo()+"管道中初始任务节点【"+inital.getName()+"】"+"开始处理。");
				inital.handle();
				log.info(getThreadInfo()+"管道中初始任务节点【"+inital.getName()+"】"+"处理完成。");

				//遍历整个任务队列
				TaskValve current = inital;
				while(current.getNext()!=null){
					log.info(getThreadInfo()+"开始遍历管道的任务节点taskvalve");
					if(current.getNext()==end){
						//最后一个任务，本线程处理
						current=end;
						log.info(getThreadInfo()+"管道中end任务节点【"+current.getName()+"】");
							
						current.handle();  
						
						log.info(getThreadInfo()+"管道中end任务节点【"+current.getName()+"】"+"：处理完成。");
						break;
					}else{
						current=current.getNext();
					}

					//监听本任务节点。
					current.listen();
				}
			}
			log.info(getThreadInfo()+"管道中的任务节点遍历完成。");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void close(){
		log.info(getThreadInfo()+"管道：关闭。");
	}
	private String getThreadInfo(){
		return "线程号："+Thread.currentThread().getId()+",线程名："+Thread.currentThread().getName()+"。";
	}
	
}