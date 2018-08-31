package com.crscic.incube.data.typeparser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.crscic.incube.data.Data;
import com.crscic.incube.data.ITypeParser;
import com.crscic.incube.entity.Part;
import com.crscic.incube.exception.GenerateDataException;
import com.crscic.incube.log.Log;
import com.k.util.StringUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月16日 下午12:23:11
 */
public class TimeParser implements ITypeParser
{

	@Override
	public byte[] getSendData(List<Object> paramList)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, GenerateDataException
	{
		Part part = (Part) paramList.get(0);
		
		String valueString = part.getValue();
		if (StringUtils.isNullOrEmpty(valueString))
			GenerateDataException.nullNodeValueException(part.getAttribute().get("name"), "value");
		String classString = part.getValueClass();
		if (StringUtils.isNullOrEmpty(classString))
			GenerateDataException.nullNodeValueException(part.getAttribute().get("name"), "class");

		SimpleDateFormat f = new SimpleDateFormat(valueString);
		String timeString = f.format(new Date());

		byte[] b = Data.getByteArrayByClass(timeString, classString);
		// 长度校验
		int len = 0;
		String lenString = part.getLen();
		if (!StringUtils.isNullOrEmpty(lenString))
			len = Integer.parseInt(lenString);

		// 检查有没有配置fill-byte，若配置了则说明需要填充
		String fillByteString = part.getFillByte();
		if (!StringUtils.isNullOrEmpty(fillByteString))
		{
			String fillDirectionString = part.getFillDirection();
			b = Data.doFill(b, fillByteString, fillDirectionString, len);
		}

		if (len > 0 && (b.length != len || b.length > len))
			Log.lengthWarning(part.getAttribute().get("name"));
		return b;
	}

}
