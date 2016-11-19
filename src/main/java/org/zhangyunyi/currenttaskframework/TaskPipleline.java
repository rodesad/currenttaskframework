package org.zhangyunyi.currenttaskframework;

import java.util.*;
/**
 * 定义了任务管道的接口。管道模式的变种,负责管道和任务对象的组装和查询。
 *
 */
public interface TaskPipleline {
	//获得管道名称 
	public String getName();
	//获得初始任务，管道管理任务。
	public TaskValve getInital();
	//最后结束任务，管道管理任务。
	public TaskValve getEnd();
	public void setEnd(TaskValve valve);

	//第一个任务
	public TaskValve getFirst();
	//最后一个任务
	public TaskValve getLast();
	public void addTaskValve(TaskValve valve);
	
	//得到管道中任务节点的集合，不包括初始节点和end节点
	public ArrayList<TaskValve> getTaskValves();
}