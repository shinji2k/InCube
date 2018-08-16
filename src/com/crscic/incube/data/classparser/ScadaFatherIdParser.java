package com.crscic.incube.data.classparser;

import com.crscic.incube.data.IClassParser;
import com.k.util.ByteUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月15日 下午3:53:35
 */
public class ScadaFatherIdParser implements IClassParser
{

	@Override
	public byte[] getByteArrayByClass(String valueString)
	{
		String[] parts = valueString.split("\\.");
		for (int i = parts.length - 1; i > -1; i--)
		{
			if (!parts[i].equals("0"))
			{
				parts[i] = "0";
				break;
			}
		}
		StringBuilder fatherIdStr = new StringBuilder();
		for (int i = 0; i < parts.length; i++)
			fatherIdStr.append(parts[i] + ".");

		fatherIdStr = fatherIdStr.deleteCharAt(fatherIdStr.length() - 1);
		return ByteUtils.scadaIdToBytes(fatherIdStr.toString());
	}

}
