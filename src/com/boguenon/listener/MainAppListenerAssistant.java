package com.boguenon.listener;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Node;

import com.boguenon.service.modules.bayes.BayesFrontEndService;
import com.boguenon.service.modules.dataloader.FileLoaderService;
import com.boguenon.service.system.CommonService;
import com.boguenon.service.system.ProcBase;
import com.boguenon.rpc.bogServer;
import com.boguenon.utility.ClassUtils;
import com.boguenon.utility.BoguenonUtil;
import com.boguenon.utility.XMLTransform;

public class MainAppListenerAssistant 
{
	public static final int A_Unknown = 0;
	public static final int A_Content = 5;
	public static final int A_SystemHelper = 11;
	public static final int A_UploadContent = 27;
	public static final int A_WriteContent = 31;

	public static final int A_Upload = 36;
	
	public static final int A_Bayes = 77;
	
	public static String uploadProcessing(bogServer pDaemon, String _token, String clientIP, String hostname, HttpServletRequest req, HttpServletResponse resp, Integer purpose, String address, String content)
	{
	
		String ret = null;
		HttpSession session = (req != null) ? req.getSession() : null;
		
		FileLoaderService m_file = new FileLoaderService(pDaemon, _token, MainAppListenerAssistant.A_Unknown, null, null, false, session, false);
		
		try
		{
			ret = m_file.uploadFile(CommonService.m_base.BOG_HOME + "/upload", req, resp);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}
		return ret;
	}
	
	public static String processCommandString(bogServer pDaemon, String _token, String _mts, String clientIP, String hostname, HttpServletRequest req, HttpServletResponse resp, Integer purpose, String address, String content, boolean isremote, boolean is_schedule)
	{
		String ret = null;
		
		String user_locale = null;
		
		HttpSession session = req != null ? req.getSession() : null; 
				
		switch (purpose)
		{
		case MainAppListenerAssistant.A_Content:
			ProcBase m_base = new ProcBase(pDaemon, _token, purpose, address, content, isremote, false, session, is_schedule);
			XMLTransform xa = new XMLTransform(address);
			Node tnode = xa.getNode("/smsg/item");
			String uid = XMLTransform.GetElementValue(tnode, "uid");
			ret = m_base.readMetaContent(uid, true);
			break;
		case MainAppListenerAssistant.A_WriteContent:
			ProcBase m_writer = new ProcBase(pDaemon, _token, purpose, address, content, isremote, false, session, is_schedule);
			XMLTransform xw = new XMLTransform(address);
			Node wnode = xw.getNode("/smsg/item");
			String wuid = XMLTransform.GetElementValue(wnode, "uid");
			ret = m_writer.writeContent(wuid, wuid, content);
			break;
		case MainAppListenerAssistant.A_SystemHelper:
			ret = "<smsg><item uid='" + ClassUtils.generateUID() + "'></item></smsg>";
			break;
		case MainAppListenerAssistant.A_UploadContent:
		case MainAppListenerAssistant.A_Upload:
			FileLoaderService m_loader = new FileLoaderService(pDaemon, _token, purpose, address, content, isremote, session, is_schedule);
			ret = m_loader.processRequest();
			break;
		case MainAppListenerAssistant.A_Bayes:
			BayesFrontEndService bayes = new BayesFrontEndService(pDaemon, _token, purpose, address, content, isremote, session, is_schedule);
			ret = bayes.processRequest();
			break;
		default:
			ret = BoguenonUtil.printErr(200, pDaemon, null, user_locale, null);
			break;
		}
		
		if (ret == null || "".equals(ret))
		{
			ret = BoguenonUtil.printErr(200, pDaemon, null, user_locale, null);
		}
		
		return ret;
	}
}
