package com.crscic.interfacetesttool.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.crscic.interfacetesttool.DataFactory;

public class SocketInLr
{
	public static void main(String[] args) throws IOException
	{
		int port = 7676;
		Boolean flag = true;
		ServerSocket server = new ServerSocket(port, 5);
		while(flag)
		{
			Socket s = server.accept();
			DataFactory sdf = new DataFactory(s, "D:\\WorkSpace\\Java\\Eclipse\\SocketSend\\config\\config.xml");
			sdf.run();
		}
	}
}