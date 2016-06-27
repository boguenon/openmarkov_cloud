package com.boguenon.service.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

import net.sf.json.JSONObject;

import com.boguenon.rpc.bogServer;
import com.boguenon.utility.JSONUtil;

public class ApplConfigParam
{
	private bogServer svr;
	
	private JSONObject doc;
	public ApplConfigParamServerInfo confConnection;
		
	public ApplConfigParam(bogServer m_svr, JSONObject m_doc, boolean do_save)
		throws Exception
	{
		doc = m_doc;
		svr = m_svr;
		
		if (m_svr != null)
		{
			m_svr.default_locale = "en_US";
		}
		
		parseConfig(do_save);
	}
	
	public JSONObject getDocument()
	{
		return this.doc;
	}
	
	private void parseConfig(boolean do_save)
		throws Exception
	{
		String p_stat = "Start Parse config file";
		
		try
		{
		
			boolean bNeedSave = false;
			
			JSONObject tnode = null;
			String temp = null;
			
			p_stat = "... server node processing: ";
			
			tnode = this.doc.getJSONObject("server");
			
			if (tnode != null)
			{
				Properties p = JSONUtil.getParameters(tnode);
				
				p_stat = "... connection node processing: ";
				this.confConnection = new ApplConfigParamServerInfo(p);
				
				temp = confConnection.defaultlocale;
				temp = (temp == null) ? "en_US" : temp;
				confConnection.defaultlocale = temp;
				if (svr != null)
				{
					svr.default_locale = temp;
				}
			}
			
			if (bNeedSave == true && do_save == true)
			{
				p_stat = "update configurations to file : ";
				this.updateConfig();
			}
		}
		catch (Exception ex)
		{
			com.boguenon.service.common.Logger.logException(ex);
			System.err.println("Parsing Configuration Status : " + p_stat);
			System.err.println("Error while reading config file" + ex.getMessage());
			
			throw ex; // new Exception(ex);
		}
	}
	
	public void updateConfig()
	{
		String n_config = this.doc.toString(4);
		System.out.println(">> System modified config.json and overwrite existing");
		ApplConfigParam.saveFileContent(this.svr.BOG_HOME + "/config/config.json", n_config);
	}
	
	public static boolean saveFileContent(String filename, String content) 
	{
		boolean res = false;
		
		File f = new File(filename);
		
		FileOutputStream  fos = null;
		OutputStreamWriter dos = null;
	    
		try
		{
			if (!f.isHidden() && f.exists() && f.canRead() && f.canWrite())
			{
				fos = new FileOutputStream(f);
				dos = new OutputStreamWriter(fos, "UTF-8");

				dos.write(content);
				res = true;
			}
		}
		catch (Exception e)
		{
			res = false;
		}
		finally
		{
			try
			{
				if (dos != null)
				{
					dos.close();	
				}
				dos = null;
				
				if (fos != null)
				{
					fos.close();
				}
				fos = null;
			}
			catch (Exception e)
			{
				System.out.println("Error while release file content");
			}
			
			f = null;
		}
	    
	    return res;
	}
}