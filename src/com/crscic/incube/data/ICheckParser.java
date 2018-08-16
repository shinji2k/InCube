package com.crscic.incube.data;

import java.util.List;

/**
 * 
 * @author zhaokai
 * 2018年8月16日 下午5:17:52
 */
public interface ICheckParser
{
	public byte[] getCheckData(List<Byte> data);
}
