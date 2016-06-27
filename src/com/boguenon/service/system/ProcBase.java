package com.boguenon.service.system;

import java.io.*;

import java.util.*;
import java.util.zip.*;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Node;

import com.boguenon.rpc.bogServer;
import com.boguenon.service.common.Logger;
import com.boguenon.utility.BoguenonUtil;
import com.boguenon.utility.OSUtil;
import com.boguenon.utility.XMLTransform;
import com.boguenon.utility.ClassUtils;

public class ProcBase
{
	// protected BufferedOutputStream outprn;
	// protected BufferedInputStream instream;

	public bogServer p_daemon;
	
	public int p_purpose;
	public String p_address;
	public String p_content;
	
	protected java.net.InetAddress inet;
	
	public String token;
	
	public String user_locale;
		
	public String _hostAddr = null;
	public String _hostName = null;
	public String _userid = null;
	
	public HttpSession _session = null;
	
	protected boolean is_schedule = false;
	
	public bogServer getDaemon()
	{
		return p_daemon;
	}
	
	public ProcBase(bogServer daemon, String _token, Integer purpose, String address, String content, boolean isRemote, boolean isSecurity, HttpSession _session, boolean is_schedule)
	{
		p_daemon = daemon;
		
		this.token = _token; 
		
		p_purpose = purpose;
		p_address = address;
		p_content = content;
		
		this._session = _session;
		this.is_schedule = is_schedule;
	
		if (daemon != null)
		{
			this.user_locale = (this.user_locale == null) ? daemon.default_locale : this.user_locale;
		}
	}
		
	public void SetContent(Integer purpose, String address, String content)
	{
		p_purpose = purpose;
		p_address = address;
		p_content = content;
	}
	
	public static void writeCompressedString(String value, BufferedOutputStream ostream)
	{
		writeCompressedString(value, ostream, "UTF-8");
	}
	
	public static void writeCompressedString(String value, BufferedOutputStream ostream, String encode)
	{
		try
		{
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			DeflaterOutputStream dout = new DeflaterOutputStream(bo);
			
			if (encode != null)
			{
				dout.write(value.getBytes(encode));
			}
			else
			{
				dout.write(value.getBytes());
			}
			
			dout.flush();
			dout.close();
			
			byte[] tbyte = bo.toByteArray();
			long lbytesize = tbyte.length;
			byte[] lbyte = ClassUtils.convertLongToByte(lbytesize);
			ostream.write(lbyte);
			ostream.write(tbyte);
			ostream.flush();
		}
		catch (Exception e)
		{
			com.boguenon.service.common.Logger.logException(e);
			System.out.println("Error while processing compressed string : " + e.getMessage());
		}
	}
	
	public String sendError(int errorcode, String detail, Exception ex)
    {
    	return sendError(errorcode, detail, ex, 0, 0);
    }
	
	public String sendError(int errorcode, String detail, Exception ex, int m_stat, int p_stat)
    {
    	String err = BoguenonUtil.printErr(errorcode, p_daemon, detail, user_locale, ex, m_stat, p_stat);
    	
    	if (this.p_daemon != null && this.p_daemon.g_config != null && this.p_daemon.g_config.ignoreError == true)
    	{
    		err = "<smsg errorcode='0x9999' errormsg=''></smsg>";
    	}
    	
    	this.printMsg(Logger.SEVERE, err);
    	return err;
    }
	
	protected String decodeString(String value, String dbcharset, String outcharset)
	{
		String utf = (value == null) ? "" : value;
		
		try
		{
			if (value != null && dbcharset != null && dbcharset.length() > 0 && outcharset != null && outcharset.length() > 0)
			{
				// utf = new String(value.getBytes("iso-8859-1"), "euc-kr");
				utf = new String(value.getBytes(dbcharset), outcharset);
			}
		}
		catch (Exception ex)
		{
			System.out.println("DecodeString exception " + ex.getMessage());
		}

		return utf;
	}
	
	public static String encodeCompressedContent(String value)
	{
		String out = "";
		
		try
		{
			String encode = "UTF8";
			
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			DeflaterOutputStream dout = new DeflaterOutputStream(bo);
			
			dout.write(value.getBytes(encode));
			
			dout.flush();
			dout.close();
			
			byte[] tbyte = bo.toByteArray();
			
			// BASE64Encoder dec = new BASE64Encoder();
			// ByteArrayInputStream bin = new ByteArrayInputStream(tbyte);
			// ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			// dec.encodeBuffer(bin, bout);
			
			byte[] bout = Base64.encodeBase64(tbyte);
			
			// out = bout.toString().trim();
			out = new String(bout, "UTF-8").trim();
			out = out.replace("\r", "");
			out = out.replace("\n", "");
		}
		catch (Exception e)
		{
			e.getStackTrace();
		}
		
		
		return out;
	}
	
