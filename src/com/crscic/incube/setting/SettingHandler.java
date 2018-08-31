package com.crscic.incube.setting;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.crscic.incube.entity.SettingEntity;
import com.crscic.incube.log.Log;
import com.crscic.incube.xmlhelper.XmlHelper;

/**
 * 
 * @author zhaokai
 * 2018年8月14日 下午3:20:28
 */
public class SettingHandler
{
	public static final String TYPE_SETTING_FILE = "config/setting/type.xml";
	public static final String CLASS_SETTING_FILE = "config/setting/class.xml";
	public static final String CHECK_SETTING_FILE = "config/setting/check.xml";
	
	private SettingEntity typeSetting = new SettingEntity();
	private SettingEntity classSetting = new SettingEntity();
	private SettingEntity checkSetting = new SettingEntity();
	
	public SettingHandler()
	{
		
	}
	
	public void initTypeSetting()
	{
		XmlHelper typeXml = new XmlHelper();
		Log.debug("加载typeSetting");
		typeXml.loadXml(TYPE_SETTING_FILE);
		List<Element> typeEleList = typeXml.getElements("/root/type");
		for (Element typeEle : typeEleList)
		{
			String typeName = typeEle.elementText("name");
			typeSetting.addMethod(typeName, typeEle.elementText("class"));
			List<Element> paramEleList = XmlHelper.getElements(typeEle.element("parameters"));
			Log.debug(typeName + ": ");
			List<String> paramList = new ArrayList<String>();
			if (paramEleList.size() == 0)
			{
				Log.debug("\t无参");
			}
			else
			{
				for (Element paramEle : paramEleList)
				{
					paramList.add(paramEle.getStringValue());
					Log.debug("\t增加参数-" + paramEle.getStringValue());
				}
			}
			typeSetting.addParam(typeName, paramList);
		}
	}
	
	public void initClassSetting()
	{
		XmlHelper classXml = new XmlHelper();
		Log.debug("加载classSetting");
		classXml.loadXml(CLASS_SETTING_FILE);
		List<Element> classEleList = classXml.getElements("/root/class");
		for (Element classEle : classEleList)
		{
			String className = classEle.elementText("name");
			classSetting.addMethod(className, classEle.elementText("class"));
		}
	}
	
	public void initCheckSetting()
	{
		XmlHelper checkXml = new XmlHelper();
		Log.debug("加载checkSetting");
		checkXml.loadXml(CHECK_SETTING_FILE);
		List<Element> checkEleList = checkXml.getElements("/root/check");
		for (Element checkEle : checkEleList)
		{
			String checkName = checkEle.elementText("name");
			checkSetting.addMethod(checkName, checkEle.elementText("class"));
		}
	}

	public SettingEntity getTypeSetting()
	{
		return this.typeSetting;
	}
	
	
	public static void main(String[] args)
	{
		SettingHandler sh = new SettingHandler();
		sh.initTypeSetting();
		SettingEntity ts = sh.getTypeSetting();
		System.out.println(ts.getParam("aptotic"));
	}

	public SettingEntity getClassSetting()
	{
		return classSetting;
	}

	public void setClassSetting(SettingEntity classSetting)
	{
		this.classSetting = classSetting;
	}

	public SettingEntity getCheckSetting()
	{
		return checkSetting;
	}

	public void setCheckSetting(SettingEntity checkSetting)
	{
		this.checkSetting = checkSetting;
	}
}
