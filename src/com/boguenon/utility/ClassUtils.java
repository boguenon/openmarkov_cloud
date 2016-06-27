package com.boguenon.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.time.FastDateFormat;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.apache.commons.codec.binary.Base64;

import com.boguenon.service.system.ProcBase;

public class ClassUtils 
{
	public ClassUtils()
	{
		
	}
	
	public static String getDateTime()
	{
		return getDateTime("yyyy-MM-dd HH:mm:ss");
	}
	
	public static String getDateTime(String format)
	{
		return getDateTime(format, new java.util.Date());
	}
	
	private final static Locale __sys_locale = Locale.getDefault();
	
	public static SimpleDateFormat getSimpleDateFormat(String format, Locale locale)
	{
		Locale lc = locale == null ? __sys_locale : locale;

		SimpleDateFormat formatter = new SimpleDateFormat(format, lc);

		return formatter;
	}
	
	public static String getDateTime(String format, java.util.Date date)
	{
		return ClassUtils.getDateTime(format, date, null);
	}
	
	public static String getDateTime(String format, java.util.Date date, Locale lc)
	{
		SimpleDateFormat formatter = ClassUtils.getSimpleDateFormat(format, lc);
		
		return formatter.format(date);
	}
	
	public static String getDateTime(String format, long dt)
	{
		return ClassUtils.getDateTime(format, dt, null, null);
	}
	
	public static String getDateTime(String format, long dt, TimeZone tz)
	{
		return ClassUtils.getDateTime(format, dt, tz, null);
	}
	
	public static String getDateTime(String format, long dt, TimeZone tz, Locale lc)
	{
		Date date = ClassUtils.getTimeValue(dt, Calendar.getInstance().getTimeZone());
		SimpleDateFormat formatter = ClassUtils.getSimpleDateFormat(format, lc);
		
		return formatter.format(date);
	}
	
	public static String calcExpireDate(int duration)
	{
		String r = null;
		
		Date dt = new java.util.Date(System.currentTimeMillis() + duration * 1000);
		
		r = ClassUtils.getDateTime("yyyyMMddHHmmss", dt);
		
		return r;
	}
	
	public static long getUTC()
	{
		Date c = new Date();
		
		return c.getTime();
	}
	
	public static String getAvailableTimeZone()
	{
		String[] ids = TimeZone.getAvailableIDs();
		StringBuilder sb = new StringBuilder();
		// sb.append("<smsg>");
		
		for (int i=0; i < ids.length; i++)
		{
			TimeZone tz = TimeZone.getTimeZone(ids[i]);
			sb.append("<timezone id='" + ids[i] + "'");
			sb.append(" name='" + ClassUtils.escapeXML(tz.getDisplayName()) + "'");
			sb.append(" offset='" + tz.getRawOffset() + "'");
			sb.append("/>");
		}
		
		// sb.append("</smsg>");
		
		return sb.toString();
	}
	
	public static Date getJavaDate(String format, String value)
	{
		return ClassUtils.getJavaDate(format, value, null);
	}
	
	public static Date getJavaDate(String format, String value, Locale lc)
	{
		SimpleDateFormat formatter = ClassUtils.getSimpleDateFormat(format, lc);
		
		java.util.Date d = null;
		
		try
		{
			d = (Date) formatter.parseObject(value);
		}
		catch (Exception e)
		{
			
		}
		
		return d;
	}
	
	public static Date getTimeValue(long t, String timezone)
	{
		TimeZone tz = TimeZone.getTimeZone(timezone);

		return ClassUtils.getTimeValue(t, tz);
	}
	
	public static Date getTimeValue(long t, TimeZone tz)
	{
		Date dt = null;
		
		Calendar calendar = Calendar.getInstance(tz);
		calendar.setTimeInMillis(t);
		calendar.setTimeZone(tz);
		
		dt = calendar.getTime();
		
		return dt;
	}
	
