package com.crscic.incube.data.classparser;

import com.crscic.incube.data.IClassParser;
import com.k.util.ByteUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月15日 下午3:44:42
 */
public class IntToByteToAscParser implements IClassParser
{

	@Override
	public byte[] getByteArrayByClass(String valueString)
	{
		byte[] intByte = ByteUtils.getBytes(Integer.parseInt(valueString)); // 返回的是4字节的数组
		// 为适用A接口，将4字节缩为2字节
		byte[] b = new byte[2];
		b[0] = (byte) ((intByte[0] << 8) | intByte[1]);
		b[1] = (byte) ((intByte[2] << 8) | intByte[3]);
		b = ByteUtils.byteToAsc(b);
		return b;
	}

}
