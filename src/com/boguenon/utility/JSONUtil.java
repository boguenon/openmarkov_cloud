package com.boguenon.utility;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class JSONUtil 
{
	public static String Object(String name)
	{
		String r = "\"" + name + "\":{";
		
		return r;
	}
	
	public static Object GetNode(JSONObject json, String nodes)
	{
		Object r = null;
		
		String[] ne = nodes.split("/");
		
		JSONObject pe = json;
		boolean b_pe = true;
		
		for (int i=0; i < ne.length; i++)
		{
			if (pe.containsKey(ne[i]))
			{
				Object n = pe.get(ne[i]);
				
				if (n instanceof JSONObject)
				{
					pe = (JSONObject) n;
				}
				else if (n instanceof JSONArray)
				{
					r = (JSONArray) n;
					b_pe = false;
				}
			}
			else
			{
				pe = null;
			}
		}
		
		if (b_pe == true)
		{
			r = pe;
		}
		
		return r;
	}
	
	public static String GetParam(String name, int value, boolean isend)
	{
		return GetParam(name, Integer.toString(value), isend);
	}
	
	public static String GetParam(String name, String value, boolean isend)
	{
		String r = "\"" + name + "\":";
        r += "\"" + value + "\"";
        
        if (isend == false)
        {
        	r += ",";
        }
        
        return r;
	}
	
	@SuppressWarnings("unchecked")
	public static Properties getParameters(JSONObject tnode)
	{
		Properties pvalues = new Properties();
		
		if (tnode != null && tnode.isNullObject() == false)
		{
			Iterator<Object> keys = tnode.keys(); 
			while (keys.hasNext())
			{
				String kvalue = keys.next().toString();
				pvalues.put(kvalue, tnode.get(kvalue).toString());
			}
		}
		
		return pvalues;
	}
	
	public static String getParameterValue(JSONObject tnode, String key)
	{
		return (tnode.containsKey(key) == true ? tnode.getString(key) : null);
	}
	
	public static void setAttribute(JSONObject node, String key, String value)
	{
		node.element(key, value);
	}
	
	public static JSONObject readFile(File f)
	{
		String jc = OSUtil.readFile(f);		
		JSONObject json = null;
		
		try
		{
			json = (JSONObject) JSONSerializer.toJSON(jc);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
		}
		
		return json;
	}
}
