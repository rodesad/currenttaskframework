package org.zhangyunyi.currenttaskframework;

import java.util.concurrent.*;
import java.lang.*;
/**
 *     处理流程，在源数据库中执行sql，将ResultSet中的数据转换为sql插入脚本，
 *  执行插入脚本写入目的库；验证源和目的库的数据的正确性、完整性和一致性。
 *     架构设计，采用管道模式pipleline；任务为valve，流程为piple。
 import java.util.concurrent.*;
 * 主线程控制任务管道，其他的子线程执行任务。从上一个任务Queue队列中取任务
 * 并发执行，结果写入当前任务队列。
 *     没有采用责任链模式的原因，在此处理过程中是一个串行的，不存在根据内容（即职责）
 * 进行选择。
 * 在piple流动的queue队列。
 */
public interface TaskValve<TTodoJob,TFinishedJob> {
	public TaskValve getNext();
	public void setNext(TaskValve valve);
	public String getName();
  
	//handle负责处理任务。
	public void handle();

	//handle负责处理任务。
	public void handle(TTodoJob e);
  
	//监听任务节点，启动单独的监听线程。
	public void listen();

	//下面的接口是处理该任务handler的接口，TaskValve把处理委托给TaskHanlder。
	//TaskHandler接口框架定义，实现为框架外实现。是框架的边界接口类。
	public JobHandler getJobHandler();

	//  得到待处理队列
	public BlockingQueue<TTodoJob> getTodoQueue();
	//  设置待处理队列
	public void setTodoQueue(BlockingQueue<TTodoJob> todoQueue);

	//  得到处理完成队列
	public BlockingQueue<TFinishedJob>getFinishedQueue();
	//  得到处理失败队列
	public BlockingQueue<TFinishedJob> getFailedQueue();
	
	//得到作业job的执行情况
	public ConcurrentHashMap<String,JobWrapper> getJobExecStatistics();
	
  
}