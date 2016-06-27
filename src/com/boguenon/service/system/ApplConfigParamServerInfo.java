package com.boguenon.service.system;

import java.util.Properties;

import com.boguenon.utility.ClassUtils;

public class ApplConfigParamServerInfo 
{

	public int acceptcount;
	public String defaultlocale;
	public String defaultdateformat;
	public int idletimeout;
	public boolean ignore_error;
	public String javahome;
	public int maxthreads;
	public int minthreads;
	
	public String port;
	public int session_expire;
	public String serverhost;
	public String servername;
	public boolean enabled = true;
		
	public ApplConfigParamServerInfo(Properties p)
	{
		if (p != null)
		{
			if (p.containsKey("acceptcount") == true)
			{
				acceptcount = ClassUtils.isInt(p.getProperty("acceptcount"));
			}
			
			if (p.containsKey("defaultlocale") == true)
			{
				defaultlocale = p.getProperty("defaultlocale");
			}
			
			if (p.containsKey("defaultdateformat") == true)
			{
				defaultdateformat = p.getProperty("defaultdateformat");
			}
			
			if (p.containsKey("idletimeout") == true)
			{
				idletimeout = ClassUtils.isInt(p.getProperty("idletimeout"));
			}
			
			if (p.containsKey("ignore_error") == true)
			{
				ignore_error = p.getProperty("ignore_error").equals("true");
			}
			
			if (p.containsKey("javahome") == true)
			{
				javahome = p.getProperty("javahome");
			}
			
			if (p.containsKey("maxthreads") == true)
			{
				maxthreads = ClassUtils.isInt(p.getProperty("maxthreads"));
			}
			
			if (p.containsKey("minthreads") == true)
			{
				minthreads = ClassUtils.isInt(p.getProperty("minthreads"));
			}
			
			if (p.containsKey("port") == true)
			{
				port = p.getProperty("port");
			}
			
			if (p.containsKey("session_expire") == true)
			{
				session_expire = ClassUtils.isInt(p.getProperty("session_expire"));
				session_expire = session_expire < 0 ? 0 : session_expire * 1000;
			}
			
			if (p.containsKey("serverhost") == true)
			{
				serverhost = p.getProperty("serverhost");
			}
			
			if (p.containsKey("servername") == true)
			{
				servername = p.getProperty("servername");
			}
		}
	}

}
