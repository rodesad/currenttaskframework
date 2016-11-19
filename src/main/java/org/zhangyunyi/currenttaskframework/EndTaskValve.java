package org.zhangyunyi.currenttaskframework;

import java.util.concurrent.*;
/**
 * 任务结束时处理类，结束的任务框架进行控制。记录结束时间等信息。
 */
 public class EndTaskValve<TTodoJob,TFinishedJob> extends BlockingTaskValve<TTodoJob,TFinishedJob>{

	protected TaskValve next = null;
	//任务名称
	private String taskName;

	public EndTaskValve(String taskName){
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

	public void handle(){
		System.out.println("处理最后一个任务。线程号："+Thread.currentThread().getId());
	};
	public void listen(){
		System.out.println("监听，空。线程号："+Thread.currentThread().getId());
	};
}