	public String writeContent(String uid, String cname, String mvalue)
	{
		String result = null;
		
		try 
		{
			String content = "";
			
			String rmeta = mvalue;
			XMLTransform parser = new XMLTransform(rmeta);
			Node nd_item = parser.getNode("/smsg/item");
			
			if (nd_item != null)
				content = XMLTransform.innerXML(nd_item);
			else
				content = mvalue;
			
			String o_content = content;

			content = encodeContent(content);
			
			this.writeContentToFile(uid, cname, o_content);
		} 
		catch(Exception e) 
		{
			com.boguenon.service.common.Logger.logException(e);
			result = BoguenonUtil.printErr(900, this.p_daemon, e.getMessage(), user_locale, e);
		} 
		finally 
		{
		}
		
		return result;
	}
	
	protected String readMetaContentFromFile(String uid)
	{
		String r = null;
		
		String fbase = this.p_daemon.BOG_HOME;
		
		if (fbase.endsWith("/") == false)
		{
			fbase = fbase + "/";
		}
		
		String floc = fbase + "meta_files/" + uid;
		
		File f_p = new File(floc);
		
		if (f_p.exists() == true && f_p.canRead() == true)
		{
			r = OSUtil.readFile(f_p);
		}
		
		return r;
	}
	
	protected void writeContentToFile(String uid, String mname, String content)
	{
		try
		{
			String fbase = this.p_daemon.BOG_HOME;
			
			if (fbase.endsWith("/") == false)
			{
				fbase = fbase + "/";
			}
			
			String floc = fbase + "meta_files/";
			
			if (floc.endsWith("/") == false)
			{
				floc += "/";
			}
			
			File folder = new File(floc);
			
			if (folder.exists() == false)
			{
				folder.mkdirs();
			}
			
			OSUtil.writeToFile(floc, uid, content);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			
		}
	}
	
	
	public void printMsg(int level, String msg)
	{
		p_daemon.printMsg(level, msg, this._hostAddr, this._hostName, this._userid);
	}
	
	public static String decodeBase64String(String v)
	{
		String out = "";
		
		// BASE64Decoder dec = new BASE64Decoder();
		
		try
		{
			// ByteArrayInputStream bin = new ByteArrayInputStream(v.getBytes());
			// ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			byte[] bout = Base64.decodeBase64(v.getBytes("UTF-8"));
			// dec.decodeBuffer(bin, bout);
			// bout.flush();
			// out = bout.toString("UTF-8");
			out = new String(bout, "UTF-8");
			
			// bin.close();
			// bout.close();
			
			// bin = null;
			// bout = null;
		}
		catch (Exception ex)
		{
			out = v;
		}
		
		return out;
	}
	
	public static String encodeBase64String(String v)
	{
		return encodeContent(v);
	}
	
	public static String decodeContent(String v)
	{
		String out = "";
		
		// BASE64Decoder dec = new BASE64Decoder();
		// ByteArrayInputStream bin = new ByteArrayInputStream(v.getBytes());
		// ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		try
		{
			if (v.startsWith("PE") || v.startsWith("PG") || v.startsWith("PHNtc"))
			{
				byte[] bout = Base64.decodeBase64(v.getBytes("UTF-8"));
				// dec.decodeBuffer(bin, bout);
				// bout.flush();
				
				out = new String(bout, "UTF-8");
				
				// bin.close();
				// bout.close();
				
				// bin = null;
				// bout = null;
			}
			else
			{
				// dec.decodeBuffer(bin, bout);
				byte[] bout = Base64.decodeBase64(v.getBytes("UTF-8"));
				
				ByteArrayInputStream in = new ByteArrayInputStream(bout);
				InflaterInputStream din = new InflaterInputStream(in);
				ByteArrayOutputStream bresult = new ByteArrayOutputStream();
				
				int b;
				
				while ((b = din.read()) != -1)
				{
					bresult.write(b);
				}
				
				bresult.flush();
				out = bresult.toString("UTF-8");
				
				// bin.close();
				din.close();
				bresult.close();
				// bin = null;
				din = null;
				bresult = null;
			}
		}
		catch (Exception e)
		{
			// com.boguenon.service.Logger.logException(e);
			System.out.println("Error while decode text content: " + v);
		}
		return out;
	}
	
	public static byte[] decodeByteContent(String value)
	{
		byte[] b = null;
		
		// BASE64Decoder dec = null;
		// ByteArrayInputStream bin = null;
		// ByteArrayOutputStream bout = null;
		
		try
		{
			// dec = new BASE64Decoder();
			// bin = new ByteArrayInputStream(value.getBytes());
			// bout = new ByteArrayOutputStream();
			
			b = Base64.decodeBase64(value.getBytes("UTF-8"));
			// dec.decodeBuffer(bin, bout);
			// b = bout.toByteArray();
		}
		catch (Exception e)
		{
			System.out.println("Error while decode to byte content: " + e.getMessage());
		}
		finally
		{
		}
		
		return b;
	}
	
