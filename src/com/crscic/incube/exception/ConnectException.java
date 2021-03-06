/**
 * 
 */
package com.crscic.incube.exception;

import java.io.IOException;

import com.crscic.incube.log.Log;

/**
 * 仅用来中断程序
 * @author ken_8
 * 2017年9月10日 下午11:09:07
 */
public class ConnectException extends Exception
{
	private static final long serialVersionUID = -3319490080953863019L;

	public ConnectException(String msg)
	{
		super(msg);
	}
	
	public ConnectException()
	{
		super();
	}
	
	public ConnectException(IOException e)
	{
		super(e);
	}
	
	public static void throwWriteErr(IOException e) throws ConnectException
	{
		Log.error("写入数据失败", e);
		throw new ConnectException();
	}
}
