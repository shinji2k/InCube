package com.crscic.incube.data.typeparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crscic.incube.data.Data;
import com.crscic.incube.data.ITypeParser;
import com.crscic.incube.data.Part;
import com.crscic.incube.exception.GenerateDataException;

/**
 * 
 * @author zhaokai
 * 2018年8月16日 下午3:09:54
 */
public class ParameterParser implements ITypeParser
{

	@Override
	public byte[] getSendData(List<Object> paramList)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, GenerateDataException
	{
		Part part = (Part) paramList.get(0);
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> paramMap = (Map<String, Map<String, String>>) paramList.get(1);
		@SuppressWarnings("unchecked")
		Map<String, byte[]> lastRandomByteMap = (Map<String, byte[]>) paramList.get(2);
		
		if (paramMap == null || paramMap.keySet().size() == 0)
			return null;
		String fieldName = part.getAttribute().get("name");
		byte[] b = null;
		if (paramMap.containsKey(fieldName))
		{
			Map<String, String> paramAttrMap = paramMap.get(fieldName);
			if (paramAttrMap.get("type").toLowerCase().trim().equals("aptotic"))
				b = Data.getByteArrayByClass(paramAttrMap.get("value"), paramAttrMap.get("class"));
			else if (paramAttrMap.get("type").toLowerCase().trim().equals("random"))
			{
				// 组装一个临时part
				Part tempRandomPart = new Part();
				Map<String, String> tempAttribute = new HashMap<String, String>();
				tempAttribute.put("name", part.getAttribute().get("name"));
				tempRandomPart.setAttribute(tempAttribute);
				tempRandomPart.setType(paramAttrMap.get("type"));
				tempRandomPart.setValue(paramAttrMap.get("value"));
				tempRandomPart.setValueClass(paramAttrMap.get("class"));
				tempRandomPart.setLen(part.getLen());
				tempRandomPart.setSplit(part.getSplit());
				tempRandomPart.setFillByte(part.getFillByte());
				tempRandomPart.setFillDirection(part.getFillDirection());
				tempRandomPart.setPercent(Integer.parseInt(paramAttrMap.get("percent")));

				List<Object> tempParamList = new ArrayList<Object>();
				tempParamList.add(tempRandomPart);
				tempParamList.add(lastRandomByteMap);
				b = new RandomParser().getSendData(tempParamList);
			}
			else
				b = Data.getByteArrayByClass(paramAttrMap.get("value"), paramAttrMap.get("class"));

		}
		return b;
	}

}
