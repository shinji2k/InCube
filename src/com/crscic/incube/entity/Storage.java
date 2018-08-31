package com.crscic.incube.entity;

import java.util.List;

/**
 * param.xml中storage节点实体，用来存放一些需要根据请求返回的信息
 * @author zhaokai
 * 2018年8月17日 下午4:31:18
 */
public class Storage
{
	private String storageName;
	private List<Part> partList;
	
	public String getStorageName()
	{
		return storageName;
	}
	public void setStorageName(String storageName)
	{
		this.storageName = storageName;
	}
	public List<Part> getPartList()
	{
		return partList;
	}
	public void setPartList(List<Part> partList)
	{
		this.partList = partList;
	}
}
