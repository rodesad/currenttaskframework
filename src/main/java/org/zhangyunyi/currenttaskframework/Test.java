package org.zhangyunyi.currenttaskframework;
//测试
public class Test{
	public static void main(String[] args){
		System.out.println(ConstantClass.HandleResultKey.STATUS);
		System.out.println(ConstantClass.HandleResultStatusValue.SUCESS);
		
				ConstantClass.HandleResultStatusValue s= ConstantClass.HandleResultStatusValue.SUCESS;
				
				switch (s){
					case SUCESS:
						System.out.println(ConstantClass.HandleResultStatusValue.SUCESS);
						break;
					case FALSE:
						System.out.println(ConstantClass.HandleResultStatusValue.FALSE);
						break;
					default:
				}//swith

		}
}