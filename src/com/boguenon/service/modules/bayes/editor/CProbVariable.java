package com.boguenon.service.modules.bayes.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Node;

import com.boguenon.utility.ClassUtils;
import com.boguenon.utility.XMLTransform;

public class CProbVariable {
	public static final int P_NONE = 0;
	public static final int P_FINITE_STATES = 1;
	
	public String name;
	public int type;
	public String role;
	public boolean is_input;
	
	private String _comments;
	
	private List<CProbState> _states;
	
	private Properties _props;
	
	private int _x;
	private int _y;
	
	public CProbVariable(Node pnode) {
		this._props = new Properties();
		
		Properties p = XMLTransform.GetElements(pnode);
		
		this.name = p.getProperty("name");
		this.role = p.getProperty("role");
		this.is_input = p.containsKey("isInput") && p.getProperty("isInput").equals("true");
		this._states = new ArrayList<CProbState>();
		
		String mtype = p.containsKey("type") ? p.getProperty("type") : "";
		
		if (mtype.equals("finiteStates") == true)
		{
			this.type = CProbVariable.P_FINITE_STATES;
		}
		
		Node tnode = null;
		List<Node> tnodes = null;
		String tval = null;
		
		tnode = XMLTransform.GetXMLSubNode(pnode, "comments");
		
		if (tnode != null)
		{
			this._comments = XMLTransform.GetTextContent(tnode);
		}
		
		tnode = XMLTransform.GetXMLSubNode(pnode, "States");
		
		if (tnode != null)
		{
			tnodes = XMLTransform.GetChildNode(tnode);
			
			for (int i=0; i < tnodes.size(); i++)
			{
				CProbState stat = new CProbState(tnodes.get(i));
				this._states.add(stat);
			}
		}
		
		tnode = XMLTransform.GetXMLSubNode(pnode, "AdditionalProperties");
		
		if (tnode != null)
		{
			tnodes = XMLTransform.GetChildNode(tnode);
			
			for (int i=0; i < tnodes.size(); i++)
			{
				String pn = XMLTransform.GetElementValue(tnodes.get(i), "name");
				String pv = XMLTransform.GetElementValue(tnodes.get(i), "value");
				
				this._props.put(pn, pv);
			}
		}
		
		tnode = XMLTransform.GetXMLSubNode(pnode, "Coordinates");
		
		if (tnode != null)
		{
			tval = XMLTransform.GetElementValue(tnode, "x");
			if (tval != null)
			{
				this._x = ClassUtils.isInt(tval);
			}
			
			tval = XMLTransform.GetElementValue(tnode, "y");
			if (tval != null)
			{
				this._y = ClassUtils.isInt(tval);
			}
		}
	}
	
	public String getComments()
	{
		return _comments;
	}
	
	public int getX()
	{
		return _x;
	}
	
	public int getY()
	{
		return _y;
	}
}
