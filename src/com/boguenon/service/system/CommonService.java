package com.boguenon.service.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.boguenon.rpc.bogServer;

public class CommonService 
{
	public static bogServer m_base;
	
	public static HttpServletRequest req;
	public static HttpServletResponse resp;
	
	public static String getHostName()
	{
		String hostname = "";
		
		if (req != null)
		{
			hostname = req.getRemoteHost();
		}
		
		return hostname;
	}
	
	public static String getHostAddr()
	{
		String addr = "";
		
		if (req != null)
		{
			addr = req.getRemoteAddr();
		}
		
		return addr;
	}
}
