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

	public static void main(String[] args)
	{
		int a = 25;
		System.out.println(Integer.toBinaryString(a));
		System.out.println(ByteUtils.byteToHexString(ByteUtils.getBytes(a)));
		
		IntToByteToAscParser itba = new IntToByteToAscParser();
		byte[] b = itba.getByteArrayByClass("15");
		System.out.println(ByteUtils.byteToHexString(b));
	}
	
	@Override
	public byte[] getByteArrayByClass(String valueString)
	{
		byte[] intByte = ByteUtils.getBytes(Integer.parseInt(valueString)); // 返回的是4字节的数组
		// 为适用A接口，将4字节缩为2字节
		byte[] b = new byte[2];
		//原规则，添加海信精密空调时注释
//		b[0] = (byte) ((intByte[0] << 8) | intByte[1]);
//		b[1] = (byte) ((intByte[2] << 8) | intByte[3]);
		//海信精密空调使用的下面这个规则。后续需要确定这个规则是不是通用的
		b[0] = intByte[2];
		b[1] = intByte[3];
		b = ByteUtils.byteToAsc(b);
		return b;
	}

}
