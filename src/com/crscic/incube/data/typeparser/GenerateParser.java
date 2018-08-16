package com.crscic.incube.data.typeparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.crscic.incube.data.Data;
import com.crscic.incube.data.ITypeParser;
import com.crscic.incube.data.Part;
import com.crscic.incube.data.PartMem;
import com.crscic.incube.exception.GenerateDataException;
import com.k.util.ByteUtils;
import com.k.util.CollectionUtils;
import com.k.util.StringUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月15日 下午6:16:57
 */
public class GenerateParser implements ITypeParser
{

	@Override
	public byte[] getSendData(List<Object> paramList)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, GenerateDataException
	{
		Part part = (Part) paramList.get(0);
		@SuppressWarnings("unchecked")
		Map<String, byte[]> quoteMap = (Map<String, byte[]>) paramList.get(1);
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> paramMap = (Map<String, Map<String, String>>) paramList.get(2);
		@SuppressWarnings("unchecked")
		Map<String, Integer> fileParamMap = (Map<String, Integer>) paramList.get(3);
		@SuppressWarnings("unchecked")
		Map<String, Integer> increaseParamMap = (Map<String, Integer>) paramList.get(4);
		@SuppressWarnings("unchecked")
		List<PartMem> partMem = (List<PartMem>)paramList.get(5);
		@SuppressWarnings("unchecked")
		Map<String, byte[]> lastRandomByteMap = (Map<String, byte[]>) paramList.get(6);
		
		List<Part> childPartList = part.getChildNodeList();
		String splitString = null;
		byte[] split = null;
		splitString = Data.getSplitString(part.getSplit());
		if (!StringUtils.isNullOrEmpty(splitString))
			split = ByteUtils.getBytes(splitString);
		List<Byte> res = new ArrayList<Byte>();
		for (Part childPart : childPartList)
		{
			byte[] b = Data.getPartData(childPart, quoteMap, paramMap, fileParamMap, increaseParamMap, partMem, lastRandomByteMap);
			CollectionUtils.copyArrayToList(res, b);
			CollectionUtils.copyArrayToList(res, split);
		}
		// 去掉末尾的分隔符
		int resLen = res.size();
		if (split != null)
			resLen = res.size() - split.length;

		byte[] ret = new byte[resLen];
		for (int i = 0; i < resLen; i++)
			ret[i] = res.get(i);
		return ret;
	}

}
