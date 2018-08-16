package com.crscic.incube.data.typeparser;

import java.util.List;
import java.util.Map;

import com.crscic.incube.data.Data;
import com.crscic.incube.data.ITypeParser;
import com.crscic.incube.data.Part;
import com.crscic.incube.exception.GenerateDataException;
import com.crscic.incube.log.Log;
import com.k.util.StringUtils;

/**
 * 
 * @author zhaokai
 * 2018年8月16日 下午3:03:01
 */
public class IncreaseParser implements ITypeParser
{

	@Override
	public byte[] getSendData(List<Object> paramList)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, GenerateDataException
	{
		Part part = (Part) paramList.get(0);
		@SuppressWarnings("unchecked")
		Map<String, Integer> increaseParamMap = (Map<String, Integer>) paramList.get(1);
		
		List<Part> childPartList = part.getChildNodeList();
		if (childPartList.size() == 0)
		{
			// 考虑到一个文件中有多组协议，其中一个出问题其他的可能正确，因此使用警告继续程序进行而不是中断程序
			Log.warn(part.getAttribute().get("name") + "节点缺少子节点");
			return null;
		}
		Part childPart = childPartList.get(0);
		// 若自增缓存变量未初始化，则初始化为配置中的起始值
		if (increaseParamMap.get("increaseMem") == -1)
			increaseParamMap.put("increaseMem", Integer.parseInt(childPart.getValue()));
		String valueString = Integer.toString(increaseParamMap.get("increaseMem"));
		String classString = childPart.getValueClass();
		byte[] b = null;
		b = Data.getByteArrayByClass(valueString, classString);
		// 检查有没有配置fill-byte，若配置了则说明需要填充
		String fillByteString = childPart.getFillByte();
		int len = 0;
		String lenString = childPart.getLen();
		if (!StringUtils.isNullOrEmpty(lenString))
			len = Integer.parseInt(lenString);
		if (!StringUtils.isNullOrEmpty(fillByteString))
		{
			String fillDirectionString = childPart.getFillDirection();
			b = Data.doFill(b, fillByteString, fillDirectionString, len);
		}

		if (len > 0 && b.length != len)
			Log.warn(part.getAttribute().get("name") + "节点生成数据的长度与配置中不一致");

		// 每使用一次自增值
		int step = Integer.parseInt(childPart.getSplit());
		increaseParamMap.put("increaseMem", increaseParamMap.get("increaseMem") + step);
		return b;
	}

}
