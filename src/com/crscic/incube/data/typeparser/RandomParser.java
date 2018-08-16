package com.crscic.incube.data.typeparser;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.crscic.incube.data.Data;
import com.crscic.incube.data.ITypeParser;
import com.crscic.incube.data.Part;
import com.crscic.incube.exception.GenerateDataException;
import com.crscic.incube.log.Log;
import com.k.util.CollectionUtils;
import com.k.util.StringUtils;

/**
 * 
 * @author zhaokai 2018年8月16日 下午12:25:22
 */
public class RandomParser implements ITypeParser
{

	@Override
	public byte[] getSendData(List<Object> paramList)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, GenerateDataException
	{
		Part part = (Part) paramList.get(0);
		@SuppressWarnings("unchecked")
		Map<String, byte[]> lastRandomByteMap = (Map<String, byte[]>) paramList.get(1);
		
		// partname可能存在重复的情况，但目前在random中未出现，暂未处理该情况
		String partName = part.getAttribute().get("name");
		if (lastRandomByteMap.containsKey(partName))
		{
			int random = (int) (Math.random() * 1000 + 1);
			if (random > part.getPercent())
			{
				return lastRandomByteMap.get(partName);
			}
		}
		String valueString = part.getValue();
		// split节点
		String splitString = part.getSplit();
		if (StringUtils.isNullOrEmpty(splitString))
			splitString = ",";
		splitString = Data.getSplitString(splitString);
		// 对给定范围内的值进行随机
		String[] randomArray = valueString.split(splitString);
		Random r = new Random();
		String res = null;
		byte[] b = null;
		int len = 0;

		while (true)
		{
			// 不同的随机方式
			if (splitString.equals(","))
			{
				int randomIndex = r.nextInt(randomArray.length);
				res = randomArray[randomIndex];
			}
			else if (splitString.equals("-"))
			{
				int min = Integer.parseInt(randomArray[0]);
				int max = Integer.parseInt(randomArray[1]);
				int random = r.nextInt(max) % (max - min + 1) + min;
				res = Integer.toString(random);
			}
			// class节点，先取得随机数后在判断class
			String classString = part.getValueClass();
			b = Data.getByteArrayByClass(res, classString);
			// 长度校验
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
			// 与上次发送的匹配是否相同，若相同则再次随机直到不同为止
			if (!CollectionUtils.isSameArray(b, lastRandomByteMap.get(partName)))
			{
				break;
			}
			b = null;
		}
		lastRandomByteMap.put(partName, b);
		if (len > 0 && (b.length != len || b.length > len))
			Log.lengthWarning(part.getAttribute().get("name"));
		return b;
	}

}
