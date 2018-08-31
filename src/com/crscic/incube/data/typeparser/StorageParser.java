package com.crscic.incube.data.typeparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.crscic.incube.config.ConfigHandler;
import com.crscic.incube.data.Data;
import com.crscic.incube.data.ITypeParser;
import com.crscic.incube.entity.Part;
import com.crscic.incube.entity.PartMem;
import com.crscic.incube.entity.Storage;
import com.crscic.incube.exception.GenerateDataException;
import com.crscic.incube.xmlhelper.XmlHelper;
import com.k.util.CollectionUtils;

/**
 * 
 * @author zhaokai 2018年8月30日 下午3:15:59
 */
public class StorageParser implements ITypeParser
{
	private List<Storage> storageList;

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
		List<PartMem> partMem = (List<PartMem>) paramList.get(5);
		@SuppressWarnings("unchecked")
		Map<String, byte[]> lastRandomByteMap = (Map<String, byte[]>) paramList.get(6);

		XmlHelper xml = new XmlHelper(part.getValue());
		storageList = getStoragePart(xml.getSingleElement("/storage"));
		// 请求中的id
		byte[] quote = quoteMap.get(part.getAttribute().get("name"));
		// 遍历param获得节点name
		String storageName = null;
		for (String paramKey : paramMap.keySet())
		{
			Part paramPart = paramMap.get(paramKey);
			byte[] paramValue = Data.getPartData(paramPart, quoteMap, paramMap, fileParamMap, increaseParamMap, partMem,
					lastRandomByteMap);
			if (paramPart.getAttribute().get("name").equals("envFolderId"))
			if (CollectionUtils.isSameArray(paramValue, quote))
			{
				storageName = paramKey;
				break;
			}
		}
		if (storageName == null)
			return null;

		// 根据对应storage组装数据
		Storage storage = getStorageByName(storageName);
		List<Byte> resList = new ArrayList<Byte>();
		for (Part storagePart : storage.getPartList())
		{
			byte[] storagePartBytes = Data.getPartData(storagePart, quoteMap, paramMap, fileParamMap, increaseParamMap,
					partMem, lastRandomByteMap);
			resList = CollectionUtils.copyArrayToList(resList, storagePartBytes);
		}
		
		return CollectionUtils.toByteArray(resList);
	}

	private List<Storage> getStoragePart(Element rootStorageEle)
	{
		List<Storage> storageList = new ArrayList<Storage>();
		List<Element> storageEleList = XmlHelper.getElements(rootStorageEle);
		for (Element storageEle : storageEleList)
		{
			Storage storage = new Storage();
			storage.setStorageName(storageEle.getName());
			List<Part> partList = ConfigHandler.fillPart(storageEle);
			storage.setPartList(partList);
			storageList.add(storage);
		}

		return storageList;
	}

	private Storage getStorageByName(String name)
	{
		for (Storage storage : storageList)
		{
			if (storage.getStorageName().equals(name))
				return storage;
		}
		return null;
	}

}
