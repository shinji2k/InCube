package com.crscic.incube.data.checkparser;
import java.util.List;

import com.crscic.incube.data.Encryption;
import com.crscic.incube.data.ICheckParser;

/**
 * 
 * @author zhaokai
 * 2018年8月16日 下午5:19:29
 */
public class XorParser implements ICheckParser
{

	@Override
	public byte[] getCheckData(List<Byte> data)
	{
		return Encryption.getXorData(data);
	}

}
