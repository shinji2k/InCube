package com.crscic.incube.data.classparser;

import com.crscic.incube.data.IClassParser;
import com.k.util.ByteUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月15日 下午3:52:42
 */
public class ScadaIdParser implements IClassParser
{

	@Override
	public byte[] getByteArrayByClass(String valueString)
	{
		return ByteUtils.scadaIdToBytes(valueString);
	}

}
