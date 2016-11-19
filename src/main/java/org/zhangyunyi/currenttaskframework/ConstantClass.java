package org.zhangyunyi.currenttaskframework;

/**
 * 常量类
 * @author 张立新
 * @date 2016-11-9
 */
 public class ConstantClass{
	//jobhandler.handle()返回值，hashmap的key
	public enum HandleResultKey{
		STATUS,//执行成功还是失败，值sucess，false
		RESULTSIZE,//执行记录数
		ERRORMESSAGE,//异常信息
		ERRORSTACKTRACE,//异常堆栈
	 }

	public enum HandleResultStatusValue{
		//jobhandler.handle()返回值，hashmap的value
		//和key值一一对应
		SUCESS,//执行成功
		FALSE,//执行失败
	}  
	 
 }