/**
 * 
 */
package com.crscic.incube.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.crscic.incube.data.typeparser.SetPartMem;
import com.crscic.incube.exception.AppException;
import com.crscic.incube.exception.GenerateDataException;
import com.crscic.incube.log.Log;
import com.crscic.incube.setting.Setting;
import com.crscic.incube.setting.SettingEntity;
import com.k.util.ByteUtils;
import com.k.util.CollectionUtils;
import com.k.util.StringUtils;
import com.k.util.filehelper.FileHelper;

/**
 * @author zhaokai
 *
 *         2017年9月12日 下午1:50:37
 */
public class Data
{
	// private int increaseMem = -1;
	private Map<String, Integer> increaseParamMap;
	private Map<String, Integer> fileParamMap;
	private Map<String, Integer> proIncreaseMem;
	// private int fileOrderMem = 1;
	private Map<String, Integer> proFileOrderMem;
	// private int fileRandomMem = 0;
	// private int fileLength = 0;
	private List<PartMem> partMem; // 缓存需要补完的字段信息
	private Map<String, byte[]> lastRandomByteMap;// 所有part上次发送的随机值

	private static SettingEntity typeSetting;
	private static SettingEntity classSetting;
	private static SettingEntity checkSetting;

	public Data(Setting setting)
	{
		fileParamMap = new HashMap<String, Integer>();
		fileParamMap.put("fileLength", 0);
		fileParamMap.put("fileOrderMem", 1);
		Random r = new Random();
		fileParamMap.put("fileRandomMem", r.nextInt(500) % (499) + 1);

		increaseParamMap = new HashMap<String, Integer>();
		increaseParamMap.put("increaseMem", -1);

		proIncreaseMem = new HashMap<String, Integer>();
		proFileOrderMem = new HashMap<String, Integer>();
		partMem = new ArrayList<PartMem>();
		lastRandomByteMap = new HashMap<String, byte[]>();

		typeSetting = setting.getTypeSetting();
		classSetting = setting.getClassSetting();
		checkSetting = setting.getCheckSetting();
	}

	/**
	 * 获取发送的数据
	 * 
	 * @param proConfig
	 *            协议配置信息
	 * @param quoteMap
	 *            引用字段
	 * @param paramMap
	 *            参数字段
	 * @return
	 * @throws GenerateDataException
	 * @author zhaokai
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @create 2017年12月1日 下午5:49:25
	 */
	public byte[] getSendData(ProtocolConfig proConfig, Map<String, byte[]> quoteMap,
			Map<String, Map<String, String>> paramMap)
			throws GenerateDataException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		List<Part> partList = proConfig.getPart();

		// 根据协议初始化顺序读文件时的缓存
		if (proFileOrderMem.containsKey(proConfig.getProtocolName()))
			fileParamMap.put("fileOrderMem", proFileOrderMem.get(proConfig.getProtocolName()));
		else
			fileParamMap.put("fileOrderMem", 1);
		// 根据协议初始化自增时的缓存
		if (proIncreaseMem.containsKey(proConfig.getProtocolName()))
			fileParamMap.put("increaseMem", proIncreaseMem.get(proConfig.getProtocolName()));
		else
			fileParamMap.put("increaseMem", -1);

		// 根据配置开始生成数据
		Map<String, Byte[]> res = new LinkedHashMap<String, Byte[]>();
		for (Part part : partList)
		{
			String attrInfo = part.getAttribute().get("name");
			byte[] b = null;

			b = getPartData(part, quoteMap, paramMap, fileParamMap, increaseParamMap, partMem, lastRandomByteMap);
			res.put(attrInfo, CollectionUtils.byteToByte(b));
		}
		// 补完之前略过的内容
		res = reFilling(res);
		List<Byte> resTmpList = new ArrayList<Byte>();
		for (String key : res.keySet())
		{
			Byte[] fieldValueArray = res.get(key);
			if (fieldValueArray == null)
			{
				Log.warn(key + "字段内容为空");
				continue;
			}
			for (Byte fieldValue : fieldValueArray)
				resTmpList.add(fieldValue);
		}

