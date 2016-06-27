package com.boguenon.rpc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;

import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boguenon.service.system.CommonService;
import com.boguenon.service.system.ProcBase;

import com.boguenon.listener.MainAppListenerAssistant;
import com.boguenon.utility.BoguenonUtil;
import com.boguenon.utility.XMLTransform;

import org.w3c.dom.*;

public class bogRpc extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private Map <String, Object> __mechan;
	
	private Context getInitContext(Context initContext, String path)
	{
		Context envContext = null;
		Object obj = null;
		
		try
		{
			obj = initContext.lookup(path);
			
			if (obj != null)
			{
				envContext = (Context) obj;
			}
		}
		catch (javax.naming.NotContextException e)
		{
			System.out.println("Error the lookup for " + path + " is not a context");
		}
		catch (javax.naming.NamingException e2)
		{
			System.out.println("Error naming exception for " + path + " is not a context");
		}
		
		if (envContext != null)
		{
			System.out.println("Success on get system environment " + path);
		}
		return envContext;
	}

	@SuppressWarnings("unchecked")
	public void init() throws ServletException 
	{
		super.init();
		
		try 
		{
			// Get DataSource
			ServletContext context = getServletContext();
			String c_home = context.getInitParameter("BOG_HOME");
			
			Context initContext  = new InitialContext();
			Context envContext = getInitContext(initContext, "java:comp/env");
			
			// System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
			
			NamingEnumeration <NameClassPair> names = null;
			
			if (envContext != null)
			{
				// for some webserver it breaks in here
				try
				{
					names = envContext.list("");
				}
				catch (Exception ex)
				{
					System.err.println(">> webserver errors to get configuration name list");
				}
			}
			
			__mechan = (Map <String, Object>) getServletContext().getAttribute("__mechan_");
			
			if (__mechan == null)
			{
				__mechan = new HashMap <String, Object> ();
				getServletContext().setAttribute("__mechan", __mechan);
			}
			
			bogServer m_base = (bogServer) __mechan.get("_mecserver_");
			
			if (m_base == null)
			{
				c_home = context.getInitParameter("BOG_HOME");
				m_base = new bogServer();
				m_base.BOG_HOME = c_home.endsWith("/") == true ? c_home : c_home + "/";
				
				m_base.InitServletEnvironment();
				
				__mechan.put("_mecserver_", m_base);
			}
			
			CommonService.m_base = m_base;
		} 
		catch (NamingException e) 
		{
			
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		resp.setContentType("text/xml");
		PrintWriter out = resp.getWriter();
		out.println("<smsg errorcode='0x0001' errormsg='Do not support direct access to db' version='" + bogServer.version + "'></smsg>");

		out.flush();
		out.close();
		out = null;
	}
	
	public static String getPayload(HttpServletRequest req)
	{
		String address = req.getParameter("payload");
		
		if (address == null)
		{
			String names = req.getParameter("__i");
			if (names != null)
			{
				String[] namelist = names.split(";");
				
				address = "<smsg><item";
				
				for (int i=0; i < namelist.length; i++)
				{
					if (namelist[i].equals("") == false)
					{
						address += " " + namelist[i] + "='" + req.getParameter(namelist[i]) + "'";
					}
				}
				
				address += "/></smsg>";
			}
		}
		
		return address;
	}
	
	public static String getMessageBody(HttpServletRequest req)
	{
		String address = req.getParameter("mbody");
		
		if (address == null)
		{
			String names = req.getParameter("__g");
			if (names != null)
			{
				String[] namelist = names.split(";");
				
				address = "<smsg><info";
				
				for (int i=0; i < namelist.length; i++)
				{
					if (namelist[i].equals("") == false)
					{
						address += " " + namelist[i] + "='" + req.getParameter(namelist[i]) + "'";
					}
				}
				
				address += "/></smsg>";
			}
		}
		
		return address;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException 
	{
		try 
		{
			bogServer m_base = (bogServer)__mechan.get("_mecserver_");
						
			req.setCharacterEncoding("UTF-8");
						
			String token = null;
			String inp_data = req.getParameter("data");
			String inp_content = req.getParameter("content");
			
			int purpose = MainAppListenerAssistant.A_Unknown; // req.getParameter("Purpose");
			String address = null; // req.getParameter("Address");
			String content = null; // req.getParameter("Content");
			String messageid = null;
			String mts = req.getParameter("mts");
			String _mts_id = req.getParameter("_mts_");
			
			String pstr = null;
			
			if (inp_data != null && inp_data.indexOf("|") > -1)
			{
				pstr = inp_data.substring(0, inp_data.indexOf("|"));
				address = inp_data.substring(inp_data.indexOf("|")+1);
				
				pstr = ProcBase.decodeBase64String(pstr);
				address = ProcBase.decodeBase64String(address);
			}
			else
			{
				pstr = req.getParameter("ack");
				address = bogRpc.getPayload(req);
				messageid = req.getParameter("MessageID");
			}
			
			if (pstr != null && "".equals(pstr) == false)
			{
				purpose = Integer.parseInt(pstr);
			}
			
			if (inp_content != null)
			{
				content = ProcBase.decodeBase64String(inp_content);
			}
			else
			{
				content = bogRpc.getMessageBody(req);
			}
			
			m_base.servlet_context_path = req.getSession().getServletContext().getRealPath("");
						
			String ret = null;
			
			String clientIP = req.getRemoteAddr();
			/* performance issues on reverse hostname lookup*/
			String hostname = clientIP; // req.getRemoteHost();
			
			HttpSession session = req.getSession(true);
			
			System.out.println(">> rpc call : " + clientIP + "/" + hostname);
			
			if (purpose == MainAppListenerAssistant.A_Upload || req.getHeader("Content-Type").startsWith("multipart/form-data") == true)
			{
				if (req.getHeader("Content-Type").startsWith("multipart/form-data") == true)
				{
					clientIP = req.getRemoteAddr();
					/* performance issues on reverse hostname lookup*/
					hostname = clientIP; // req.getRemoteHost();
					
					ret = MainAppListenerAssistant.uploadProcessing(m_base, token, clientIP, hostname, req, resp, purpose, address, content);
				}
				else
				{
					if (session.getAttribute("__token_" + _mts_id) != null)
					{
						token = session.getAttribute("__token_" + _mts_id).toString();
					}
					
					if (token != null && "".equals(token) == false)
					{
						clientIP = req.getRemoteAddr();
						/* performance issues on reverse hostname lookup*/
						hostname = clientIP; // req.getRemoteHost();
						
						ret = MainAppListenerAssistant.uploadProcessing(m_base, token, clientIP, hostname, req, resp, purpose, address, content);
					}
				}
				return;
			}
			else if (purpose != MainAppListenerAssistant.A_Unknown)
			{
				ret = MainAppListenerAssistant.processCommandString(m_base, token, mts, clientIP, hostname, req, resp, purpose, address, content, false, false);
			}
			
			if (ret == null || ret.equals(""))
			{
				ret = BoguenonUtil.printErr(200, m_base, "Result is not build for this request", null, null);
			}
			
			if (messageid != null)
			{
				XMLTransform p_ret = new XMLTransform(ret);
				Node p_retroot = p_ret.rootNode();
				XMLTransform.SetAttribute(p_retroot, "messageid", messageid);
				
				ret = XMLTransform.innerXML(p_retroot);
			}
			
			resp.setContentType("text/plain; charset=UTF-8");

			PrintWriter out = new PrintWriter(new java.io.OutputStreamWriter(resp.getOutputStream(), "UTF8"), true); // .getWriter();
			out.print(ret);
			out.flush();
			out.close();
			out = null;
		} 
		catch (Exception e) 
		{
			
		}
	}
}
