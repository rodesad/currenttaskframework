package org.zhangyunyi.currenttaskframework;

/**
 * 读取配置文件，配置参数信息。
 * @author 张立新
 * @date 2016-11-9
 */
 public class ConfigParam{
	//todo队列长度
	static int TODOQUEUE_LENGTH=10;
	//finished队列长度
	static int FINISHEDQUEUE_LENGTH=16;
	//failed队列长度
	static int FAILEDQUEUE_LENGTH=10;
    //job执行线程池大小
	static int JOBEXEC_THREADPOOL_SIZE=100;
	//向管道中添加初始作业线程池大小
	static int ADDJOBEXEC_THREADPOOL_SIZE=2;
 }