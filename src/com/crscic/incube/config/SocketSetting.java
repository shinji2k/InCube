/**
 * 
 */
package com.crscic.incube.config;

/**
 * Socket接口的配置实体类
 * 
 * @author zhaokai 2017年9月6日 下午6:27:59
 */
public class SocketSetting
{
	private String type;
	private String ip;
	private String localIp;
	private String port;
	private String localPort;
	private boolean keepAlive;

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public String getPort()
	{
		return port;
	}

	public void setPort(String port)
	{
		this.port = port;
	}

	/**
	 * @return the keepAlive
	 */
	public boolean getKeepAlive()
	{
		return keepAlive;
	}

	/**
	 * @param keepAlive
	 *            the keepAlive to set
	 */
	public void setKeepAlive(String keepAlive)
	{
		if (keepAlive.trim().toLowerCase().equals("true"))
			this.keepAlive = true;
		else	//若为空时，默认为false 
			this.keepAlive = false;
			
	}

	public String getLocalIp()
	{
		return localIp;
	}

	public void setLocalIp(String localIp)
	{
		this.localIp = localIp;
	}

	public String getLocalPort()
	{
		return localPort;
	}

	public void setLocalPort(String localPort)
	{
		this.localPort = localPort;
	}

}
