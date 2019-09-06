package com.crscic.incube.connector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.crscic.incube.entity.SocketSetting;
import com.crscic.incube.exception.ConnectException;
import com.crscic.incube.log.Log;
import com.k.util.CollectionUtils;
import com.k.util.StringUtils;

/**
 * 
 * @author zhaokai 2018年4月24日 上午11:23:34
 */
public class UdpConnector implements Connector
{
	protected String type;
	protected String ip;
	protected String localIp;
	protected int port;
	protected int localPort;
	protected boolean isServer = false;
	protected boolean keepAlive;
	protected boolean isReconnect;

	private DatagramSocket udpConnector;

	@Override
	public void send(byte[] data) throws ConnectException
	{
		if (udpConnector == null)
			openConnect();
		try
		{
			InetAddress destAddr = InetAddress.getByName(ip);
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, destAddr, port);
			udpConnector.send(sendPacket);
		}
		catch (IOException e)
		{
			throw new ConnectException(e.getMessage());
		}
		finally
		{
			if (!keepAlive)
			{
				closeConnect();
			}
		}
	}

	@Override
	public List<Byte> receive()
	{
		List<Byte> recvData = null;
		try
		{
			recvData = new ArrayList<Byte>();
			int len = 1024;
			byte[] buff = new byte[len];
			DatagramPacket recePacket = new DatagramPacket(buff, buff.length);
			udpConnector.receive(recePacket);// 根据receive方法说明，当接收数据长度超过数组长度时，整个消息将被弃用
			CollectionUtils.copyArrayToList(recvData, buff, recePacket.getLength());
			// }
		}
		catch (IOException e)
		{
			Log.error("无法获取输入数据", e);
		}
		return recvData;
	}

	@Override
	public void openConnect() throws ConnectException
	{
		try
		{
			if (udpConnector != null)
			{
				closeConnect();
				udpConnector = null;
			}

			if (type.toLowerCase().equals("client"))
			{
				synchronized (this)
				{
					isServer = false;
				}

				Log.debug("使用IP地址：" + this.localIp + ", 开始连接服务: " + ip + ":" + port);
				if (this.localIp == null || this.localIp.equals(""))
				{
					udpConnector = new DatagramSocket();
				}
				else
				{
					InetAddress inet = InetAddress.getByName(this.localIp);
					SocketAddress sockAddr = new InetSocketAddress(inet, this.localPort);
					udpConnector = new DatagramSocket(sockAddr);
				}
				Log.debug("连接成功");
			}
			else
			{
				synchronized (this)
				{
					isServer = true;
				}
				Log.debug("启动服务:" + ip + ":" + port + " ...");
				InetAddress inet = InetAddress.getByName(ip);
				SocketAddress sockAddr = new InetSocketAddress(inet, port);
				udpConnector = new DatagramSocket(sockAddr);
			}
		}
		catch (

		UnknownHostException e)
		{
			Log.error("错误的主机地址", e);
			throw new ConnectException();
		}
		catch (IOException e)
		{
			Log.error("接口打开失败", e);
			throw new ConnectException();
		}
	}

	@Override
	public boolean isOpen()
	{
		if (udpConnector == null || udpConnector.isClosed())
			return false;
		return true;
	}

	@Override
	public void closeConnect() throws ConnectException
	{
		if (udpConnector != null)
			udpConnector.close();
		if (keepAlive)
		{
			// 如果是长连接的话，那么输入输出流应该是打开状态的，是否需要关闭一下呢？
		}
		isServer = false;
		udpConnector = null;
	}

	@Override
	public String getRemoteIp()
	{
		return udpConnector.getRemoteSocketAddress().toString().substring(1);
	}

	@Override
	public String getLocalIp()
	{
		return localIp;
	}

	@Override
	public boolean isServer()
	{
		return isServer;
	}

	@Override
	public String getType()
	{
		return "udp";
	}

	public UdpConnector(SocketSetting sockCfg)
	{
		this.type = sockCfg.getType();
		this.ip = sockCfg.getIp();
		if (StringUtils.isNullOrEmpty(sockCfg.getLocalIp()))
		{
			try
			{
				InetAddress inet = InetAddress.getLocalHost();
				this.localIp = inet.getHostAddress();
			}
			catch (UnknownHostException e)
			{
				Log.error("获取本地Ip地址出错", e);
			}
		}
		else
		{
			this.localIp = sockCfg.getLocalIp();
		}

		this.port = Integer.parseInt(sockCfg.getPort());
		if (sockCfg.getLocalPort() == null || sockCfg.getLocalPort().equals(""))
			this.localPort = new Random().nextInt(55534) + 10000;
		else
			this.localPort = Integer.parseInt(sockCfg.getLocalPort());

		this.keepAlive = sockCfg.isKeepAlive();
		this.isReconnect = sockCfg.isReconnect();

		String logInfo;
		if (this.type.equals("server"))
			logInfo = "监听端口：" + this.port + ", 连接方式：" + (this.keepAlive ? "长连接" : "短连接");
		else
			logInfo = "服务器IP：" + this.ip + ", 服务器端口：" + this.port + ", 本地IP：" + this.localIp + "，本地端口：" + this.localPort
					+ "，连接方式：" + (this.keepAlive ? "长连接" : "短连接");

		Log.debug("接口类型为Socket-" + this.type + ", " + logInfo);
	}

	public boolean isReconnect()
	{
		return isReconnect;
	}
}
