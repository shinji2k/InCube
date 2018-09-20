package com.crscic.incube.data.classparser;

import com.crscic.incube.data.IClassParser;

/**
 * 
 * @author zhaokai
 * 2018年9月12日 下午3:40:14
 */
public class AscParser implements IClassParser
{

	@Override
	public byte[] getByteArrayByClass(String valueString)
	{
		return valueString.getBytes();
	}

}
