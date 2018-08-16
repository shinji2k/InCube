package com.crscic.incube.data.classparser;

import com.crscic.incube.data.IClassParser;
import com.k.util.ByteUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月15日 下午3:42:47
 */
public class HexToByteParser implements IClassParser
{

	@Override
	public byte[] getByteArrayByClass(String valueString)
	{
		String hexString = ByteUtils.byteToHexString(valueString.getBytes());
		hexString = hexString.replaceAll(" ", "");
		return ByteUtils.getBytes(hexString);
	}

}