		byte[] data = new byte[resTmpList.size()];
		for (int i = 0; i < resTmpList.size(); i++)
			data[i] = resTmpList.get(i);

		if (fileParamMap.get("increaseMem") != -1)
			this.proIncreaseMem.put(proConfig.getProtocolName(), fileParamMap.get("increaseMem"));

		fileParamMap.put("fileOrderMem", fileParamMap.get("fileOrderMem") + 1);
		proFileOrderMem.put(proConfig.getProtocolName(), fileParamMap.get("fileOrderMem"));
		fileParamMap.put("fileRandomMem", 0); // 重置为0，下次调用的时候判断要是0的话生成新的随机数，避免重复读取
		return data;
	}

	/**
	 * 对初步生成的数据进行再填充，将诸如长度，校验位等第一次生成时没有进行生成的字段进行补完
	 *
	 * @param res
	 *            第一次生成后的结果数据Map
	 * @return
	 * @author zhaokai
	 * @version 2017年3月3日 下午4:28:20
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private Map<String, Byte[]> reFilling(Map<String, Byte[]> res)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (partMem.size() == 0) // 不需要补完
			return res;
		// 先计算长度，再生成校验
		PartMem m = new PartMem();
		m.setName("length");
		int lenIndex = partMem.indexOf(m);
		if (lenIndex > -1)
		{
			m = partMem.get(lenIndex);
			byte[] t = getLengthData(m, res);
			res.put(m.getName(), CollectionUtils.byteToByte(t));

			partMem.remove(m);
		}

		// 循环填充其他需要补完的字段，目前主要是校验
		for (PartMem mem : partMem)
		{
			byte[] b = null;
			if (mem.getType().equals("check"))
				b = getCheckData(mem, res);
			res.put(mem.getName(), CollectionUtils.byteToByte(b));
		}

		partMem.clear();
		return res;
	}

	/**
	 * 生成校验位数据
	 *
	 * @param type
	 * @param key
	 * @param content
	 * @return
	 * @author zhaokai
	 * @version 2017年3月3日 下午5:47:42
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private byte[] getCheckData(PartMem mem, Map<String, Byte[]> content)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		List<String> rangeList = mem.getRangeList();
		List<Byte> data = new ArrayList<Byte>();
		for (String range : rangeList)
		{
			Byte[] rangeValue = content.get(range);
			if (rangeValue == null)
			{
				Log.warn(range + "字段不在协议配置中");
				return null;
			}
			for (Byte b : rangeValue)
				data.add(b);
		}

		byte[] b = null;
		String checkType = mem.getChildType();
		ICheckParser checkParser = getCheckParser(checkType);
		b = checkParser.getCheckData(data);
		return b;
	}

	/**
	 * 根据check节点的内容调用对应的校验算法，并返回校验码
	 * 
	 * @param checkType
	 * @return
	 * @author zhaokai
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @create 2018年8月16日 下午5:27:00
	 */
	private ICheckParser getCheckParser(String checkType)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		return (ICheckParser) Class.forName(checkSetting.getMethodClass(checkType)).newInstance();
	}

	/**
	 * 生成长度数据
	 *
	 * @return
	 * @author zhaokai
	 * @version 2017年3月3日 下午4:50:46
	 */
	private byte[] getLengthData(PartMem mem, Map<String, Byte[]> content)
	{
		int len = 0;
		List<String> rangeList = mem.getRangeList();
		for (String range : rangeList)
		{
			if (range.equals("length"))
				len += 4; // 若包含字段自己本身的话，则长度加4字节。（4字节这事儿先这么定，如果需要改为读一下字段长度)
			else
				len += content.get(range).length;
		}
		// 根据子节点中的type字段判断，长度数据的计算方式
		byte[] lenData = null;
		if (mem.getChildType().equals("length"))
			lenData = ByteUtils.intToByteArray(len);
		else if (mem.getChildType().equals("lenid+lchksum"))
			lenData = getLenIdAndLChkSum(len);
		return lenData;
	}

	/**
	 * 生成lenid + lchksum这种形式的长度数据
	 * 
	 * @return
	 * @author ken_8 2017年9月23日 下午10:42:18
	 */
	private byte[] getLenIdAndLChkSum(int len)
	{
		byte[] lenid = new byte[3];
		lenid[0] = (byte) (len >> 8 & 0xF);
		lenid[1] = (byte) (len >> 4 & 0xF);
		lenid[2] = (byte) (len & 0xF);
		byte lchkSum = Encryption.getLChkSum(lenid);
		byte[] lenData = new byte[4];
		lenData[0] = lchkSum;
		for (int i = 0; i < lenid.length; i++)
			lenData[i + 1] = Encryption.ASCII[lenid[i]];
		return lenData;
	}

	/**
	 * 递归调用，组装part节点中的内容
	 *
	 * @param part
	 *            字段信息
	 * @quoteMap 引用自请求中的信息
	 * @paramMap 外部参数信息
	 * @return
	 * @author zhaokai
	 * @version 2017年3月1日 上午10:17:00
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws AppException
	 */
	public static byte[] getPartData(Part part, Map<String, byte[]> quoteMap, Map<String, Map<String, String>> paramMap,
			Map<String, Integer> fileParamMap, Map<String, Integer> increaseParamMap, List<PartMem> partMem,
			Map<String, byte[]> lastRandomByteMap)
			throws GenerateDataException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		String typeString = part.getType();
		byte[] b = null;
		String nodeName = part.getAttribute().get("name");

		ITypeParser typeParser = getTypeParser(typeString);
		List<String> paramList = typeSetting.getParam(typeString);
		List<Object> params = new ArrayList<Object>();
		for (String param : paramList)
		{
			if (param.equals("com.crscic.incube.data.Part"))
				params.add(part);
			if (param.equals("quoteMap"))
				params.add(quoteMap);
			if (param.equals("paramMap"))
				params.add(paramMap);
			if (param.equals("fileParamMap"))
				params.add(fileParamMap);
			if (param.equals("increaseParamMap"))
				params.add(increaseParamMap);
			if (param.equals("partMem"))
				params.add(partMem);
			if (param.equals("lastRandomByteMap"))
				params.add(lastRandomByteMap);
		}

		b = typeParser.getSendData(params);
		
		//如果只缓存不处理的话，强制返回null。目前仅length和校验字段会缓存数据
		if (typeParser instanceof SetPartMem)
			return null;

		if (b == null)
			GenerateDataException.nullNodeValueException(nodeName, "type");
		return b;

	}

	/**
	 * 根据type节点的值返回对应的处理类
	 * 
	 * @param typeString
	 * @return
	 * @author zhaokai
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @create 2018年8月14日 下午6:28:23
	 */
	private static ITypeParser getTypeParser(String typeString)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		return (ITypeParser) Class.forName(typeSetting.getMethodClass(typeString)).newInstance();
	}

	/**
	 * 根据外部参数返回生成的字段数据
	 * 注：取type判断并生成数据的逻辑与getPartData()方法重复，但有差别。此处仅支持random关键字（目前），且先判断class的类型，再进行随机。随机方式支持“，”分隔（目前）
	 * 
	 * @param part
	 *            字段信息
	 * @param paramMap
	 *            外部参数
	 * @return
	 * @author zhaokai
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @create 2017年12月1日 下午5:53:23
	 */
	@Deprecated
	private byte[] getParameterData(Part part, Map<String, Map<String, String>> paramMap)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (paramMap == null || paramMap.keySet().size() == 0)
			return null;
		String fieldName = part.getAttribute().get("name");
		byte[] b = null;
		if (paramMap.containsKey(fieldName))
		{
			Map<String, String> paramAttrMap = paramMap.get(fieldName);
			if (paramAttrMap.get("type").toLowerCase().trim().equals("aptotic"))
				b = getByteArrayByClass(paramAttrMap.get("value"), paramAttrMap.get("class"));
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

				b = getRandomData(tempRandomPart);
			}
			else
				b = getByteArrayByClass(paramAttrMap.get("value"), paramAttrMap.get("class"));

		}
		return b;
	}

	/**
	 * 生成自增的数据
	 * 
	 * @param part
	 * @return
	 * @author zhaokai
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @create 2017年10月13日 下午5:55:14
	 */
	@Deprecated
	private byte[] getIncreaseData(Part part)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
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
		b = getByteArrayByClass(valueString, classString);
		// 检查有没有配置fill-byte，若配置了则说明需要填充
		String fillByteString = childPart.getFillByte();
		int len = 0;
		String lenString = childPart.getLen();
		if (!StringUtils.isNullOrEmpty(lenString))
			len = Integer.parseInt(lenString);
		if (!StringUtils.isNullOrEmpty(fillByteString))
		{
			String fillDirectionString = childPart.getFillDirection();
			b = doFill(b, fillByteString, fillDirectionString, len);
		}

		if (len > 0 && b.length != len)
			Log.warn(part.getAttribute().get("name") + "节点生成数据的长度与配置中不一致");

		// 每使用一次自增值
		int step = Integer.parseInt(childPart.getSplit());
		increaseParamMap.put("increaseMem", increaseParamMap.get("increaseMem") + step);
		return b;
	}

	@Deprecated
	private byte[] getAptoticData(Part part)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		byte[] b;
		// 获取value的值
		String valueString = part.getValue();
		// xml中value是16进制字符串，将其装换为byte
		String classString = part.getValueClass();
		b = getByteArrayByClass(valueString, classString);
		String lenString = part.getLen();
		int len = 0;
		if (!StringUtils.isNullOrEmpty(lenString))
			len = Integer.parseInt(lenString);
		// 检查有没有配置fill-byte，若配置了则说明需要填充
		String fillByteString = part.getFillByte();
		if (!StringUtils.isNullOrEmpty(fillByteString))
		{
			String fillDirectionString = part.getFillDirection();
			b = doFill(b, fillByteString, fillDirectionString, len);
		}
		if (len > 0 && b.length != len)
			Log.warn(part.getAttribute().get("name") + "节点生成数据的长度与配置中不一致");
		return b;
	}

	/**
	 * 读取文件类型生成数据
	 *
	 * @param part
	 * @return
	 * @author zhaokai
	 * @version 2017年2月14日 下午7:47:20
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Deprecated
	private byte[] getFileData(Part part) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
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
		b = getByteArrayByClass(line, classString);
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
			b = doFill(b, fillByteString, fillDirectionString, len);
		}
		if (len > 0 && len != b.length)
			Log.lengthWarning(part.getAttribute().get("name"));

		return b;
	}

	@Deprecated
	private byte[] getGenerateData(Part part, Map<String, byte[]> quoteMap, Map<String, Map<String, String>> paramMap)
			throws GenerateDataException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		List<Part> childPartList = part.getChildNodeList();
		String splitString = null;
		byte[] split = null;
		splitString = getSplitString(part.getSplit());
		if (!StringUtils.isNullOrEmpty(splitString))
			split = ByteUtils.getBytes(splitString);
		List<Byte> res = new ArrayList<Byte>();
		for (Part childPart : childPartList)
		{
			byte[] b = getPartData(childPart, quoteMap, paramMap, fileParamMap, increaseParamMap, partMem,
					lastRandomByteMap);
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

	@Deprecated
	private byte[] getTimeData(Part part)
			throws GenerateDataException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		String valueString = part.getValue();
		if (StringUtils.isNullOrEmpty(valueString))
			GenerateDataException.nullNodeValueException(part.getAttribute().get("name"), "value");
		String classString = part.getValueClass();
		if (StringUtils.isNullOrEmpty(classString))
			GenerateDataException.nullNodeValueException(part.getAttribute().get("name"), "class");

		SimpleDateFormat f = new SimpleDateFormat(valueString);
		String timeString = f.format(new Date());

		byte[] b = getByteArrayByClass(timeString, classString);
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
			b = doFill(b, fillByteString, fillDirectionString, len);
		}

		if (len > 0 && (b.length != len || b.length > len))
			Log.lengthWarning(part.getAttribute().get("name"));
		return b;
	}

	/**
	 * 生成随机类型的数据（type=random）
	 *
	 * @param part
	 * @return
	 * @author zhaokai
	 * @version 2017年3月1日 下午4:29:40
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Deprecated
	private byte[] getRandomData(Part part)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
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
		splitString = getSplitString(splitString);
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
			b = getByteArrayByClass(res, classString);
			// 长度校验
			String lenString = part.getLen();
			if (!StringUtils.isNullOrEmpty(lenString))
				len = Integer.parseInt(lenString);

			// 检查有没有配置fill-byte，若配置了则说明需要填充
			String fillByteString = part.getFillByte();
			if (!StringUtils.isNullOrEmpty(fillByteString))
			{
				String fillDirectionString = part.getFillDirection();
				b = doFill(b, fillByteString, fillDirectionString, len);
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

	/**
	 * 设置需要补完时生成字段的参数缓存
	 *
	 * @param part
	 * @return
	 * @author zhaokai
	 * @version 2017年3月3日 下午3:11:29
	 */
	@Deprecated
	private void setPartMem(Part part) throws GenerateDataException
	{
		// value中子part检查
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
		String splitString = getSplitString(childPart.getSplit());
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
	}

	/**
	 * 获取分隔符，若分隔符是特殊字符，则返回特殊字符，否则原样返回
	 *
	 * @param split
	 * @return
	 * @author zhaokai
	 * @version 2017年3月20日 下午4:36:27
	 */
	public static String getSplitString(String split)
	{
		String res = split;
		if (StringUtils.isNullOrEmpty(res))
			return res;
		if (res.toLowerCase().equals("tab"))
			return "\t";
		return res;
	}

	public static IClassParser getClassParser(String classValue)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		return (IClassParser) Class.forName(classSetting.getMethodClass(classValue)).newInstance();
	}

	/**
	 * 根据class节点的内容对数据进行处理并返回处理后的byte数组
	 *
	 * @param src
	 * @param classString
	 * @return
	 * @author zhaokai
	 * @version 2017年4月13日 上午10:07:42
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static byte[] getByteArrayByClass(String src, String classString)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		byte[] b = null;
		if (classString == null)
		{
			b = ByteUtils.getBytes(src);
		}
		classString = classString.trim().toLowerCase();

		IClassParser classParser = getClassParser(classString);
		b = classParser.getByteArrayByClass(src);

		return b;
	}

	/**
	 * 长度不足时填充指定字节补足长度
	 *
	 * @param value
	 *            待填充的内容
	 * @param fillByteString
	 *            填充时使用的字节，16进制字符串形式
	 * @param fillDirect
	 *            填充方向，在左侧填充还是右侧填充
	 * @param len
	 *            填充后的长度
	 * @return
	 * @author zhaokai
	 * @version 2017年4月5日 下午4:29:44
	 */
	public static byte[] doFill(byte[] value, String fillByteString, String fillDirect, int len)
	{
		if (len == 0)
			return value;
		int fillNum = len - value.length;
		byte[] res = new byte[len];
		byte[] b = ByteUtils.hexStringToBytes(fillByteString);
		if (b.length > 1)
			Log.warn("填充字符只能为1字节");
		byte fillCharByte = b[0];

		if (fillDirect.equals("right"))
		{
			for (int i = 0; i < len; i++)
			{
				if (i < value.length)
					res[i] = value[i];
				else
					res[i] = fillCharByte;
			}
		}
		else
		{
			for (int i = 0; i < len; i++)
			{
				if (i < fillNum)
					res[i] = fillCharByte;
				else
					res[i] = value[i - fillNum];
			}
		}
		return res;
	}

}
