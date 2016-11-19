package org.zhangyunyi.currenttaskframework;

import java.sql.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.*;
/**
 * 这是一个主动对象，负责装配任务和管道。
 */
public class Main {
	//获取Logger
	private static Logger log= Logger.getLogger("Main");
	public static void main(String[] args) {
		
		log.info("系统主线程启动。线程Id：【"
			+Thread.currentThread().getId()+"】,线程名称：【"+Thread.currentThread().getName()+"】");
			
		//装配任务
		//1.定义作业队列，这个基础，下面的作业是阻塞队列中保存，需要相应的处理阻塞队列的任务taskvalve处理。
		//第一个任务是查询任务，将待执行的sql语句写入队列,todo队列，failed队列为空，finished队列存储SQL
		// 语句。
			
		//2.定义TaskValve，为每个TaskValve分配一个任务处理器Task Handler，
		BlockingTaskValve<Integer,Integer> queryTask = new BlockingTaskValve<Integer,Integer>(
			"执行SQL查询",new QueryDataTaskHandler());
		BlockingTaskValve<Integer,Integer> generSql1 = new BlockingTaskValve<Integer,Integer>(
			"生成SQL脚本-1",new GenerSqlTaskHandler());
		BlockingTaskValve<Integer,Integer> generSql2 = new BlockingTaskValve<Integer,Integer>(
			"生成SQL脚本-2",new GenerSqlTaskHandler());
		BlockingTaskValve<Integer,Integer> lastTask = new BlockingTaskValve<Integer,Integer>(
			"最后的任务",new LastTaskHandler());

		log.info("任务"+queryTask.getName()+"todoQueue作业个数:【"
			+((BlockingQueue)queryTask.getTodoQueue()).size()+"】");
		
		TaskPiplelineManager tplm = new TaskPiplelineManager();
		TaskPipleline tpl= tplm.createPipleline("数据迁移1号管道");
		tpl.addTaskValve(queryTask);
		tpl.addTaskValve(generSql1);
		tpl.addTaskValve(generSql2);
		tpl.addTaskValve(lastTask);
		log.info("将任务节点添加到管道中：添加完成。");	

		log.info("向管道添加作业：开始添加。");	
		
		for(int i=0;i<10;i++){
			
			tplm.addJob(1);
		}
		
		log.info("向管道添加作业：添加完成。");	
		
		log.info("管道：开始启动。");	
		tplm.start();
		try{
			Thread.sleep(10*1000);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		tplm.close();
		log.info("作业执行统计数据：");	
		log.info(tplm.getExecStatistics());
		
		log.info("管道:执行结束，关闭。");	
		log.info("主线程成功执行结束。");	
	
	}
}