package com.crscic.incube.data.classparser;

import com.crscic.incube.data.IClassParser;
import com.k.util.ByteUtils;

/**
 * 
 * @author zhaokai 2018年8月15日 下午3:41:39
 */
public class FloatParser implements IClassParser
{

	@Override
	public byte[] getByteArrayByClass(String valueString)
	{
		// Float f = new Float(Float.parseFloat(src) * 100);
		// 按照铁标只需要float乘以100发送，不需要转int
		// b = ByteUtils.getBytes(f.intValue());
		return ByteUtils.getBytes(Float.parseFloat(valueString));
	}

}
