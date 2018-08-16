package com.crscic.incube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;

import com.crscic.incube.config.ConfigHandler;
import com.crscic.incube.config.ParseSetting;
import com.crscic.incube.config.Request;
import com.crscic.incube.config.Response;
import com.crscic.incube.connector.ComConnector;
import com.crscic.incube.connector.Connector;
import com.crscic.incube.connector.TcpConnector;
import com.crscic.incube.connector.UdpConnector;
import com.crscic.incube.data.Data;
import com.crscic.incube.data.ProtocolConfig;
import com.crscic.incube.exception.GenerateDataException;
import com.crscic.incube.exception.ParseXMLException;
import com.crscic.incube.log.Log;
import com.crscic.incube.setting.ComSetting;
import com.crscic.incube.setting.InterChangeSetting;
import com.crscic.incube.setting.ReplySetting;
import com.crscic.incube.setting.SendSetting;
import com.crscic.incube.setting.Setting;
import com.crscic.incube.setting.SettingHandler;
import com.crscic.incube.setting.SocketSetting;
import com.crscic.incube.xmlhelper.XmlHelper;
import com.k.util.StringUtils;

public class DataFactory
{
	private static ConfigHandler config;
	private static SettingHandler setting;
	private Connector connector;
	private XmlHelper configXml;

	public Setting initSetting()
	{
		if (setting == null)
			setting = new SettingHandler();
		setting.initTypeSetting();
		setting.initClassSetting();
		setting.initCheckSetting();
		Setting res = new Setting();
		res.setTypeSetting(setting.getTypeSetting());
		res.setClassSetting(setting.getClassSetting());
		res.setCheckSetting(setting.getCheckSetting());
		return res;
	}
	
	/**
	 * 获取多线程启动时的参数信息
	 * 
	 * @param paramFile
	 * @return
	 * @throws DocumentException
	 * @author zhaokai
	 * @throws ParseXMLException
	 * @create 2017年12月13日 下午5:46:44
	 */
	public Map<String, Map<String, Map<String, String>>> getConnParam() throws ParseXMLException
	{
		if (config == null)
			config = new ConfigHandler(configXml);
		return config.getParamInfo();
	}

	public ParseSetting getParseSetting(String configFilePath) throws DocumentException
	{
		XmlHelper xml = new XmlHelper(configFilePath);
		ConfigHandler config = new ConfigHandler(xml);
		ParseSetting parseSetting = config.getParseSetting();
		return parseSetting;
	}

	public DataFactory(String configPath)
	{
		configXml = new XmlHelper();
		Log.debug("加载配置文件：" + configPath);
		configXml.loadXml(configPath);
	}

	/**
	 * 获取连接器
	 * 
	 * @return
	 * @author ken_8 2017年9月12日 上午12:25:33
	 */
	public Connector getConnector(String connConf)
	{
		if (config == null)
			config = new ConfigHandler(configXml);
		if (config.getConnectType().toLowerCase().equals("tcp"))
		{
			SocketSetting sockSetting = config.getSocketConfig();
			if (!StringUtils.isNullOrEmpty(connConf))
			{
				if (sockSetting.getType().equals("server"))
					sockSetting.setIp(connConf);
				else
					sockSetting.setLocalIp(connConf);
			}
			connector = new TcpConnector(sockSetting);
		}
		else if (config.getConnectType().toLowerCase().equals("com"))
		{
			ComSetting comSetting = config.getComConfig();
			if (!StringUtils.isNullOrEmpty(connConf))
				comSetting.setPort(connConf);
			connector = ComConnector.getInstance(comSetting);
		}
		else if (config.getConnectType().toLowerCase().equals("udp"))
		{
			SocketSetting sockSetting = config.getSocketConfig();
			if (!StringUtils.isNullOrEmpty(connConf))
			{
				if (sockSetting.getType().equals("server"))
					sockSetting.setIp(connConf);
				else
					sockSetting.setLocalIp(connConf);
			}
			connector = new UdpConnector(sockSetting);
		}

		return connector;
	}

	/**
	 * 获取发送接口的配置信息
	 * 
	 * @return
	 * @author zhaokai
	 * @date 2017年9月28日 下午10:53:15
	 */
	public SendSetting getSendSetting()
	{
		if (config == null)
			config = new ConfigHandler(configXml);
		return config.getSendSetting();
	}

	/**
	 * 获取回复接口的配置信息
	 * 
	 * @return
	 * @author zhaokai
	 * @date 2017年9月28日 下午10:52:52
	 */
	public ReplySetting getReplySetting()
	{
		if (config == null)
			config = new ConfigHandler(configXml);
		return config.getReplySetting();
	}

	public InterChangeSetting getInterChangeSetting()
	{
		if (config == null)
			config = new ConfigHandler(configXml);
		return config.getInterChangeSetting();
	}

	/**
	 * 生成发送数据
	 * 
	 * @author zhaokai 2017年9月12日 下午12:38:22
	 * @throws GenerateDataException 报文生成异常
	 * @throws IllegalAccessException  实例化type、class、check处理类对象异常
	 * @throws InstantiationException 实例化type、class、check处理类对象异常
	 * @throws ClassNotFoundException 未找到type、class、check对应的处理类
	 */
	public byte[] getSendData(Setting setting, ProtocolConfig proConfig, Map<String, Map<String, String>> paramMap)
			throws GenerateDataException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Data data = new Data(setting);
		return data.getSendData(proConfig, new HashMap<String, byte[]>(), paramMap);
	}

	/**
	 * 获取协议配置文件中的协议字段配置信息
	 * 
	 * @param settingFilePath
	 * @param responseList
	 * @return
	 * @author zhaokai
	 * @throws DocumentException
	 * @throws ParseXMLException
	 * @date 2017年9月29日 下午6:36:05
	 */
	public static <T> List<ProtocolConfig> getDataConfig(String settingFilePath, List<T> entityList)
			throws ParseXMLException
	{
		List<ProtocolConfig> proCfgList = null;
		if (entityList.size() == 0)
			return proCfgList;
		List<String> protocolList = new ArrayList<String>();
		if (entityList.get(0).getClass().getSimpleName().equals("String"))
		{
			for (int i = 0; i < entityList.size(); i++)
				protocolList.add((String) entityList.get(i));
		}
		else if (entityList.get(0).getClass().getSimpleName().equals("Response"))
		{
			for (int i = 0; i < entityList.size(); i++)
			{
				Response response = (Response) entityList.get(i);
				protocolList.add(response.getProtocol());
			}
		}
		else if (entityList.get(0).getClass().getSimpleName().equals("Request"))
		{
			for (int i = 0; i < entityList.size(); i++)
			{
				Request request = (Request) entityList.get(i);
				protocolList.add(request.getSendProtocol());
			}
		}

		XmlHelper dataXml = new XmlHelper();
		dataXml.loadXml(settingFilePath);
		ConfigHandler dataConfig = new ConfigHandler(dataXml);

		proCfgList = dataConfig.getProtocolConfigList(protocolList);
		return proCfgList;
	}

	/**
	 * 获取deviceList.xml中配置的设备和设备对应的配置文件信息
	 * 
	 * @return
	 * @author ken_8
	 * @create 2017年10月11日 下午11:45:05
	 */
	public static Map<String, String> getDeviceInfo()
	{
		return ConfigHandler.getDeviceInfo();
	}

}
