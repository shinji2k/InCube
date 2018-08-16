package com.crscic.incube.data.classparser;

import com.crscic.incube.data.IClassParser;
import com.k.util.ByteUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月15日 下午3:54:29
 */
public class Int8Parser implements IClassParser
{

	@Override
	public byte[] getByteArrayByClass(String valueString)
	{
		int srcInt = Integer.parseInt(valueString);
		byte[] b = new byte[1];
		b[0] = ByteUtils.getBytes(srcInt)[3];
		return b;
	}

}
