package org.zhangyunyi.currenttaskframework;

import java.util.concurrent.*;
import java.util.logging.*;
import java.util.*;
import java.rmi.server.*;

/**
 * 并发任务，泛型<TTodoJob,TFinishedJob>表示该任务处理的对象类型，和阻塞队列里的类型相同。
 * TSource表示待处理队列的中的元素类型;TFinishedJob表示处理完成队列中的元素类型。
 * 实现了TaskHanlder接口，将task的处理委托给TaskHanlder.
 * 实现了TaskValve和TaskHanlder两者的结合。
 */
public class BlockingTaskValve<TTodoJob,TFinishedJob> implements TaskValve<TTodoJob,TFinishedJob> {
	
	//获取Logger
	private static Logger log= Logger.getLogger("BlockingTaskValve");

	protected TaskValve next = null;
	//任务名称
	private String taskName;
	
	//实际的任务处理对象
	protected JobHandler  jobHandler;
	
	//包含三个队列，类型BlockingQueue
	//消费者队列,最大Integer.MAX_Value 2^31-1
	//通过配置文件配置队列大小
	private BlockingQueue<TTodoJob> todoQueue=new LinkedBlockingQueue<TTodoJob>(ConfigParam.TODOQUEUE_LENGTH);

	private BlockingQueue<TFinishedJob> finishedQueue = new LinkedBlockingQueue<TFinishedJob>(ConfigParam.FINISHEDQUEUE_LENGTH);

	private BlockingQueue<TFinishedJob> failedQueue = new LinkedBlockingQueue<TFinishedJob> (ConfigParam.FAILEDQUEUE_LENGTH) ;
	
	//JobHandler执行返回的结果信息，保存在wrapper中，注意wrapper中不要有job的引用，否则不能释放内存。
	private ConcurrentHashMap<String,JobWrapper> jobExecStatistics = new ConcurrentHashMap<String,JobWrapper>();

	// job执行线程池，初始化线程池，和线程池大小，配置文件中设置初始值，可以动态调整大小。
	private final Executor jobExec = Executors.newFixedThreadPool(ConfigParam.JOBEXEC_THREADPOOL_SIZE);

	
	
	public BlockingTaskValve(){
	}

	/**
	 * 处理器实例，任务名称。
	 */
	public BlockingTaskValve(String taskName,JobHandler handler){
		this.jobHandler=handler;
		this.taskName=taskName;
	}
	
	public TaskValve getNext() {
		return next;
	}
	public JobHandler getJobHandler(){
		return this.jobHandler;
	}
	public void setJobHandler(JobHandler handler){
		this.jobHandler=handler;
	}
	public String getName(){
		return "task name：【"+this.taskName+"】";
	}
	public void setNext(TaskValve valve) {
		this.next = valve;
		//将前一个队列的finished作为后一个队列的todoqueue
		valve.setTodoQueue(this.finishedQueue);
	}
	
	public BlockingQueue<TTodoJob> getTodoQueue(){
		return todoQueue;
	};
	public void setTodoQueue(BlockingQueue<TTodoJob> todoQueue){
		this.todoQueue=todoQueue;
	};
	public BlockingQueue<TFinishedJob>getFinishedQueue(){
		return finishedQueue;
	};
	public BlockingQueue<TFinishedJob> getFailedQueue(){
		return failedQueue;
	};

	//得到作业job的执行情况
	public ConcurrentHashMap<String,JobWrapper> getJobExecStatistics(){
		return this.jobExecStatistics;
	};

	public void handle(){
		log.info(getThreadInfo()+"【"+this.taskName+"】处理。");

	};

	/**
	 * TaskVavle任务节点处理,记录执行信息。
	 */
	public void handle(TTodoJob e){
		log.info(getThreadInfo()+"Task:【"+this.taskName+"】的任务【"+e+"】：开始处理；"+
			"处理器:【"+this.jobHandler.getName()+"】；");
		jobHandler.handle(e,(BlockingQueue<TFinishedJob>)this.finishedQueue);
	};

	//监听todoQueue
	public void listen(){
		log.info(getThreadInfo()+"任务节点【"+this.taskName+"】：准备启动监听线程。");
					
		//从任务中获取处理的线程，分配给新的线程处理,该线程监听TaskValve节点。
		Runnable r = new TaskVavleListenRunnable("监听线程："+this.taskName);
		Thread  t= new Thread(r,"【"+this.taskName+"】监听线程");
		t.start();
					
	}
	private String getThreadInfo(){
		return "线程号："+Thread.currentThread().getId()+",线程名："+Thread.currentThread().getName()+"。";
	}

//内部监听类
class TaskVavleListenRunnable implements Runnable{
	
	//获取Logger
	private  Logger log= Logger.getLogger("TaskVavleListenRunnable");
	private String threadName;

