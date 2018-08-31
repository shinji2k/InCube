package com.crscic.incube.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhaokai 2018年8月14日 下午3:00:21
 */
public class SettingEntity
{
	private Map<String, String> method = new HashMap<String, String>();
	private Map<String, List<String>> param = new HashMap<String, List<String>>();

	public Map<String, String> getMethod()
	{
		return method;
	}

	public void setMethod(Map<String, String> method)
	{
		this.method = method;
	}

	public Map<String, List<String>> getParam()
	{
		return param;
	}

	public void setParam(Map<String, List<String>> param)
	{
		this.param = param;
	}
	
	public List<String> getParam(String methodName)
	{
		if (param.containsKey(methodName))
			return param.get(methodName);
		else
			return null;
	}

	public String getMethodClass(String methodName)
	{
		if (method.containsKey(methodName))
			return method.get(methodName);
		else
			return null;
	}
	
	public void addMethod(String methodName, String className)
	{
		if (method == null)
			method = new HashMap<String, String>();
		method.put(methodName, className);
	}
	
	public void addParam(String methodName, List<String> paramList)
	{
		if (param == null)
			param = new HashMap<String, List<String>>();
		param.put(methodName, paramList);
	}
}
