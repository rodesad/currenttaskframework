package org.zhangyunyi.currenttaskframework;

import java.util.*;

/**
 * 任务管道实现，先设置结束阀门，顺序添加其他的阀门，先添加的先执行，最后调用结束阀门。
 * 该类非线程安全。
 */
public class TaskPiplelineImpl implements TaskPipleline {
	// 管道名称
	private String name="default pipleline";
	protected TaskValve inital = null; 
	protected TaskValve end = null; 
	private ArrayList<TaskValve> tasks=new ArrayList<TaskValve>(); 

	public TaskPiplelineImpl(String piplelineName){
		this.name=piplelineName;
	}
	public void addTaskValve(TaskValve valve) {
	
		if(tasks.isEmpty()){
			valve.setNext(end);
			inital=valve;
			tasks.add(valve);
		}else{
			//如果队列仅有一个last元素
			if(tasks.get(tasks.size()-1)==end && tasks.size()==1){
				//将新添加的元素放到数组尾部
				end=tasks.set(tasks.size()-1,valve);
			}else{
				//将上一个元素指向新添加的元素
				tasks.get(tasks.size()-2).setNext(valve);
				//将新添加的元素放到数组尾部
				end=tasks.set(tasks.size()-1,valve);
				
			}
			tasks.add(end);
		}
	}
	public String getName() {
		return this.name;
	}

	public void setEnd(TaskValve valve){
		this.end=valve;
		tasks.add(valve);
	};

	public TaskValve getEnd() {
		return tasks.get(tasks.size()-1);
	}
	public TaskValve getInital() {
		return tasks.get(0);
	}

	public TaskValve getLast() {
		TaskValve tv=null;
		if (tasks.size()>1){
			tv = tasks.get(tasks.size()-2);
		}
		return tv ;
	}
	public TaskValve getFirst() {
		TaskValve tv=null;
		if (tasks.size()>1){
			tv = tasks.get(1);
		}
		return tv ;
	}
	//得到管道中的所有任务节点，不包括初始节点和end节点
	public ArrayList<TaskValve> getTaskValves(){
		ArrayList<TaskValve> al = new ArrayList<TaskValve>();
		for(int i=1;i<tasks.size()-1;i++){
			al.add(tasks.get(i));
		}
		return al;
	}
}