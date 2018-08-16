package com.crscic.incube.data.classparser;

import com.crscic.incube.data.IClassParser;
import com.k.util.ByteUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月15日 下午3:50:58
 */
public class IntToHexToByteParser implements IClassParser
{

	@Override
	public byte[] getByteArrayByClass(String valueString)
	{
		int srcInt = Integer.parseInt(valueString);
		String hexSrc = Integer.toHexString(srcInt).toUpperCase();
		return ByteUtils.getBytes(hexSrc);
	}

}
