package com.crscic.incube.data.typeparser;

import java.util.List;

import com.crscic.incube.data.Data;
import com.crscic.incube.data.ITypeParser;
import com.crscic.incube.data.Part;
import com.crscic.incube.log.Log;
import com.k.util.StringUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月14日 下午12:02:06
 */
public class AptoticParser implements ITypeParser
{

	@Override
	public byte[] getSendData(List<Object> paramList) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Part part = (Part) paramList.get(0);
		byte[] b;
		// 获取value的值
		String valueString = part.getValue();
		// xml中value是16进制字符串，将其装换为byte
		String classString = part.getValueClass();
		b = Data.getByteArrayByClass(valueString, classString);
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
