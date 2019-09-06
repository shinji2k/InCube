package com.crscic.incube.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author zhaokai 2017年9月7日 下午2:47:01
 */
public class Log
{
//	private static 
	private static <T>  Logger getLogger()
	{
		Throwable t = new Throwable();
//		返回调用类的名称
		String className = t.getStackTrace()[2].getClassName();
		Class<?> c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to create Log instance!",e);
		}
		
		return LoggerFactory.getLogger(c);
	}

	public static void debug(String log)
	{
		getLogger().debug(log);
	}

	/**
	 * 普通日志
	 * 
	 * @param log
	 *            zhaokai 2017年9月12日 下午2:22:05
	 */
	public static void info(String log)
	{
		getLogger().info(log);
	}

	/**
	 * 错误日志，含Exception重载
	 * 
	 * @param log
	 * @param e
	 *            zhaokai 2017年9月12日 下午2:21:48
	 */
	public static void error(String log, Exception e)
	{
		getLogger().error(log, e);
	}

	/**
	 * 错误日志，不含Exception重载
	 * 
	 * @param string
	 * @author ken_8 2017年9月10日 下午10:46:24
	 */
	public static void error(String log)
	{
		getLogger().error(log);
	}

	/**
	 * 警告日志
	 * 
	 * @param log
	 *            zhaokai 2017年9月12日 下午2:28:08
	 */
	public static void warn(String log)
	{
		getLogger().warn(log);
	}
	
	/**
	 * 警告日志，含Exception重载
	 * @param log
	 * @param e
	 * @author zhaokai
	 * @create 2019年9月6日 上午10:04:26
	 */
	public static void warn(String log, Exception e)
	{
		getLogger().warn(log, e);
	}

	/**
	 * 长度与配置不一致告警
	 * 
	 * @param nodaName
	 * @author zhaokai 2017年9月12日 下午6:37:05
	 */
	public static void lengthWarning(String nodaName)
	{
		getLogger().warn(nodaName + "节点生成数据的长度与配置中不一致");
	}
}
