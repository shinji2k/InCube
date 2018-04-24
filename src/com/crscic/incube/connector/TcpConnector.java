/**
 * 
 */
package com.crscic.incube.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.crscic.incube.exception.ConnectException;
import com.crscic.incube.log.Log;
import com.crscic.incube.config.SocketSetting;
import com.k.util.CollectionUtils;
import com.k.util.StringUtils;

/**
 * 
 * @author zhaokai 2017年9月7日 下午3:12:32
 */
public class TcpConnector implements Connector
{
	protected String type;
	protected String ip;
	protected String localIp;
	protected int port;
	protected int localPort;
	protected boolean isServer = false;
	protected boolean keepAlive;

	protected Socket connector;
	protected ServerSocket server;

	public TcpConnector(SocketSetting sockCfg)
	{
		this.type = sockCfg.getType();
		this.ip = sockCfg.getIp();
		if (StringUtils.isNullOrEmpty(sockCfg.getLocalIp()))
		{
			try
			{
				InetAddress inet= InetAddress.getLocalHost();
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

		this.keepAlive = sockCfg.getKeepAlive();

		String logInfo;
		if (this.type.equals("server"))
			logInfo = "监听端口：" + this.port + ", 连接方式：" + (this.keepAlive ? "长连接" : "短连接");
		else
			logInfo = "服务器IP：" + this.ip + ", 服务器端口：" + this.port + ", 本地IP：" + this.localIp + "，本地端口：" + this.localPort
					+ "，连接方式：" + (this.keepAlive ? "长连接" : "短连接");

		Log.debug("接口类型为Socket-" + this.type + ", " + logInfo);
	}

	@Override
	public void send(byte[] data) throws ConnectException
	{
		if (connector == null)
			openConnect();

		OutputStream os = null;
		try
		{
			os = connector.getOutputStream();
			os.write(data, 0, data.length);
			os.flush();
		}
		catch (IOException e)
		{
			ConnectException.throwWriteErr(e);
		}
		finally
		{
			if (!keepAlive)
			{
				if (os != null)
				{
					try
					{
						os.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}

				closeConnect();
			}
		}
	}

	public void sendString(String str) throws IOException
	{
		OutputStream os = connector.getOutputStream();
		System.out.println("发送：" + str);
		os.write(str.getBytes());
		os.flush();
	}

	@Override
	public List<Byte> receive()
	{
		InputStream is = null;
		List<Byte> recvData = null;
		try
		{
			is = connector.getInputStream();
			recvData = new ArrayList<Byte>();
			int len = 1024;
			// while ((len = is.available()) > 0)
			// {
			// 虽然几率较低，但仍有可能在从while的条件判断到read之间有新的数据进来，造成少取了数据
			byte[] buff = new byte[len];
			int recvLen = is.read(buff, 0, len);
			if (recvLen != -1)
				CollectionUtils.copyArrayToList(recvData, buff, recvLen);
			// }
		}
		catch (IOException e)
		{
			Log.error("无法获取输入数据", e);
		}
		return recvData;
	}

	/**
	 * 连接Socket实现
	 */
	@Override
	public void openConnect() throws ConnectException
	{
		try
		{
			if (type.toLowerCase().equals("client"))
			{
				synchronized (this)
				{
					isServer = false;
				}

				Log.debug("使用IP地址：" + this.localIp + ", 开始连接服务: " + ip + ":" + port);
				if (this.localIp == null || this.localIp.equals(""))
				{
					connector = new Socket(ip, port);
				}
				else
				{
					InetAddress inet = InetAddress.getByName(this.localIp);
					connector = new Socket(ip, port, inet, this.localPort);
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
				server = new ServerSocket(port, 5, inet);
				Log.debug("等待客户端连接...");
				connector = server.accept(); // 不确定当离开这个方法后，server会不会销毁
				Log.debug("连接客户端：" + connector.getRemoteSocketAddress().toString().substring(1));
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
	public void closeConnect() throws ConnectException
	{
		try
		{
			if (connector != null)
				connector.close();
			if (server != null)
				server.close();
			if (keepAlive)
			{
				// 如果是长连接的话，那么输入输出流应该是打开状态的，是否需要关闭一下呢？
			}
			isServer = false;
			connector = null;
			server = null;

		}
		catch (IOException e)
		{
			Log.error("接口关闭失败", e);
			throw new ConnectException();
		}

	}

	@Override
	public boolean isOpen()
	{
		if (connector == null || connector.isClosed())
			return false;
		return true;
	}

	@Override
	public String getRemoteIp()
	{
		return connector.getRemoteSocketAddress().toString().substring(1);
	}

	@Override
	public String getLocalIp()
	{
		return connector.getLocalAddress().toString().substring(1);
	}

	@Override
	public boolean isServer()
	{
		return isServer;
	}

	@Override
	public String getType()
	{
		return "socket";
	}
}
