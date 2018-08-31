package com.crscic.incube.data.typeparser;

import java.util.List;
import java.util.Map;

import com.crscic.incube.data.Data;
import com.crscic.incube.data.ITypeParser;
import com.crscic.incube.entity.Part;
import com.crscic.incube.log.Log;
import com.k.util.StringUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月16日 下午3:26:16
 */
public class RequestParser implements ITypeParser
{

	@Override
	public byte[] getSendData(List<Object> paramList)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Part part = (Part) paramList.get(0);
		@SuppressWarnings("unchecked")
		Map<String, byte[]> quoteMap = (Map<String, byte[]>) paramList.get(1);
		
		String nodeName = part.getAttribute().get("name");
		byte[] b = null;
		if (quoteMap.containsKey(nodeName))
			b = quoteMap.get(nodeName);
		else
			return b;
		
		String lenString = part.getLen();
		int len = 0;
		if (!StringUtils.isNullOrEmpty(lenString))
			len = Integer.parseInt(lenString);
		// 检查有没有配置fill-byte，若配置了则说明需要填充
		String fillByteString = part.getFillByte();
		if (!StringUtils.isNullOrEmpty(fillByteString))
		{
			String fillDirectionString = part.getFillDirection();
			b = Data.doFill(b, fillByteString, fillDirectionString, len);
		}
		if (len > 0 && b.length != len)
			Log.warn(part.getAttribute().get("name") + "节点生成数据的长度与配置中不一致");
		return b;
	}

}
