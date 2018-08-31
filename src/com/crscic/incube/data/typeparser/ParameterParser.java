package com.crscic.incube.data.typeparser;

import java.util.List;
import java.util.Map;

import com.crscic.incube.data.Data;
import com.crscic.incube.data.ITypeParser;
import com.crscic.incube.entity.Part;
import com.crscic.incube.entity.PartMem;
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
		Map<String, byte[]> quoteMap = (Map<String, byte[]>) paramList.get(1);
		@SuppressWarnings("unchecked")
		Map<String, Part> paramMap = (Map<String, Part>) paramList.get(2);
		@SuppressWarnings("unchecked")
		Map<String, Integer> fileParamMap = (Map<String, Integer>) paramList.get(3);
		@SuppressWarnings("unchecked")
		Map<String, Integer> increaseParamMap = (Map<String, Integer>) paramList.get(4);
		@SuppressWarnings("unchecked")
		List<PartMem> partMem = (List<PartMem>)paramList.get(5);
		@SuppressWarnings("unchecked")
		Map<String, byte[]> lastRandomByteMap = (Map<String, byte[]>) paramList.get(6);
		
		if (paramMap == null || paramMap.keySet().size() == 0)
			return null;
		String fieldName = part.getAttribute().get("name");
		byte[] b = null;
		if (paramMap.containsKey(fieldName))
			b = Data.getPartData(paramMap.get(fieldName), quoteMap, paramMap, fileParamMap, increaseParamMap, partMem, lastRandomByteMap);
		
		return b;
	}

}
