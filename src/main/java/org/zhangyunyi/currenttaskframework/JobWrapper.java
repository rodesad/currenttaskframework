package org.zhangyunyi.currenttaskframework;

import java.util.*;
import java.util.concurrent.*;
/**
 *    任务包装类，该类定义了，1）执行的task名称，实例名称；
 * 2）作业执行情况，执行结果，执行次数，失败的次数，执行总时间；每次执行结果，执行时间。
 *    一个实例，对应一个作业实例，一个作业实例对应一个task实例，
 * @author 张立新
 * @date 2016-11-7
 */
 public class JobWrapper{
	//作业id，唯一表示job，取System.identityHashCode();
	private String jobId;
	//作业名称,取job的toString()，此时有重复的对象的名字
	private String jobName;
	//job执行结果,结构件ConstantClass.HandleResult
	private HashMap<ConstantClass.HandleResultKey,Object> jobResult;
	//作业描述
	private String jobDescription;
	// 作业执行成功数量,保存线程的返回值
	private long successResult;
	//失败原因，保存e.getmsg();
	private String errorMessage;
	//异常堆栈
	private StackTraceElement[] errorStackTrace; 
	// 作业执行状态
	private String status;
	// 作业执行时间，单位毫秒
	private long time;
	// 作业开始时间
	private Date beginDate;
	// 作业结束时间
	private Date endDate;
	
	//执行的线程ID
	private String threadID;
	
	public JobWrapper (String jobId,String jobName){
		this.jobId=jobId;
		this.jobName=jobName;
	}
	public JobWrapper (String jobId){
		this.jobId=jobId;
	}

//*****属性操作**********	
	public String getJobId(){
		return this.jobId;
	}
	public String getJobName(){
		return this.jobName;
	}
	public void setJobName(String jobName){
		this.jobName=jobName;
	}
	
	public void setJobResult(HashMap jobResult){
		this.jobResult=jobResult;
	}
	public HashMap getJobResult(){
		return this.jobResult;
	}
	
	public String getJobDescription(){
		return jobDescription;
	}
	public void setJobDescription(String jobDescription){
		 this.jobDescription=jobDescription;
	}

	public long getsuccessResult(){
		return successResult;
	}
	public void setJobResult(long successResult){
		 this.successResult=successResult;
	}

	public String getErrorMessage(){
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage){
		 this.errorMessage=errorMessage;
	}

	public String getErrorStackTraceString(){
		String rt="";
		if(errorStackTrace!=null){
			for(int i=0;i<this.errorStackTrace.length;i++){
				rt=rt+System.getProperty("line.separator")+errorStackTrace[i];
		}
		}
		return rt;
	}
	public StackTraceElement[] getErrorStackTrace(){
		 return this.errorStackTrace;
	}
	public void setErrorStackTrace(StackTraceElement[] errorStackTrace){
		 this.errorStackTrace=errorStackTrace;
	}
	
	
	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
		 this.status=status;
	}
	
	public long getTime(){
		return time;
	}
	public void setTime(long time){
		 this.time=time;
	}

	public Date getBeginDate(){
		return beginDate;
	}
	public void setBeginDate(Date beginDate){
		 this.beginDate=beginDate;
	}

	public Date getEndDate(){
		return endDate;
	}
	public void setEndDate(Date endDate){
		 this.endDate=endDate;
	}	
}