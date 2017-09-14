package com.crscic.interfacetesttool;

import java.util.HashMap;

import org.dom4j.DocumentException;

import com.crscic.interfacetesttool.config.ConfigHandler;
import com.crscic.interfacetesttool.config.ProtocolSetting;
import com.crscic.interfacetesttool.connector.ComConnector;
import com.crscic.interfacetesttool.connector.Connector;
import com.crscic.interfacetesttool.connector.SocketConnector;
import com.crscic.interfacetesttool.data.Data;
import com.crscic.interfacetesttool.data.ProtocolConfig;
import com.crscic.interfacetesttool.exception.GenerateDataException;
import com.crscic.interfacetesttool.exception.ParseXMLException;
import com.crscic.interfacetesttool.log.Log;
import com.crscic.interfacetesttool.xmlhelper.XmlHelper;

public class DataFactory
{
	private ConfigHandler setting;
	private Connector connector;
	private XmlHelper configXml;

	public DataFactory(String configPath) throws DocumentException
	{
		configXml = new XmlHelper();
		Log.info("加载配置文件：" + configPath);
		configXml.loadXml(configPath);
	}

	/**
	 * 获取连接器
	 * 
	 * @return
	 * @author ken_8 2017年9月12日 上午12:25:33
	 */
	public Connector getConnector() throws DocumentException
	{
		if (setting.getConnectType().toLowerCase().equals("socket"))
			connector = new SocketConnector(setting.getSocketConfig());
		else if (setting.getConnectType().toLowerCase().equals("com"))
			connector = new ComConnector(setting.getComConfig());

		return connector;
	}
	
	/**
	 * 获取程序配置
	 * @return
	 * @throws ParseXMLException
	 * @author zhaokai
	 * 2017年9月13日 下午5:31:15
	 */
	public ProtocolSetting getProtocolSetting() throws ParseXMLException
	{
		setting = new ConfigHandler(configXml);
		return setting.getProtocolSetting();
	}
	
	/**
	 * 生成发送数据
	 * 
	 * @author zhaokai 2017年9月12日 下午12:38:22
	 * @throws GenerateDataException
	 */
	public byte[] getSendData(ProtocolConfig proConfig) throws GenerateDataException
	{
		Data data = new Data();
		return data.getSendData(proConfig, new HashMap<String, byte[]>());
	}

	/**
	 * 获取协议配置
	 * @return
	 * @throws DocumentException
	 * @throws ParseXMLException
	 * @author ken_8
	 * 2017年9月14日 上午12:30:20
	 */
	public ConfigHandler getDataConfig(ProtocolSetting proSetting) throws DocumentException, ParseXMLException
	{
		XmlHelper dataXml = new XmlHelper();
		dataXml.loadXml(proSetting.getProFilePath());
		ConfigHandler dataConfig = new ConfigHandler(dataXml);
		return dataConfig;
	}
}
