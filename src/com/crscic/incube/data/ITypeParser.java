package com.crscic.incube.data;

import java.util.List;

import com.crscic.incube.exception.GenerateDataException;

/**
 * 
 * @author zhaokai
 * 2018年8月14日 下午12:00:58
 */
public interface ITypeParser
{
	byte[] getSendData(List<Object> paramList) throws InstantiationException, IllegalAccessException, ClassNotFoundException, GenerateDataException;
}
