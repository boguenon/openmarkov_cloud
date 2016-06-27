package com.boguenon.service.modules.bayes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.w3c.dom.Node;

import com.boguenon.utility.ClassUtils;
import com.boguenon.utility.OSUtil;
import com.boguenon.utility.XMLTransform;
import com.boguenon.rpc.bogServer;
import com.boguenon.service.system.ProcBase;

public class BayesFrontEndService extends ProcBase
{

	public BayesFrontEndService(bogServer daemon, String _token, Integer purpose, String address, String content, boolean isremote, HttpSession _session, boolean is_schedule)
	{
		super(daemon, _token, purpose, address, content, isremote, false, _session, is_schedule);
	}

	public String processRequest()
	{
		String ret = null;
		
		try
		{
			XMLTransform x_addr = new XMLTransform(this.p_address);
			
			Node tnode = null;
			
			if (x_addr != null)
			{
				tnode = x_addr.getNode("/smsg/item");
				
				if (tnode != null)
				{
					String action = XMLTransform.GetElementValue(tnode, "action");
					action = action != null ? action.toLowerCase() : "";
					
					if (action.equals("load_pgmx"))
					{
						String instanceid = ClassUtils.generateUID();
						ret = this.loadPGMX(this.p_content, instanceid);
					}
					else if (action.equals("get_var"))
					{
						ret = this.getSystemVariables();
					}
					else if (action.equals("ref_down") == true)
					{
						Properties p = XMLTransform.GetElements(tnode);
						String instanceid = p.getProperty("instanceid");
						String uid = p.getProperty("uid");
						
						ret = this.referenceDown(uid);
					}
					else if (action.equals("load_url") == true)
					{
						ret = this.loadFile();
					}
					else if (action.equals("get_property") == true)
					{
						Properties p = XMLTransform.GetElements(tnode);
						String instanceid = p.getProperty("instanceid");
						
						ret = this.getObjectProperty(instanceid);
					}
					else if (action.equals("set_property") == true)
					{
						Properties p = XMLTransform.GetElements(tnode);
						String instanceid = p.getProperty("instanceid");
						String target = p.getProperty("target");
						
						ret = this.setObjectProperty(instanceid, target);
					}
					else if (action.equals("set_working_mode") == true)
					{
						ret = this.setWorkingMode(XMLTransform.GetElementValue(tnode, "instanceid"));
					}
					else if (action.equals("set_new_finding") == true)
					{
						Properties p = XMLTransform.GetElements(tnode);
						String instanceid = p.getProperty("instanceid");
						
						XMLTransform xc = new XMLTransform(p_content);
						
						Node xnode = xc.getNode("/smsg/info");
						
						String target = XMLTransform.GetElementValue(xnode, "target");
						String state = XMLTransform.GetElementValue(xnode, "state");
						
						ret = this.setNewFinding(instanceid, target, state);
					}
					else if (action.equals("set_decision_tree") == true)
					{
						ret = this.setDecisionTree(XMLTransform.GetElementValue(tnode, "instanceid"));
					}
					else if (action.equals("update_network") == true)
					{
						Properties p = XMLTransform.GetElements(tnode);
						String instanceid = p.getProperty("instanceid");
						String actiontype = p.getProperty("actiontype");
						ret = this.updateNetwork(instanceid, actiontype, p);
					}
					else if (action.equals("restore_pgmx") == true)
					{
						String instanceid = XMLTransform.GetElementValue(tnode, "instanceid");
						
						CPGXML pgxml = null;
						
						if (instanceid != null && instanceid.equals("") == false)
						{
							pgxml = getInstanceObj(instanceid);
						}
						
						if (pgxml == null)
						{
							XMLTransform xcontent = new XMLTransform(this.p_content);
							instanceid = ClassUtils.generateUID();
							pgxml = new CPGXML(this.p_daemon, null, instanceid);
							pgxml.loadBayesModel(xcontent);
							setPGMXInstance(instanceid, pgxml);
						}
						
						if (pgxml != null)
						{
							String pgval = pgxml.toPGXMFile();
							ret = "<smsg><data instanceid='" + instanceid + "'><![CDATA[" + ClassUtils.base64Encode(pgval.getBytes("UTF-8")) + "]]></data></smsg>";
						}
					}
					else if (action.equals("load_evidence") == true)
					{
						String instanceid = XMLTransform.GetElementValue(tnode, "instanceid");
						
						ret = this.loadEvidence(instanceid);
					}
					else if (action.equals("learn_network") == true)
					{
						String instanceid = XMLTransform.GetElementValue(tnode, "instanceid");
						ret = this.learnNetwork(instanceid);
					}
					else if (action.equals("cost_effectiveness") == true)
					{
						String instanceid = XMLTransform.GetElementValue(tnode, "instanceid");
						String method = XMLTransform.GetElementValue(tnode, "method");
						
						ret = this.costEffectivenessAnalysis(instanceid, method);
					}
					else if (action.equals("close_network") == true)
					{
						String instanceid = XMLTransform.GetElementValue(tnode, "instanceid");
						closeInstanceObject(instanceid);
					}
				}
			}
		}
		catch (Exception ei)
		{
			com.boguenon.service.common.Logger.logException(ei);
		}
		finally
		{
			
		}
		
		return ret;
	}
	