	public static String printEllapsedTime(long time)
	{
		String ret;
		int dss = (int) Math.floor(time / 1000.0);
		int dSS = (int) (time - dss * 1000);
		int dmm = (int) Math.floor(dss / 60.0);
		dss = (int) (dss - dmm * 60.0);
		int dhh = (int) Math.floor(dmm / 60.0);
		dmm = (int) (dmm - dhh * 60.0);
		
		String SS = trimTimeValue(dSS, 3);
		String ss = trimTimeValue(dss, 2);
		String mm = trimTimeValue(dmm, 2);
		String hh = trimTimeValue(dhh, 2);
		
		ret = hh + ":" + mm + ":" + ss + ":" + SS; 
		return ret;
	}
	
	public static String trimTimeValue(int time, int length)
	{
		String strtime = "" + time;
		String ret = strtime;
		
		for (int i=strtime.length(); i < length; i++)
		{
			ret = "0" + ret;
		}
		
		return ret;
	}
	
	public static void addURL(URL u) throws IOException 
    {
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        
        URL urls[] = sysLoader.getURLs();
        
        for (int i = 0; i < urls.length; i++) 
        {
        	if (urls[i].toString().toLowerCase() == u.toString().toLowerCase()) 
        	{
        		return;
        	}
        }
        
        /*
        Class sysclass = URLClassLoader.class;
        
        try 
        {
        	Method method = sysclass.getDeclaredMethod("addURL", parameters);
        	method.setAccessible(true);
        	method.invoke(sysLoader, new Object[]{u});
        } 
        catch (Throwable t) 
        {
        	com.boguenon.service.Logger.logException(t);;
        	throw new IOException("Error, could not add URL to system classloader");
        }
        */
    }
	
	public static String generateUID()
	{
		long[] uid_a = generateRawUID();
		String s_uid = ClassUtils.GetHexUID(uid_a[0], uid_a[1]);
		
		return s_uid;
	}
	
	public static String GetHexUID(long uid1, long uid2)
	{
		String m1 = Integer.toHexString((int)uid1);
		String m2 = Integer.toHexString((int)uid2);
		
		if (m1.length() < 8)
		{
			int m1length = m1.length();
			for (int i=0; i < 8-m1length; i++)
			{
				m1 = "0" + m1;
			}
		}
		
		if (m2.length() < 8)
		{
			int m2length = m2.length();
			for (int i=0; i < 8-m2length; i++)
			{
				m2 = "0" + m2;
			}
		}
		return m1 + "-" + m2;
	}
	
	public static long[] generateRawUID()
	{
		long uid = makeUID();
		long[] uid_a = divideLongValue(uid);
		
		return uid_a;
	}
	
	public static String generateSecureUID()
	{
		String uid = null;
		
		try
		{
			java.security.SecureRandom prng = java.security.SecureRandom.getInstance("SHA1PRNG");
			String randomNum = new Integer( prng.nextInt() ).toString();
			java.security.MessageDigest sha = java.security.MessageDigest.getInstance("SHA-1");
			byte[] result =  sha.digest( randomNum.getBytes() );
			
			uid = hexEncode(result);
			
			if (uid.length() > 15)
			{
				uid = uid.substring(5, 15);
			}
		}
		catch (Exception e)
		{
			
		}
		
		return uid;
	}
	
	public static long makeUID()
	{
		UUID id = UUID.randomUUID();
		long l2 = id.getMostSignificantBits();
		return l2;
	}
	

	public static String hexEncode(byte[] aInput)
	{
		StringBuilder result = new StringBuilder();
		
		char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
		
		for ( int idx = 0; idx < aInput.length; ++idx) 
		{
			byte b = aInput[idx];
			result.append( digits[ (b&0xf0) >> 4 ] );
			result.append( digits[ b&0x0f] );
		}
		
		return result.toString();
	}
	
