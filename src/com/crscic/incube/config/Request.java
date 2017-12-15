package com.crscic.incube.config;

/**
 * 
 * @author ken_8
 * @create 2017年10月12日 下午11:44:31
 */
public class Request
{
	private String timeout;
	private String retry;
	private String pro;
	private Response response;
	
	/**
	 * @return the sendProtocol
	 */
	public String getSendProtocol()
	{
		return pro;
	}
	/**
	 * @param sendProtocol the sendProtocol to set
	 */
	public void setSendProtocol(String sendProtocol)
	{
		this.pro = sendProtocol;
	}
	/**
	 * @return the response
	 */
	public Response getResponse()
	{
		return response;
	}
	/**
	 * @param response the response to set
	 */
	public void setResponse(Response response)
	{
		this.response = response;
	}
	/**
	 * @return the timeout
	 */
	public String getTimeout()
	{
		return timeout;
	}
	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(String timeout)
	{
		this.timeout = timeout;
	}
	/**
	 * @return the retry
	 */
	public String getRetry()
	{
		return retry;
	}
	/**
	 * @param retry the retry to set
	 */
	public void setRetry(String retry)
	{
		this.retry = retry;
	}
	/**
	 * @return the pro
	 */
	public String getPro()
	{
		return pro;
	}
	/**
	 * @param pro the pro to set
	 */
	public void setPro(String pro)
	{
		this.pro = pro;
	}
}
