package com.crscic.incube.data.classparser;

import com.crscic.incube.data.IClassParser;
import com.k.util.ByteUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月15日 下午3:51:56
 */
public class IntParser implements IClassParser
{

	@Override
	public byte[] getByteArrayByClass(String valueString)
	{
		int srcInt = Integer.parseInt(valueString);
		return ByteUtils.getBytes(srcInt);
	}

}
