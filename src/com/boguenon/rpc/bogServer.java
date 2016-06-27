package com.boguenon.rpc;

import java.net.*;
import java.io.*;
import java.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.boguenon.utility.JSONUtil;
import com.boguenon.utility.ClassUtils;
import com.boguenon.service.common.Logger;
import com.boguenon.service.system.ApplConfigParam;

public class bogServer 
{
	public static String version = "";
	
	public float DBVersion;
	
	public HashMap <String, HashMap <Integer, ErrorCodes>> gLocaleData;
	
	public String default_locale = null;
	
	public String servlet_context_path;
		
	public bogServer()
	{
		gLocaleData = new HashMap <String, HashMap<Integer, ErrorCodes>> ();
	}
	
	public String BOG_HOME = "";
	
	public ApplConfigParam g_config;
		
	public boolean GetEnvValueFromFile(boolean is_servlet)
	{
		String p_stat = "Start get environment file " + (is_servlet ? "(servlet)" : "(app)");
		
		try
		{
			String mpath = BOG_HOME;
			mpath = (mpath.endsWith("/") == true) ? mpath : mpath + "/";
			
			String m_config_file = mpath + "config/config.json";
			
			p_stat = "Getting License config file";
		  	
			File m_file_config = new File(m_config_file);
			
			if(m_file_config.exists() != true)
			{
				printMsg(null, 0, m_config_file + " not found !");
				return false;
			}
			
			p_stat = "Getting Locale JSON file";
			
			String m_msg_filename = mpath + "config/locale.json";
			this.gLocaleData.clear();
			
			File f = new File(m_msg_filename);
			JSONObject jlocale = JSONUtil.readFile(f);
			
			p_stat = "Process Locale JSON file";
			
			if (jlocale != null && jlocale.containsKey("data") == true)
			{
				JSONArray locales = jlocale.getJSONArray("data");
				
				for (int i=0; i < locales.size(); i++)
				{
					JSONObject loc = locales.getJSONObject(i);
					String locf = mpath + "config/" + loc.getString("file");
					String locn = loc.getString("lang");
					
					if (locn != null && locf != null)
					{
						loadLocaleFile(locf, locn);
					}
				}
			}
			
			p_stat = "Load JSON file into JSON Object";
			
			JSONObject rootnode = JSONUtil.readFile(m_file_config);
			g_config = new ApplConfigParam(this, rootnode, true);
		}
		catch (Exception ex)
		{
			com.boguenon.service.common.Logger.logException(ex);
			printMsg(null, Logger.SEVERE, "Processing Status : " + p_stat);
			printMsg(null, Logger.SEVERE, "Error while reading locale file" + ex.getMessage());
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void loadLocaleFile(String m_msg_filename, String locale) 
	{
		try
		{
			File m_msg_file = new File(m_msg_filename);
			
			if (m_msg_file.exists() == false)
			{
				printMsg(null, Logger.SEVERE, m_msg_filename + " not found !");
			}
			else
			{
				HashMap <Integer, ErrorCodes> maplocale = null;
				
				if (gLocaleData.containsKey(locale) == true)
				{
					maplocale = gLocaleData.get(locale);
				}
				else
				{
					maplocale = new HashMap<Integer, ErrorCodes>();
					gLocaleData.put(locale, maplocale);
				}
				
				JSONObject m_data = JSONUtil.readFile(m_msg_file);
				
				Iterator<Object> mkeys = m_data.keys();
				
				while (mkeys.hasNext())
				{
					String errorcode = mkeys.next().toString();
					String errormsg = m_data.getString(errorcode);
					ErrorCodes err = new ErrorCodes(errorcode, errormsg);
					maplocale.put(err.errorCode, err);
				}
			}
		}
		catch (Exception e)
		{
			com.boguenon.service.common.Logger.logException(e);
			printMsg(null, Logger.SEVERE, "Error while reading locale file" + e.getMessage());
		}
	}
	
	public boolean GetEnvValue(boolean is_servlet)
	{
	  try
	  {
		  if (BOG_HOME == null || BOG_HOME.equals(""))
		  	 {
		  		 String m_path = new java.io.File(".").getCanonicalPath();
		  		 m_path = m_path.replace('\\', '/');
		  		 
		  		 if (m_path.substring(m_path.lastIndexOf('/')).equals("/bin"))
		  		 {
		  			 BOG_HOME = m_path.substring(0, m_path.lastIndexOf('/')) + "/"; 
		  		 }
		  		 else
		  		 {
		  			 BOG_HOME = m_path + "/";
		  		 }
		  	 }
		  	 
		  	 if (BOG_HOME == null || BOG_HOME.equals("") == true)
		  	 {
		  		 System.out.println("Required parameter is missing. Need to specifiy java option -Dapproot={your app path}");
		  		 return false;
		  	 }
	  	
	  	if (GetEnvValueFromFile(is_servlet) == false)
	  		return false;
	  }
	  catch(Exception ex)
	  {
	     printMsg(null, Logger.SEVERE, "GetEnvValue: "+ex.toString());
	     return false;
	  }
	  return true;
	}
	
	public void InitServletEnvironment()
	{
		GetEnvValueFromFile(true);
	}
	
	public boolean LoadInit()
		throws Exception
	{
		boolean ret = false;

		BOG_HOME = (BOG_HOME == null || (BOG_HOME != null && BOG_HOME.equals("") == true)) ? System.getProperty("approot") : BOG_HOME;

		if (BOG_HOME == null || BOG_HOME.equals(""))
		{
			String m_path = new java.io.File(".").getCanonicalPath();
			m_path = m_path.replace('\\', '/');
	
			if (m_path.substring(m_path.lastIndexOf('/')).equals("/bin"))
			{
				BOG_HOME = m_path.substring(0, m_path.lastIndexOf('/')) + "/"; 
			}
			else
			{
				BOG_HOME = m_path + "/";
			}
		}

		if (BOG_HOME == null || BOG_HOME.equals("") == true)
		{
			System.out.println("Required parameter is missing. Need to specifiy java option -Dapproot={your app path}");
			return false;
		}
	
		ret = GetEnvValue(false);
		
		return ret;
	}
	
	public void printMsg(InetAddress inet, int level, String msg, boolean disptime)
	{
		Logger.log(this, level, msg, inet);
	}
	
	public void printMsg(int level, String msg, String hostaddr, String hostname, String userid)
	{
		Logger.log(this, level, msg, hostaddr, hostname, userid);
	}
	
	public void printMsg(InetAddress inet, int level, String msg)
	{
		printMsg(inet, level, msg, true);
	}
	
	public class ErrorCodes
	{
		public int errorCode;
		public String errorMsg;
		
		public ErrorCodes(String _errorcode, String _errormsg)
		{
			errorCode = ClassUtils.isInt(_errorcode);
			errorMsg = _errormsg;
		}
	}
}