	TaskVavleListenRunnable(final String threadName){
		this.threadName=threadName;
	}
	
	public void run(){
		log.info(getThreadInfo()+"【"+threadName+"】监听线程:开始启动。");
		//任务监听和分发
		//作业id
		String jobId=null;
		JobWrapper jobWrapper=null;
		try {
			while(true){
				jobId=(new UID()).toString();
				jobWrapper=new JobWrapper(jobId);
				
				//执行处理TaskHandler的线程
				log.info(getThreadInfo()+"【"+BlockingTaskValve.this.taskName+"】todoQueue待处理作业个数："
					+BlockingTaskValve.this.todoQueue.size());
					
				if( BlockingTaskValve.this.todoQueue.size()==0){
					log.info(getThreadInfo()+"待处理作业【"+BlockingTaskValve.this.todoQueue.size()+"】个(0个)被阻塞");
				}
				
				//如果是null，阻塞
				final Object e = BlockingTaskValve.this.todoQueue.take();

				//初始化jobwrapper，放入jobwrrappers集合中
				//保存执行结果统计数据

				String jobName=BlockingTaskValve.this.taskName+"-"+e.toString();
				jobWrapper.setJobName(jobName);
				jobExecStatistics.put(jobId,jobWrapper);
				
				//非空，调用jobexecutor执行作业
				JobRunnable jr= new JobRunnable(jobId,e);
				
				Thread t = new Thread(jr);

				jobExec.execute(t);
				
			}//while
		}catch(Exception e){
			if(jobWrapper!=null){
				jobWrapper.setErrorMessage("作业执行异常。jobId:【"+jobWrapper.getJobId()+"】 jobName:【"+jobWrapper.getJobName()
					+"】，异常信息为:"+e.getMessage());
				jobWrapper.setErrorStackTrace(e.getStackTrace());
			}
			e.printStackTrace();
		}
		log.info(getThreadInfo()+"【"+threadName+"】监听线程:结束。");
	}//run
	
	private String getThreadInfo(){
		return "线程号："+Thread.currentThread().getId()+",线程名："+Thread.currentThread().getName()+"。";
	}
}//inner class	

/**
 * 作业（job）执行者，将作业分配给一个新的线程执行。委托给Excutors实现。
 * 内部类，之所有采用内部类设计，执行的task队列中的元素，结果也放入task的队列中，
 * 减少复杂度，
 * @author 张立新
 * @date 2016-11-7
 */
class JobRunnable<TTodoJob> implements Runnable {
	//获取Logger
	private  Logger log= Logger.getLogger("JobRunnable");

	//job id
	private String jobId;
	//job
	private final TTodoJob job;
	// hanlder执行的错误信息
	private String errorMessage;
	// 异常堆栈信息
	private StackTraceElement[] stackTrace;
	
	//待处理的作业job，处理该作业的handler：JobHandler,e处理成功处理完毕后，设置为null，垃圾回收进行回收。
	JobRunnable(String jobId,TTodoJob job){
		this.job=job; 
		this.jobId=jobId;
	}
	
	public void run(){

		log.info(getThreadInfo()+"作业【"+job+"】:开始处理。");
		
		HashMap rtMap=null;
		//执行的统计信息，保存到Jwrappers集合中。
		//从jobExecStatistics得到一个作业的JobWrapper，将执行结果，写入JobWrapper
		JobWrapper jw=BlockingTaskValve.this.jobExecStatistics.get(jobId);

		try{
			
			//委托handler执行
			//处理任务，处理结果放到finishedQueue中
			try{
				rtMap=BlockingTaskValve.this.jobHandler.handle(this.job,BlockingTaskValve.this.finishedQueue);

			}catch( Exception e){
				errorMessage=e.getMessage();
				stackTrace=e.getStackTrace();
			}

			if (rtMap==null){
				//handler执行结果返回null，出现异常，将异常信息保存jobwraper的errormessage中
				jw.setErrorMessage("作业执行异常:hanlde()返回值为[null]。jobId:【"
					+jw.getJobId()+"】 jobName:【"+jw.getJobName()+"】，异常信息为:"+this.errorMessage);
				jw.setErrorStackTrace(this.stackTrace);
			}else{
				jw.setJobResult(rtMap);
			}


		}catch (Exception e){
			jw.setErrorMessage("作业执行异常:hanlde()返回值为[null]。jobId:【"
				+jw.getJobId()+"】 jobName:【"+jw.getJobName()+"】，异常信息为:"+e.getMessage());
			jw.setErrorStackTrace(e.getStackTrace());
			e.printStackTrace();
		}

		log.info(getThreadInfo()+"作业【"+job+"】:处理完成。");
	}

	private String getThreadInfo(){
		return "线程号："+Thread.currentThread().getId()+",线程名："+Thread.currentThread().getName()+"。";
	}
}	
	
}