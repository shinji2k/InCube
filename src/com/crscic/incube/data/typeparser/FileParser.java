package com.crscic.incube.data.typeparser;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.crscic.incube.data.Data;
import com.crscic.incube.data.ITypeParser;
import com.crscic.incube.data.Part;
import com.crscic.incube.log.Log;
import com.k.util.StringUtils;
import com.k.util.filehelper.FileHelper;

/**
 * 
 * @author zhaokai
 * 2018年8月15日 下午4:47:54
 */
public class FileParser implements ITypeParser
{
	@Override
	public byte[] getSendData(List<Object> paramList)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Part part = (Part) paramList.get(0);
		@SuppressWarnings("unchecked")
		Map<String, Integer> fileParamMap = (Map<String, Integer>) paramList.get(1);
		List<Part> childPartList = part.getChildNodeList();
		// 如果后期加入从多个文件中读取内容，此处的子节点数量需要修改为==0
		if (childPartList.size() != 1)
		{
			Log.warn(part.getAttribute().get("name") + "节点中缺少文件配置子节点或子节点数量不唯一");
			return null;
		}

		Part child = childPartList.get(0);
		String filePath = child.getValue();
		if (StringUtils.isNullOrEmpty(filePath))
		{
			Log.warn(part.getAttribute().get("name") + "节点指定的读取文件路径为空");
			return null;
		}
		// 取type确定获取文件中数据的方式
		String type = child.getType();
		FileHelper fh = new FileHelper(filePath);
		String line = null;
		// 设置文件最大行数，因读取的各文件条数需要保持一致，因此仅读取一次文件以提高性能
		if (fileParamMap.get("fileLength") == 0) // 为0即为第一次需要初始化最大行数
			fileParamMap.put("fileLength", (int) fh.length());

		if (fileParamMap.get("fileLength") == 0) // 设置完最大行数仍为0则认为文件为空
		{
			Log.warn("文件为空，文件：" + filePath);
			return null;
		}

		if (type.equals("order"))
		{
			if (fileParamMap.get("fileOrderMem") > fileParamMap.get("fileLength")) // 超过最大行数后从第一行重新开始
				fileParamMap.put("fileOrderMem", 1);
			line = fh.readLine(fileParamMap.get("fileOrderMem"));
		}
		else
		{
			// 若生成的随机数大于文件最大行数，重新计算随机数
			if (fileParamMap.get("fileRandomMem") > fileParamMap.get("fileLength")
					|| fileParamMap.get("fileRandomMem") == 0)
			{
				Random r = new Random();
				fileParamMap.put("fileRandomMem",
						r.nextInt(fileParamMap.get("fileLength")) % (fileParamMap.get("fileLength") - 1) + 1);
			}
			line = fh.readLine(fileParamMap.get("fileRandomMem"));
		}
		if (StringUtils.isNullOrEmpty(line))
		{
			Log.warn("文件中数据错误，文件：" + filePath + "，行号：" + (fileParamMap.get("fileOrderMem")));
			return null;
		}

		byte[] b = null;
		String classString = child.getValueClass();
		b = Data.getByteArrayByClass(line, classString);
		// 长度校验
		String lenString = child.getLen();
		int len = 0;
		if (!StringUtils.isNullOrEmpty(lenString))
			len = Integer.parseInt(lenString);
		// 检查有没有配置fill-byte，若配置了则说明需要填充
		String fillByteString = child.getFillByte();
		if (!StringUtils.isNullOrEmpty(fillByteString))
		{
			String fillDirectionString = child.getFillDirection();
			b = Data.doFill(b, fillByteString, fillDirectionString, len);
		}
		if (len > 0 && len != b.length)
			Log.lengthWarning(part.getAttribute().get("name"));

		return b;
	}

}
