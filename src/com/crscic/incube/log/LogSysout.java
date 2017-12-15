/**
 * 
 */
package com.crscic.incube.log;

/**
 * 
 * @author zhaokai 2017年9月7日 下午2:47:01
 */
public class LogSysout
{
	public static void debug(String log)
	{
		System.out.println("Debug:" + log);
	}

	/**
	 * 普通日志
	 * 
	 * @param log
	 *            zhaokai 2017年9月12日 下午2:22:05
	 */
	public static void info(String log)
	{
		System.out.println("Info:" + log);
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
		System.out.println("Error:" + log);
		e.printStackTrace();
	}

	/**
	 * 错误日志，不含Exception重载
	 * 
	 * @param string
	 * @author ken_8 2017年9月10日 下午10:46:24
	 */
	public static void error(String log)
	{
		System.out.println("Error:" + log);
	}

	/**
	 * 警告日志
	 * 
	 * @param log
	 *            zhaokai 2017年9月12日 下午2:28:08
	 */
	public static void warn(String log)
	{
		System.out.println("Warn:" + log);
	}

	/**
	 * 长度与配置不一致告警
	 * 
	 * @param nodaName
	 * @author zhaokai 2017年9月12日 下午6:37:05
	 */
	public static void lengthWarning(String nodaName)
	{
		System.out.println("Warn:" + nodaName + "节点生成数据的长度与配置中不一致");
	}
}