	public static byte[] convertLongToByte(long value)
	{
		byte[] data = null;
		
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();  
   			DataOutputStream dos = new DataOutputStream(bos);  
   			dos.writeLong(value);  
			dos.flush();  
   			data = bos.toByteArray();  
   		}
   		catch (Exception e)
   		{
   			// System.out.println("Error while convert Long to ByteArray");
   		}
   		
   		return data;
	}
	
	public static long convertByteToLong(byte[] bt)
	{
		long r = 0;
		
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(bt);
			DataInputStream dis = new DataInputStream(bis);
			r = dis.readLong();
		}
		catch (Exception e)
		{
			// System.out.println("Error");
		}
		return r;
	}
	
	public static String byteArrayToHexString(byte[] b)
	{
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++)
		{
			int v = b[i] & 0xff;
			if (v < 16) 
			{
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}
	
	public static byte[] hexStringToByteArray(String s)
	{
		byte[] b = new byte[s.length() / 2];
		for (int i=0; i < b.length; i++)
		{
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index+2), 16);
			b[i] = (byte) v;
		}
		
		return b;
	}
	
	public static long[] divideLongValue(long value)
	{
		byte [] b1 = new byte[4];
		byte [] b2 = new byte[4];
		
		int i;
		
		for(i= 0; i < 8; i++)
		{
			if (i < 4)
			{
				b1[3 - i] = (byte)( (value >> (i * 8)) & 0xff);
			}
			else
			{
				b2[7 - i] = (byte)( (value >> (i * 8)) & 0xff);
			}
		}
		
		long[] r = new long[2];
		
		r[0] = 0;
		r[1] = 0;
		
		for (i=0; i < 4; i++)
		{
			r[1] |= (int) (0xff & b1[3-i]) << (i * 8);
		}
		
		for (i=0; i < 4; i++)
		{
			r[0] |= (int) (0xff & b2[3-i]) << (i * 8);
		}
		
		return r;
	}
	
	public static String getServerDate()
	{
		SimpleDateFormat formatter = ClassUtils.getSimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		return formatter.format(new java.util.Date());
	}
	
	// joda time functions
	public static DateTime jodaGetDate(String format, String value, Locale lc)
	{
		DateTimeFormatter sa = DateTimeFormat.forPattern(format).withLocale(lc);
		
		DateTime dt = null;
		
		try
		{
			dt = sa.parseDateTime(value);
		}
		catch (Exception e)
		{
			
		}
		
		return dt;
	}
	
	public static String jodaGetDateTime(String format, DateTime date, Locale lc)
	{
		DateTimeFormatter sa = DateTimeFormat.forPattern(format).withLocale(lc);
		String r = sa.print(date);
		
		return r;
	}
	// end of joda time functions 
	
	public static String prepDoubleCheck(String value)
	{
		String mval = value;
		if (mval.startsWith("$") == true)
		{
			mval = mval.substring(1);
		}
		else if (mval.endsWith("%") == true)
		{
			mval = mval.substring(0, mval.length()-1);
		}
		
		mval = mval.replaceAll(",", "");
		
		return mval;
	}
	
	public static Double isDouble(String value)
	{
		double r = Double.NaN;
		
		if (value != null && value.equals("") == false)
		{
			int clen = value.length();
			clen = Math.min(5,  clen);
			boolean bf = false;
	
			for (int i=0; i < clen; i++)
			{
				char digit = value.charAt(i);
				
				if ((digit < '0' || digit > '9') && digit != '.' && digit != '-')
				{
					bf = true;
					break;
				}
			}
			
			if (bf == false)
			{
				try
				{
					r = (value != null && value.length() > 0 ? Double.parseDouble(value.trim()) : r);
				}
				catch (Exception e)
				{
					r = Double.NaN;
				}
			}
		}
		
		return r;
	}
	
	public static String formatDouble(double value)
	{
		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(340);
		
		return df.format(value);
	}
	
	public static int isInt(String value)
	{
		int r = -1;
		
		if (value != null && value.length() > 0)
		{
			int clen = value.length();
			clen = Math.min(5,  clen);
			boolean bf = false;

			for (int i=0; i < clen; i++)
			{
				char digit = value.charAt(i);
				
				if ((digit < '0' || digit > '9') && digit != '-' && digit != '.')
				{
					bf = true;
					break;
				}
			}
			
			if (bf == false)
			{
				try
				{
					r = Integer.parseInt(value.trim());
				}
				catch (Exception e)
				{
					r = -1;
				}
			}
		}
		
		return r;
	}
	
	public static long isLong(String value)
	{
		long r = -1L;
		
		if (value != null && value.length() > 0)
		{
			int clen = value.length();
			clen = Math.min(5,  clen);
			boolean bf = false;
	
			for (int i=0; i < clen; i++)
			{
				char digit = value.charAt(i);
				
				if ((digit < '0' || digit > '9') && digit != '.' && digit != '-')
				{
					bf = true;
					break;
				}
			}
			
			if (bf == false)
			{
				try
				{
					r = (value != null && value.length() > 0 ? Long.parseLong(value.trim()): r);
				}
				catch (Exception e)
				{
					r = -1;
				}
			}
		}
			
		return r;
	}
	
	public static float isFloat(String value)
	{
		float r = Float.NaN;
		
		if (value != null && value.length() > 0)
		{
			int clen = value.length();
			clen = Math.min(5,  clen);
			boolean bf = false;
	
			for (int i=0; i < clen; i++)
			{
				char digit = value.charAt(i);
				
				if ((digit < '0' || digit > '9') && digit != '.' && digit != '-')
				{
					bf = true;
					break;
				}
			}
			
			if (bf == false)
			{
			
				try
				{
					r = (value != null && value.length() > 0 ? Float.parseFloat(value.trim()) : r);
				}
				catch (Exception e)
				{
					r = Float.NaN;
				}
			}
		}
		
		return r;
	}
	
	public static boolean isUID(String value)
	{
		boolean r = false;
		if (value != null && value.length() == 17 && value.charAt(8) == '-')
		{
			r = true;
		}
		
		return r;
	}
	
	public static String stripAlphaNumeric(String value)
	{
		String ret = "";
		
		for (int i=0; i < value.length(); i++)
		{
			if ((value.charAt(i) >= '0' && value.charAt(i) <= '9') || value.charAt(i) == '.') // || value.charAt(i) == '-')
			{
				ret += value.charAt(i);
			}
			else
			{
				break;
			}
		}
		
		return ret;
	}
	
	public static String getNullString(String value)
	{
		return value == null ? "" : value;
	}
	
	public static void saveContentFile(String fname, String content)
	{
		File f = null;

		FileOutputStream fo = null;
		OutputStreamWriter ow = null;
		BufferedWriter writer = null;
		
		try
		{
			f = new File(fname);
			
			int ix = 0;
			// int seq = 0;
			
			// fw = new FileWriter(f, false);
			fo = new FileOutputStream(f);
			ow = new OutputStreamWriter(fo, "UTF-8");
			writer = new BufferedWriter(ow);
			
			content = ProcBase.encodeCompressedContent(content);
			
			String p_content;
			int size_content = content.length();
			int m_size = 1000;
			
			while( size_content > 0 ) 
			{
				if(size_content > m_size)
					p_content = content.substring(0+(ix*m_size), m_size+(ix*m_size));     
				else 
					p_content = content.substring(0+(ix*m_size), content.length());     
				
				// seq = ix;
				
				writer.write(p_content);
	          
				ix = ix + 1;
				size_content -= m_size; 
			}
			
			writer.flush();
		}
		catch (Exception e)
		{
			com.boguenon.service.common.Logger.logException(e);;
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
				writer = null;
				
				if (ow != null)
				{
					ow.close();
				}
				ow = null;
				
				if (fo != null)
				{
					fo.close();
				}
				fo = null;
				
				f = null;
			}
			catch (Exception ex)
			{
				
			}
		}
	}
	
	/**
	 * Load file content saved as session dataset
	 */
	public static String loadContentFile(String filename)
	{
		String ret = null;
		
		File f = null;
		FileInputStream fi = null;
		InputStreamReader is = null;
		BufferedReader dis = null;
		
		try
		{
			StringBuilder sb = new StringBuilder();
			
			f = new File(filename);
			
			if (f.exists() == true && f.canRead() == true)
			{
				fi = new FileInputStream(f);
				is = new InputStreamReader(fi, "UTF-8");
				dis = new BufferedReader(is);
				
				String line = null;
				while ((line = dis.readLine()) != null)
				{
					sb.append(line);
				}
			}
			
			if (sb.length() == 0)
			{
				System.err.println("-- no content info on " + filename);
			}
			
			ret = ProcBase.decodeContent(sb.toString());
		}
		catch (Exception e)
		{
			System.err.println("-- Error while loading content " + filename);
		}
		finally
		{
			try
			{
				if (dis != null)
				{
					dis.close();
				}
				dis = null;
				
				if (is != null)
				{
					is.close();
				}
				is = null;
				
				if (fi != null)
				{
					fi.close();
				}
				fi = null;
				
				f = null;
			}
			catch (Exception ex)
			{
			}
		}
		
		return ret;
	}
	
	public static String[] getDateFormat(String _locale, String type)
	{
		Locale locale = Locale.getDefault();
		
		if (_locale != null)
		{
			try
			{
				String lang = _locale;
				
				if (_locale.indexOf("_") > -1)
				{
					lang = _locale.substring(0, 2);
				}
				
				if (lang.length() == 2)
				{
					lang = lang.toLowerCase();
					locale = new Locale(lang);
				}
			}
			catch (Exception ex)
			{
				System.err.println("Locale for session user is incorrect " + _locale);
			}
		}
		
		DateFormatSymbols dfs = new DateFormatSymbols(locale);
		
		String dfr[] = null;
		
		if (type.equals("week"))
		{
			dfr = dfs.getWeekdays();
		}
		else if (type.equals("month"))
		{
			dfr = dfs.getMonths();
		}
		else if (type.equals("ampm"))
		{
			dfr = dfs.getAmPmStrings();
		}

		return dfr;
	}
	
	public static String DoubleToString(double value)
	{
		return BigDecimal.valueOf(value).toPlainString();
	}
	
	public static String escapeXML(String value)
	{
		String r = value;
		
		if (r != null && r.equals("") == false)
		{
			r = ClassUtils.unEscapeXML(r);
			
		    r = r.replaceAll("&", "&amp;");
		    r = r.replaceAll("<", "&lt;");
		    r = r.replaceAll(">", "&gt;");
		    r = r.replaceAll("\"", "&quot;");
		    r = r.replaceAll("'", "&#39;");
		}
		return r;
	}
	
	public static String unEscapeXML(String value)
	{
		String r = value;
		
		if (r != null && r.equals("") == false)
		{
		    r = r.replaceAll("&amp;", "&");
		    r = r.replaceAll("&lt;", "<");
		    r = r.replaceAll("&gt;", ">");
		    r = r.replaceAll("&quot;", "\"");
		    r = r.replaceAll("&#39;", "'");
		}
		return r;
	}
	
	
	// for escape_sql_string
	private static final String[] hex = new String[256];
	
	static {
		for ( char c = 0; c < 0xFF; c++ ) {
			if ( c >= 0x30 && c <= 0x39 || c >= 0x41 && c <= 0x5A || c >= 0x61 && c <= 0x7A ) {
				hex[c] = null;
			} else {
				hex[c] = toHex(c).intern();
			}
		}
	}
	
	private static String getHexForNonAlphanumeric(char c)
	{
		if(c < 0xFF)
			return hex[c];
		return toHex(c);
	}
	
	private static String toHex(char c)
	{
		return Integer.toHexString(c);
	}
	
	
	
	public static String toHex(byte[] bytes) 
	{
		char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j*2] = hexArray[v/16];
	        hexChars[j*2 + 1] = hexArray[v%16];
	    }
	    return new String(hexChars);
	}
	
	public static byte[] hexToByteArray(String hex) 
	{
        if (hex == null || hex.length() % 2 != 0) 
        {
            return new byte[]{};
        }

        byte[] bytes = new byte[hex.length() / 2];
        
        for (int i = 0; i < hex.length(); i += 2) 
        {
            byte value = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        }
        
        return bytes;
    }
	

	public static String getHexErrorCode(int code)
	{
		String errorcode = Integer.toHexString(code);
    	int i;
    	int n = 4 - errorcode.length();
    	
    	for (i=0; i < n; i++)
    	{
    		errorcode += "0";
    	}
    	
    	errorcode = "0x" + errorcode;
    	return errorcode;
	}
	
	public static String stripNonValidXMLCharacters(String in) 
	{
		StringBuffer out = new StringBuffer(); // Used to hold the output.
		char current; // Used to reference the current character.

		if (in == null || ("".equals(in))) 
			return ""; // vacancy test.
		for (int i = 0; i < in.length(); i++) 
		{
			current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
			if ((current == 0x9) ||
					(current == 0xA) ||
					(current == 0xD) ||
					((current >= 0x20) && (current <= 0xD7FF)) ||
					((current >= 0xE000) && (current <= 0xFFFD)) ||
					((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		
		return out.toString();
	}
	
	public static String base64Encode(byte[] bytes) 
	{
        // NB: This class is internal, and you probably should use another impl
        // String nb = new BASE64Encoder().encode(bytes);
		String r = null;
		
		try
		{
			byte[] nb = Base64.encodeBase64(bytes);
			
			r = new String(nb, "UTF-8");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
        
        return r;
    }
	
	public static String URLEncode(String value)
	{
		String r = value;
		
		r = r.replaceAll("\\/", "%2F");
    	r = r.replaceAll("+", "%2B");
    	r = r.replaceAll("=", "%3D");
		
		return r;
	}
	
	public static String URLDecode(String value)
	{
		String r = value;
		
		r = r.replaceAll("%2F", "/");
    	r = r.replaceAll("%2B", "+");
    	r = r.replaceAll("%3D", "=");
		
		return r;
	}
	
    public static byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal, and you probably should use another impl
        // return new BASE64Decoder().decodeBuffer(property);
    	return Base64.decodeBase64(property.getBytes("UTF-8"));
    }
    
    public static String base64StringDecode(String value)
    {
    	String r = null;
    	try
    	{
    		byte[] bt = base64Decode(value);
    		r = new String(bt, "UTF-8");
    	}
    	catch (Exception ex)
    	{
    		com.boguenon.service.common.Logger.logException(ex);;
    	}
    	return r;
    }

    public static String checkNullValue(String mvalue) {
    	String r = (mvalue == null) ? "" : mvalue;
    	
    	return r;
    }
    
    public static String getUTF8(String mtext) {
    	String r = mtext;
    	try
    	{
    		Charset defaultCharset = Charset.defaultCharset();
    		// String cname = defaultCharset.name();
    		byte[] bt = mtext.getBytes("UTF-8");
    		ByteBuffer bf = ByteBuffer.wrap(bt);
    		CharBuffer dt = defaultCharset.decode(bf);
    		
    		Charset utf8charset = Charset.forName("UTF-8");
    		ByteBuffer bo = utf8charset.encode(dt);
    		// r = new String(bt, "UTF-8");
    		r = new String(bo.array(), "UTF-8");
    	}
    	catch (Exception ex)
    	{
    		com.boguenon.service.common.Logger.logException(ex);;
    		r = mtext;
    	}
    	return r;
    }
}
