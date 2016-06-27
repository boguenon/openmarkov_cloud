package com.boguenon.utility;

import com.boguenon.rpc.bogServer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.DateFormat;
import java.text.ParseException;

public class BoguenonUtil 
{
	/* 
	 * General Utility
	 */
	
	public static String printErr(int code, bogServer pDaemon, String detail, String locale, Exception ex)
	{
		return printErr(code, pDaemon, detail, true, locale, ex, 0, 0);
	}
	
	public static String printErr(int code, bogServer pDaemon, String detail, String locale, Exception ex, int m_stat, int p_stat)
	{
		return printErr(code, pDaemon, detail, true, locale, ex, m_stat, p_stat);
	}
	
	public static String printErr(int code, bogServer pDaemon, String detail, boolean useIgnoreError, String locale, Exception ex)
	{
		return printErr(code, pDaemon, detail, useIgnoreError, locale, ex, 0, 0);
	}
	
    public static String printErr(int code, bogServer pDaemon, String detail, boolean useIgnoreError, String locale, Exception ex, int m_stat, int p_stat)
    {
    	StringBuilder r = new StringBuilder();
    	
    	String estack = (ex != null) ? org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex) : null;
    	
    	String errmsg = getErrorMessage(pDaemon, code, locale);
    	if (errmsg == null)
    	{
    		errmsg = "Failed while process request on server!";
    	}
    	
    	String errorcode = ClassUtils.getHexErrorCode(code);
    	
    	if (ex != null && (detail == null || "".equals(detail) == true))
    	{
    		detail = ex.getMessage();
    	}
    	
    	r.append("<smsg errorcode='" + errorcode + "' errormsg='" + errmsg + "'>");
    	
    	if (detail != null)
    	{
    		if (m_stat > 0 && p_stat > 0)
        	{
        		detail = "REF(" + m_stat + ":" + p_stat + ")\n\n" + detail;
        	}
    		
    		r.append("<detail><![CDATA[" + detail + "]]></detail>");
    	}
    	
    	if (estack != null)
    	{
    		estack = ClassUtils.base64Encode(estack.getBytes());
    		if (m_stat > 0 && p_stat > 0)
        	{
        		estack = "REF(" + m_stat + ":" + p_stat + ")\n\n" + estack;
        	}
    		
    		r.append("<stacktrace><![CDATA[" + estack + "]]></stacktrace>");
    	}
    	
    	r.append("</smsg>");
    	
    	return r.toString();
    }
    
    public static String getErrorMessage(bogServer pDaemon, int code, String locale)
    {
    	String errmsg = null;
    	
    	String default_locale = pDaemon.default_locale;
    	locale = (locale == null) ? default_locale : locale;
    	
    	HashMap <Integer, bogServer.ErrorCodes> mlocale = null;
    	HashMap <Integer, bogServer.ErrorCodes> dlocale = pDaemon.gLocaleData.get("en_US");;
    	
    	if (locale != null && pDaemon.gLocaleData.containsKey(locale) == true)
    	{
    		mlocale = pDaemon.gLocaleData.get(locale);
    	}
    	else
    	{
    		mlocale = dlocale;
    	}
    	
    	if (mlocale != null)
    	{
    		if (mlocale.containsKey(code) == true)
    		{
    			errmsg = mlocale.get(code).errorMsg;
    		}
    		else if (dlocale.containsKey(code) == true)
    		{
    			errmsg = dlocale.get(code).errorMsg;
    		}
    		else
    		{
    			errmsg = "Locale not found!";
    		}
    		/*
	    	for (i=0; i < pDaemon.gLocaleData.size(); i++)
	    	{
	    		if (pDaemon.gLocaleData.get(i).errorCode == code)
	    		{
	    			errmsg = pDaemon.gLocaleData.get(i).errorMsg;
	    			break;
	    		}
	    	}
	    	*/
    	}
    	else
    	{
    		errmsg = "Locale not found!";
    	}
    	return errmsg;
    }
    
    public static String formatErrorMessage(String errormessage, String name, String value)
    {
    	if (errormessage != null)
    	{
    		errormessage = errormessage.replace(name, value);
    	}
    	
    	return errormessage;
    }
    
    /*
     * Date Time Conversion
     */
     
    /*
		Letter	Date or Time Component	Presentation		Examples
		G		Era designator			Text				AD
		y		Year					Year				1996; 96
		M		Month in year			Month				July; Jul; 07
		w		Week in year			Number				27
		W		Week in month			Number				2
		D		Day in year				Number				189
		d		Day in month			Number				10
		F		Day of week in month	Number				2
		E		Day in week				Text				Tuesday; Tue
		a		Am/pm marker			Text				PM
		H		Hour in day (0-23)		Number				0
		k		Hour in day (1-24)		Number				24
		K		Hour in am/pm (0-11)	Number				0
		h		Hour in am/pm (1-12)	Number				12
		m		Minute in hour			Number				30
		s		Second in minute		Number				55
		S		Millisecond				Number				978
		z		Time zone				General time zone	Pacific Standard Time; PST; GMT-08:00
		Z		Time zone				RFC 822 time zone	-0800
		
		
		
		
		Date and Time Pattern						Result
		"yyyy.MM.dd G 'at' HH:mm:ss z"				2001.07.04 AD at 12:08:56 PDT
		"EEE, MMM d, ''yy"							Wed, Jul 4, '01
		"h:mm a"									12:08 PM
		"hh 'o''clock' a, zzzz"						12 o'clock PM, Pacific Daylight Time
		"K:mm a, z"									0:08 PM, PDT
		"yyyyy.MMMMM.dd GGG hh:mm aaa"				02001.July.04 AD 12:08 PM
		"EEE, d MMM yyyy HH:mm:ss Z"				Wed, 4 Jul 2001 12:08:56 -0700
		"yyMMddHHmmssZ"								010704120856-0700
	*/
	
	public static Calendar translateDate(String date, DateFormat df)
	{
		Calendar c = null;
		
		Date dt = BoguenonUtil.StringToDate(date, df);
		
		if (dt != null)
		{
			c = Calendar.getInstance();
			c.setTime(dt);
		}
		/*
			int year = c.get(Calendar.YEAR) + 1900;
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			int weekday = c.get(Calendar.DAY_OF_WEEK);
			int hh = c.get(Calendar.HOUR_OF_DAY);
			int mm = c.get(Calendar.MINUTE);
		*/
		return c;
	}
	
	public static Date StringToDate(String date, DateFormat df)
	{
		Date dt = null;
		
		try 
		{
	      	dt = df.parse(date);
	  	} 
	  	catch (ParseException e) 
	  	{
	    	// com.boguenon.service.Logger.logException(e);
	  		System.out.println(">> error while parse date : " + e.getMessage());
	  	}
	  
	  return dt;
	}
}
