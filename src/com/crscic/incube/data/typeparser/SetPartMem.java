package com.crscic.incube.data.typeparser;

import java.util.ArrayList;
import java.util.List;

import com.crscic.incube.data.Data;
import com.crscic.incube.data.ITypeParser;
import com.crscic.incube.data.Part;
import com.crscic.incube.data.PartMem;
import com.crscic.incube.exception.GenerateDataException;
import com.crscic.incube.log.Log;
import com.k.util.StringUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月16日 上午9:35:17
 */
public class SetPartMem implements ITypeParser
{

	@Override
	public byte[] getSendData(List<Object> paramList)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, GenerateDataException
	{
		Part part = (Part) paramList.get(0);
		@SuppressWarnings("unchecked")
		List<PartMem> partMem = (List<PartMem>) paramList.get(1);
		
		List<Part> childPartList = part.getChildNodeList();
		if (childPartList.size() == 0)
		{
			Log.error(part.getAttribute().get("name") + "节点缺少子节点");
			throw new GenerateDataException();
		}

		Part childPart = childPartList.get(0);
		// 最后组装时，检查flag的值，true的需要进行组装
		// 取父part的属性作为flag的key，最后组装根据该part确定替换哪部分的值
		String partName = part.getAttribute().get("name");
		// 检查子Part中type节点,并存入flag，补完时判断用
		String childTypeString = childPart.getType();
		if (StringUtils.isNullOrEmpty(childTypeString))
			GenerateDataException.nullNodeValueException(partName, "type");
		PartMem mem = new PartMem();
		mem.setName(partName);
		mem.setType(part.getType());
		mem.setChildType(childTypeString);
		// length检查
		String lenString = part.getLen();
		if (!StringUtils.isNullOrEmpty(lenString))
			mem.setLength(lenString);
		// 检查合法性
		String rangeString = childPart.getValue();
		if (StringUtils.isNullOrEmpty(rangeString))
			GenerateDataException.nullNodeValueException(partName, "value");
		// 是否需要分割
		String splitString = Data.getSplitString(childPart.getSplit());
		List<String> rangePart = new ArrayList<String>();
		if (!StringUtils.isNullOrEmpty(splitString))
		{
			String[] rangeParts = rangeString.split(splitString);
			for (String range : rangeParts)
				rangePart.add(range);
		}
		else
			rangePart.add(rangeString);
		mem.setRangeList(rangePart);
		partMem.add(mem);
		return null;
	}

}