	public static String encodeByteContent(byte[] value)
	{
		String out = "";
		
		// BASE64Encoder dec = new BASE64Encoder();
		// ByteArrayInputStream bin = new ByteArrayInputStream(value);
		// ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		try
		{
			byte[] bout = Base64.encodeBase64(value);
			// dec.encodeBuffer(bin, bout);
			out = new String(bout, "UTF-8").trim();
		}
		catch (Exception e)
		{
			com.boguenon.service.common.Logger.logException(e);
		}
		
		return out;
	}
	
	
	
	public static String encodeContent(String v)
	{
		String out = "";
		
		// BASE64Encoder dec = new BASE64Encoder();
		// ByteArrayInputStream bin = null;
		// ByteArrayOutputStream bout = null;
		
		try
		{
			byte bin[] = v.getBytes("UTF-8");
			//bout = new ByteArrayOutputStream();
			byte[] bout = Base64.encodeBase64(bin);
			// dec.encodeBuffer(bin, bout);
			out = new String(bout, "UTF-8").trim();
			out = out.replace("\r", "");
			out = out.replace("\n", "");
		}
		catch (Exception e)
		{
			com.boguenon.service.common.Logger.logException(e);
		}
		
		return out;
	}
		
	public static boolean hasError(String content)
	{
		boolean r = false;
		
		if (content != null && content.equals("") == false)
		{
			XMLTransform x_parser = new XMLTransform(content);
			
			if (x_parser.hasError == true)
			{
				r = true;
			}
			else
			{
				Node tnode = x_parser.getNode("/smsg");
				if (tnode != null)
				{
					String errorcode = XMLTransform.GetElementValue(tnode, "errorcode");
					if (errorcode != null && "".equals(errorcode) == false)
					{
						r = true;
					}
				}
			}
		}
		
		return r;
	}
	
	public String readMetaContent(String uid, boolean includeListNode)
	{
		String ret = null;
		
		try
		{
			StringBuilder r = new StringBuilder();
			if (includeListNode == true)
			{
				r.append("<smsg>");
			}
			
			r.append("<item uid='" + uid + "'>");
			
			String temp = "";
			
			String meta_content = null;
				
			// meta filesystem
			meta_content = this.readMetaContentFromFile(uid);

			if (meta_content != null && meta_content.equals("") == false)
			{
				temp = meta_content;
				
				if (temp.startsWith("<?") == true && temp.indexOf("?>") > 1)
				{
					int n = temp.indexOf("?>");
					temp = temp.substring(n + 2);
				}
			}
			
			temp = (temp != null && temp.equals("") == false) ? temp : null;
			
			XMLTransform parser = (temp != null) ? new XMLTransform(temp) : null;
			
			if (parser == null || (parser != null && parser.hasError == true))
			{
				temp = "";
			}
			else
			{
				temp = getInnerXMLText(temp);
			}
			
			r.append(temp);
			
			r.append("</item>");
			
			if (includeListNode == true)
			{
				r.append("</smsg>");
			}
			
			ret = r.toString();
		}
		catch (Exception e)
		{
			com.boguenon.service.common.Logger.logException(e);
			printMsg(Logger.SEVERE, "Error while get content " + e.getMessage());
		}
		finally
		{
		}
		
		// edate = new java.util.Date(); System.out.println(">> final " + (edate.getTime() - cdate.getTime()));
		
		return ret;
	}
	    
	public static String getInnerXMLText(String elementString)
	{
		int start, end;
        start = elementString.indexOf(">") + 1;
        end = elementString.lastIndexOf("</");
        if (end > 0)
            return elementString.substring(start,end);
        
        return "";
	}
	

	protected void setPerformanceLog(java.util.Date sdate, String msg)
	{
		java.util.Date mdate = new java.util.Date();
		long gap = mdate.getTime() - sdate.getTime();
		String logcontent = msg + " : " + Long.toString(gap);
		System.out.println(logcontent);
	}
	
	public static String getNodeText(Properties prop, String attributes, String pre_indent, String post_indent)
	{
		String ret = "";
		
		String[] attr = attributes.split(";");
		pre_indent = (pre_indent == null) ? " " : pre_indent;
		post_indent = (post_indent == null) ? "" : post_indent;
		
		for (int i=0; i < attr.length; i++)
		{
			if (prop.containsKey(attr[i]) == true && prop.getProperty(attr[i]) != null)
			{
				ret += pre_indent + attr[i] + "='" + prop.getProperty(attr[i]) + "'" + ((i == attr.length - 1) ? "" : post_indent);
			}
		}
		
		return ret;
	}
}