package com.boguenon.service.common;
import java.io.FileWriter;
import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.boguenon.rpc.bogServer;

public class Logger 
{
	public static final int SEVERE = 0;
	public static final int WARNING = 1;
	public static final int INFO = 2;
	public static final int CONFIG = 3;
	public static final int FINE = 4;
	public static final int FINER = 5;
	public static final int FINEST = 10;
	
	private static String BOG_HOME = null;
	
	public static void log(bogServer server, int level, String msg, Object host)
	{
		if (host != null && host instanceof InetAddress)
		{
			InetAddress inet = (InetAddress) host;
			log(server, level, msg, inet.getHostAddress(), inet.getHostName(), null);
		}
		else if (host != null && host instanceof HttpServletRequest)
		{
			HttpServletRequest req = (HttpServletRequest) host;
			String ipaddr = (req.getRemoteAddr() == null) ? "---.---.---.---" : req.getRemoteAddr();
			String hostname = ipaddr; // (req.getRemoteHost() == null) ? "unknown" : req.getRemoteHost()
			log(server, level, msg, ipaddr, hostname, null);
		}
		else
		{
			log(server, level, msg, null, null, null);
		}
	}
	
	public static int getLogLevel(String loglevel)
	{
		int r = Logger.FINEST;
		
		if (loglevel != null)
		{
			if (loglevel.equals("severe"))
			{
				r = Logger.SEVERE;
			}
			else if (loglevel.equals("warning"))
			{
				r = Logger.WARNING;
			}
			else if (loglevel.equals("info"))
			{
				r = Logger.INFO;
			}
			else if (loglevel.equals("config"))
			{
				r = Logger.CONFIG;
			}
			else if (loglevel.equals("fine"))
			{
				r = Logger.FINE;
			}
			else if (loglevel.equals("finer"))
			{
				r = Logger.FINER;
			}
			else if (loglevel.equals("finest"))
			{
				r = Logger.FINEST;
			}
		}
		
		return r;
	}
	
	public static void log(bogServer server, int level, String msg, String hostaddr, String hostname, String userid)
	{
		FileWriter aWriter = null;
		String wmsg = null;
		
		try
		{
			if (BOG_HOME == null)
				return;
			
			System.err.println(msg);
		}
		catch (Exception e)
		{
			System.out.println(getDateTime(null)+": "+msg);
		}
		finally
		{
			if (wmsg != null)
			{
				System.out.println(wmsg);
			}
			
			try
			{
				if (aWriter != null)
				{
					aWriter.close();
				}
			}
			catch (Exception e)
			{
			}
			
            aWriter = null;
		}
	}
	
	public static String getDateTime(String dateformat)
	{
		dateformat = dateformat == null ? "yyyy-MM-dd HH:mm:ss" : dateformat;
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat (dateformat);
		return formatter.format(new java.util.Date());
	}
	
	public static String getLevelMsg(int level)
	{
		String wmsg = null;
		
		switch (level)
		{
		case Logger.CONFIG:
			wmsg = "CONFIG";
			break;
		case Logger.SEVERE:
			wmsg = "ERROR";
			break;
		case Logger.FINE:
			wmsg = "TRACE";
			break;
		case Logger.FINER:
			wmsg = "TRACE";
			break;
		case Logger.FINEST:
			wmsg = "TRACE";
			break;
		case Logger.INFO:
			wmsg = "INFO";
			break;
		case Logger.WARNING:
			wmsg = "WARNING";
			break;
		default:
			wmsg = "UNKNOWN";
			break;
		}
		
		return wmsg;
	}
	
	public static void logException(Throwable e)
	{
		String ex = ExceptionUtils.getStackTrace(e);
		System.err.println(ex);
	}
}
