package org.zhangyunyi.currenttaskframework;

import java.util.concurrent.*;
/**
 * 初始任务处理类，任务开始时初始动作，任务框架进行控制。记录结束时间等信息。
 */
 public class InitTaskValve<TTodoJob,TFinishedJob> extends BlockingTaskValve <TTodoJob,TFinishedJob>{

 	protected TaskValve next = null;
	//任务名称
	private String taskName;

	public InitTaskValve(String taskName){
		this.taskName=taskName;
	}

	public String getName(){
		return "task name：【"+this.taskName+"】";
	}
	public void setNext(TaskValve valve) {
		this.next = valve;
	}
	public TaskValve getNext() {
		return this.next;
	}

	/**
	 * 处理数据,记录执行信息。委托给实际的类进行处理。
	 */
	public void handle(){
		System.out.println("处理初始任务。线程号："+Thread.currentThread().getId());
	};
	public void listen(){
		System.out.println("监听，空。线程号："+Thread.currentThread().getId());
	};
}
