package org.zhangyunyi.currenttaskframework;


import java.util.concurrent.*;
import java.util.*;
/**
 * 作业处理接口,接收待处理的作业，将处理结果放入队列。
 */
public interface JobHandler<TTodoJob,TFinishedJob>{
	//在hashmap保存运行结果，参数见常量类,job待处理的任务，resultQueue保存处理的job结果
	public HashMap<ConstantClass.HandleResultKey,Object> handle(TTodoJob job,BlockingQueue<TFinishedJob> reslutQueue);
	//返回处理该任务handler的名字，如：生成SQL handler。
	public String getName();
}