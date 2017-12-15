package com.crscic.incube.config;

import java.util.List;

/**
 * 
 * @author zhaokai
 * 2017年11月30日 上午10:07:26
 */
public class InterChangeSetting
{
	private String file;
	private List<Request> request;
	/**
	 * @return the file
	 */
	public String getFile()
	{
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(String file)
	{
		this.file = file;
	}
	/**
	 * @return the requestList
	 */
	public List<Request> getRequest()
	{
		return request;
	}
	/**
	 * @param requestList the requestList to set
	 */
	public void setRequest(List<Request> request)
	{
		this.request = request;
	}
}