	private String loadFile()
	{
		XMLTransform x_content = new XMLTransform(this.p_content);
		Node tnode = x_content.getNode("/smsg/info");
		
		String r = "<smsg><data><![CDATA[";
		
		try
		{
			if (tnode != null)
			{
				String fuid = XMLTransform.GetElementValue(tnode, "fuid");
				
				if (fuid != null && fuid.length() > 0)
				{
					File file = new File(p_daemon.BOG_HOME + "/upload/" + fuid);
					
					if (file.exists() == true && file.canRead() == true)
					{
						String mdata = OSUtil.readFile(file, "UTF-8");
						
						if (mdata != null && mdata.length() > 0)
						{
							r += ClassUtils.base64Encode(mdata.getBytes("UTF-8"));
						}
					}
				}
				else
				{
					String murl = XMLTransform.GetSubNodeText(tnode, "url");
					
					if (murl != null && murl.equals("") == false)
					{
						String mdata = null; // GeoDataBase.requestWebData(murl);
						
						if (mdata != null && mdata.length() > 0)
						{
							r += ClassUtils.base64Encode(mdata.getBytes("UTF-8"));
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		r += "]]></data></smsg>";
		
		return r;
	}
	
	private String learnNetwork(String instanceid)
	{
		String r = null;
		
		CPGXML pgmxl = this.getInstanceObj(instanceid);
		
		if (pgmxl == null)
		{
			instanceid = ClassUtils.generateUID();
			pgmxl = new CPGXML(this.p_daemon, CPGXML.get_default_pgmx(), instanceid);
			setPGMXInstance(instanceid, pgmxl);
		}
		
		String datafile = "c:/temp/test_sample.csv";
		
		r = pgmxl.learnNetwork(datafile);
		
		return r;
	}
	
	private String costEffectivenessAnalysis(String instanceid, String method)
	{
		String r = null;
		
		CPGXML pgmxl = this.getInstanceObj(instanceid);
		
		XMLTransform x_option = new XMLTransform(this.p_content);
		
		Node tnode = x_option.getNode("/smsg/info");
		
		r = pgmxl.costEffectivenessAnalysis(method, tnode);
		
		return r;
	}
	
	private String getObjectProperty(String instanceid)
	{
		String r = null;
		
		CPGXML pgxml = null;
		
		if (instanceid != null && instanceid.length() > 0)
		{
			pgxml = this.getInstanceObj(instanceid);
		}
		
		XMLTransform x_content = new XMLTransform(this.p_content);
		Node tnode = x_content.getNode("/smsg/info");
		
		String option = XMLTransform.GetElementValue(tnode, "option");
		String oname = XMLTransform.GetElementValue(tnode, "name");
		
		if (option.equals("node") == true)
		{
			r = pgxml.getNodeInfo(oname);
		}
		
		return r;
	}
	
	private String referenceDown(String instanceid)
	{
		String r = "<smsg></smsg>";
		
		Object pgmx = this._session.getAttribute("pgmx_" + instanceid);
		CPGXML pgxml = null;
		
		if (pgmx != null)
		{
			pgxml = (CPGXML) pgmx;
			pgxml.dispose();
			pgxml = null;
		}
		
		this._session.removeAttribute("pgmx_" + instanceid);
		
		return r;
	}
	
	private String setObjectProperty(String instanceid, String option)
	{
		String r = null;
		
		CPGXML pgxml = null;
		
		if (instanceid != null && instanceid.length() > 0)
		{
			pgxml = this.getInstanceObj(instanceid);
		}
		
		if (option.equals("node") == true)
		{
			XMLTransform x_content = new XMLTransform(this.p_content);
			r = pgxml.setNodeInfo(x_content);
		}
		
		return r;
	}
	
	private String updateNetwork(String instanceid, String action, Properties prop)
	{
		String r = null;
		
		CPGXML pgxml = null;
		
		if (instanceid != null && instanceid.length() > 0)
		{
			pgxml = this.getInstanceObj(instanceid);
		}
		
		if (pgxml == null)
		{
			XMLTransform xcontent = new XMLTransform(this.p_content);
			instanceid = ClassUtils.generateUID();
			pgxml = new CPGXML(this.p_daemon, null, instanceid);
			pgxml.loadBayesModel(xcontent);
			setPGMXInstance(instanceid, pgxml);
		}
		
		prop.setProperty("instanceid", instanceid);
		XMLTransform x_content = new XMLTransform(this.p_content);
		r = pgxml.updateNetwork(action, x_content, prop);
		
		return r;
	}
	
	private String loadEvidence(String instanceid)
	{
		String r = null;
		
		CPGXML pgmxl = this.getInstanceObj(instanceid);
		
		r = pgmxl.loadEvidence();
		
		return r;
	}
	
	private String setWorkingMode(String instanceid)
	{
		StringBuilder sb = new StringBuilder();
		
		CPGXML pgmxl = this.getInstanceObj(instanceid);
		
		if (pgmxl != null)
		{
			pgmxl.setWorkingMode(CPGXML.INFERENCE_WORKING_MODE);
			pgmxl.refreshGraphicsInfo(sb);
		}
		
		return sb.toString();
	}
	
	private String setNewFinding(String instanceid, String node, String state)
	{
		StringBuilder sb = new StringBuilder();
		
		CPGXML pgmxl = this.getInstanceObj(instanceid);
		
		if (pgmxl != null)
		{
			pgmxl.setNewFinding(node, state);
			pgmxl.refreshGraphicsInfo(sb);
		}
		
		return sb.toString();
	}
	
	private String setDecisionTree(String instanceid)
	{
		StringBuilder sb = new StringBuilder();
		
		CPGXML pgmxl = this.getInstanceObj(instanceid);
		
		if (pgmxl != null)
		{
			pgmxl.setDecisionTree(sb);
		}
		
		return sb.toString();
	}
	
	private CPGXML getInstanceObj(String instanceid)
	{
		Object pgmx = this._session.getAttribute("pgmx_" + instanceid);
		CPGXML pgxml = null;
		
		if (pgmx != null)
		{
			pgxml = (CPGXML) pgmx;
		}
		
		return pgxml;
	}
	
	private void closeInstanceObject(String instanceid)
	{
		Object pgmx = this._session.getAttribute("pgmx_" + instanceid);
		
		if (pgmx != null)
		{
			this._session.removeAttribute("pgmx_" + instanceid);
			
			CPGXML pgxml = (CPGXML) pgmx;
			pgxml.dispose();
			pgxml = null;
		}
	}
	
	public String loadPGMX(String pgmx, String instanceid)
	{
		StringBuilder sb = new StringBuilder();
		
		CPGXML pgxml = new CPGXML(this.p_daemon, pgmx, instanceid);
		
		pgxml.transform(sb);
		
		setPGMXInstance(instanceid, pgxml);
		
		return sb.toString();
	}
	
	private void setPGMXInstance(String instanceid, CPGXML pgxml)
	{
		if (this._session != null)
		{
			this._session.setAttribute("pgmx_" + instanceid, pgxml);
		}
	}
	
	public String getSystemVariables()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<smsg>");
		
		this.getEnumValues("network_types", sb);
		
		this.getEnumValues("role_types", sb);
		
		this.getEnumValues("variable_types", sb);
		
		this.getEnumValues("purpose_types", sb);
		
		this.getEnumValues("analysis_types", sb);
		
		this.getEnumValues("deterministic_axis_variation_type", sb);
		
		this.getEnumValues("scope_types", sb);
		
		this.getEnumValues("potential_types", sb);
			
		sb.append("</smsg>");
		
		return sb.toString();
	}
	
	private void getEnumValues(String enum_name, StringBuilder sb)
	{
		List<String> enum_values = CPGXML.getEnumValues(this.p_daemon, enum_name);
		
		if (enum_values != null)
		{
			sb.append("<" + enum_name + ">");
			
			for (int i=0; i < enum_values.size(); i++)
			{
				String s = enum_values.get(i);
				
				if (s != null && s.length() > 0)
				{
					sb.append("<item value='" + s + "'></item>");
				}
			}
			
			sb.append("</" + enum_name + ">");
		}
	}
}
