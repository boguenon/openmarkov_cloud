package com.boguenon.utility;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

@SuppressWarnings("deprecation")
public class XMLTransform
{
	private String g_strdoc;
	private Document xml;
	public Node rootnode;
	
	public boolean hasError;
	
	public XMLTransform()
	{
	}
	
	public XMLTransform(String m_source)
	{
		hasError = false;
		
		g_strdoc = m_source;
		
		initDocument();
	}
	
	private void initDocument()
	{
		try
		{
			if (g_strdoc.endsWith("\r\n") == true)
			{
				g_strdoc = g_strdoc.substring(0, g_strdoc.length() - "\r\n".length());
			}
			
			if (g_strdoc.startsWith("<") == true && g_strdoc.endsWith("/>") == true)
			{
				int n = g_strdoc.indexOf(" ");
				String nodename = null;
				
				if (n > -1)
				{
					nodename = g_strdoc.substring(1, n);
				}
				else
				{
					nodename = g_strdoc.substring(1, g_strdoc.length() - 2);
				}
				g_strdoc = g_strdoc.substring(0, g_strdoc.length() - "/>".length());
				g_strdoc = g_strdoc + "></" + nodename + ">";
			}
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			StringReader sr = new StringReader(g_strdoc);
			InputSource is = new InputSource(sr);
			
			xml = docBuilder.parse (is);
			xml.getDocumentElement().normalize();
			
			rootnode = xml.getDocumentElement();
		}
		catch (Exception e)
		{
			hasError = true;
			com.boguenon.service.common.Logger.logException(e);
		}
	}
	
	public Document getDocument()
	{
		return xml;
	}
	
	public Node rootNode()
	{
		return rootnode;
	}
	
	public Node createNode(String nodename)
	{
		Element nnode = xml.createElement(nodename);
		return nnode;
	}
	
	public static void SetAttribute(Node node, String name, String value)
	{
		String strkey;
		
		boolean bf = false;
		
		String lname = name.toLowerCase();
		
		for (int i=0; i < node.getAttributes().getLength(); i++)
		{
			strkey = node.getAttributes().item(i).getNodeName().toLowerCase();
			
			if (strkey.equals(lname))
			{
				Node attr = node.getAttributes().item(i);
				attr.setNodeValue(value);
				
				bf = true;
				break;
			}
		}
		
		if (bf == false)
		{
			Document xml = node.getOwnerDocument();
			Attr nattr = xml.createAttribute(name);
			nattr.setValue(value);
			
			NamedNodeMap attrmap = node.getAttributes();
			attrmap.setNamedItem(nattr);
		}
	}
	
	public static void RemoveAttribute(Node node, String name)
	{
		String strkey;
		
		for (int i=0; i < node.getAttributes().getLength(); i++)
		{
			strkey = node.getAttributes().item(i).getNodeName();
			
			if (strkey.equals(name))
			{
				Node attr = node.getAttributes().item(i);
				node.removeChild(attr);
				break;
			}
		}
	}
	
	public static void RemoveNode(Node node)
	{
		if (node.getParentNode() != null)
		{
			node.getParentNode().removeChild(node);
		}
	}
	
	public Node getNode(String path)
	{
		Node unode = rootnode;
		return GetXMLNode(unode, path);
	}
	
	public static Node AppendNode(Node node, String tagName)
	{
		return XMLTransform.AppendNode(node, tagName, null);
	}
	
	public static Node AppendNode(Node node, String tagName, Node refNode)
	{
		Element cnode = node.getOwnerDocument().createElement(tagName);
		node.getOwnerDocument().adoptNode(cnode);
		
		if (refNode != null)
		{
			node.insertBefore(cnode, refNode);
		}
		else
		{
			node.appendChild(cnode);
		}
		
		return cnode;
	}
	
	public static Node CreateNode(Node node, String tagName)
	{
		Element cnode = node.getOwnerDocument().createElement(tagName);
		node.getOwnerDocument().adoptNode(cnode);
		
		return cnode;
	}
	
	public static Node AppendNode(Node node, Node tnode)
	{
		node.appendChild(tnode);
		
		return tnode;
	}
	
	public static void MoveNode(Node tnode, Node rnode)
	{
		tnode.appendChild(rnode);
	}
	
	public static void SetNodeIndex(Node tnode, int index)
	{
		Node pnode = tnode.getParentNode();
		
		if (pnode != null)
		{
			int m_index = -1;
			
			List<Node> cnodes = GetChildNode(pnode);
			
			for (int i=0; i < cnodes.size(); i++)
			{
				if (cnodes.get(i).equals(tnode) == true)
				{
					m_index = i;
					break;
				}
			}
			
			if (m_index > -1 && m_index != index && cnodes.size() > index)
			{
				pnode.insertBefore(tnode, cnodes.get(index));
			}
		}
	}
		
	public static Node AppendCloneNode(Node unode, Node clonenode)
	{
		// String tagName = clonenode.getNodeName();
		
		Node nnode = clonenode.cloneNode(true);
		unode.getOwnerDocument().adoptNode(nnode);
		unode.appendChild(nnode);
		
		return nnode;
	}

	public static void ClearSubNode(Node node)
	{
		if (node.hasChildNodes() == true)
		{
			for (int i=0; i < node.getChildNodes().getLength(); i++)
			{
				Node snode = node.getChildNodes().item(i);
				node.removeChild(snode);
			}
		}
	}
		
	public static Node GetXMLNode(Node unode, String path)
	{
		Node nd = null;
		
		StringTokenizer tok = new StringTokenizer(path, "/");
		String fname = "";
		
		if (path.charAt(0) == '/' && tok.hasMoreElements() == false)
		{
			return unode;
		}
		
		if (tok.hasMoreElements() == true)
		{
			fname = tok.nextElement().toString();
		}
		
		if (unode == null || (unode != null && !unode.getNodeName().equals(fname)))
		{
			System.out.println("Warn: XMLNode path error " + path + ":" + unode.getNodeName());
			return null;
		}
		
		while (tok.hasMoreElements())
		{
			fname = tok.nextElement().toString();
			
			if (!fname.equals(""))
			{
				unode = FindChildNode(unode, fname);
				
				if (unode == null)
					break;
			}
		}
		
		nd = unode;
		
		return nd;
	}
	
	public static Node GetXMLSubNode(Node unode, String path)
	{
		Node nd = null;
		
		StringTokenizer tok = new StringTokenizer(path, "/");
		String fname = "";
		
		if (path.charAt(0) == '/' && tok.hasMoreElements() == false)
		{
			return unode;
		}
		
		if (tok.hasMoreElements() == true)
		{
			fname = tok.nextElement().toString();
		}
		
		if (path.charAt(0) == '/' && !unode.getNodeName().equals(fname))
		{
			System.out.println("Warn: XMLNode path error " + path + ":" + unode.getNodeName());
			return null;
		}
		else if (path.charAt(0) == '/')
		{
			fname = tok.nextElement().toString();
		}
		else
		{
			unode = FindChildNode(unode, fname);
			
			if (unode == null)
				return null;
			
			if (tok.hasMoreTokens())
			{
				fname = tok.nextElement().toString();
				unode = FindChildNode(unode, fname);
			}
			else
			{
				fname = null;
			}
		}
		
		while (tok.hasMoreElements())
		{
			fname = tok.nextToken().toString();
			if (fname != null && fname.equals("") == false)
			{
				unode = FindChildNode(unode, fname);
				
				if (unode == null)
					break;
			}
		}
		
		nd = unode;
		
		return nd;
	}
	
	private static Node FindChildNode(Node unode, String path)
	{
		Node nd = null;
		int i;
		
		for (i=0; i < unode.getChildNodes().getLength(); i++)
		{
			String nodename = unode.getChildNodes().item(i).getNodeName();
			if (nodename.equals(path) || (nodename.equals("") == false && path.equals("*") == true))
			{
				nd = unode.getChildNodes().item(i);
				break;
			}
		}
		
		return nd;
	}
	
	public static String GetNodeName(Node node)
	{
		return node.getNodeName();
	}
	
	public static void SetNodeName(Node node, String nodename)
	{
		Document doc = node.getOwnerDocument();
		doc.renameNode(node, null, nodename);
	}
	
	public static String GetElementValue(Node node, String name)
	{
		return GetElementValue(node, name, true);
	}
	
	public static String GetElementValue(Node node, String name, boolean null_string)
	{
		Properties prop = GetElements(node);
		String value = (null_string == false ? null : "");
		
		if (prop.getProperty(name) != null)
			value = prop.getProperty(name);
		
		return value;
	}
	
	public static Properties GetElements(Node node)
	{
		return GetElements(node, true);
	}
	
	public static String GetSubNodeParam(Node node, String param)
	{
		String r = null;
		
		Node snode = XMLTransform.FindChildNode(node, param);
		
		if (snode != null)
		{
			r = XMLTransform.GetTextContent(snode);
		}
		else
		{
			r = XMLTransform.GetElementValue(node, param, false);
		}
		
		return r;
	}
	
	public static String GetSubNodeText(Node node, String nodename)
	{
		String r = null;
		Node snode = XMLTransform.FindChildNode(node, nodename);
		if (snode != null)
		{
			r = XMLTransform.GetTextContent(snode);
		}
		return r;
	}
	
	public static Properties GetElements(Node node, boolean casesensitive)
	{
		int i;
		Properties prop = new Properties();
		
		String strkey;
		String strvalue;
		
		for (i=0; i < node.getAttributes().getLength(); i++)
		{
			strkey = node.getAttributes().item(i).getNodeName();
			if (casesensitive == false)
			{
				strkey = strkey.toLowerCase();
			}
			strvalue = node.getAttributes().item(i).getNodeValue();
			
			prop.setProperty(strkey, strvalue);
		}
		
		return prop;
	}
	
	public static void SetElementValue(Node node, String name, String value)
	{
		String strkey;
		
		boolean bf = false;
		
		for (int i=0; i < node.getAttributes().getLength(); i++)
		{
			strkey = node.getAttributes().item(i).getNodeName();
			
			if (strkey.equals(name))
			{
				Node attr = node.getAttributes().item(i);
				attr.setNodeValue(value);
				
				bf = true;
				break;
			}
		}
		
		if (bf == false)
		{
			Attr nattr = node.getOwnerDocument().createAttribute(name);
			nattr.setValue(value);
			
			NamedNodeMap attrmap = node.getAttributes();
			attrmap.setNamedItem(nattr);
		}
	}
	
	public static String innerXML(Node node)
	{
		return toXMLString(node);
	}
	
	public static String innerXML_transformer(Node node)
	{
		// StringWriter sw = new StringWriter();
		// serializeXML(node, sw);
		// return sw.toString();
		String xmlString = null;

		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			
			StringWriter sw = new StringWriter();
			
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(node);
			transformer.transform(source, result);
			
			xmlString = result.getWriter().toString();
		}
		catch (Exception ex)
		{
			System.out.println("Error while get innerMXL");
			xmlString = null;
		}
		
		return xmlString;
	}
	
	public static void serializeXML(Node node, Writer sw)
	{
		PrintWriter out = new PrintWriter(sw);
		PrintNode (node, out);
		out.flush();
	}
	
	public static String toXMLString(Node node)
	{
		StringWriter sw = new StringWriter();
		serializeXML(node, sw);
		return sw.toString();
	}
	
	public static String prettyFormat_old(String xstr)
	{
		String ret = xstr;
		try
		{
			final Document doc = parseXmlFile(xstr);
			OutputFormat format = new OutputFormat(doc);
			format.setLineWidth(65);
			format.setIndenting(true);
			format.setIndent(4);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(doc);
			
			ret = out.toString();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
	}
	
	public static String prettyFormat(String xstr)
	{
		final Document doc = parseXmlFile(xstr);
		
		String mstr = xstr;
		try
		{
			Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	
			StreamResult streamResult = new StreamResult(new StringWriter());
			DOMSource domSource = new DOMSource(doc);
			transformer.transform(domSource, streamResult);
			mstr = streamResult.getWriter().toString();
		}
		catch (Exception ex)
		{
			com.boguenon.service.common.Logger.logException(ex);
			mstr = xstr;
		}
		
		return mstr;
	}
	
	private static Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	public static void PrintNode(Node node, PrintWriter out)
	{
		if (node == null)
			return;
		
		boolean hasChildren = false;
        int type = node.getNodeType();
        
        switch (type)
        {
        case Node.DOCUMENT_NODE :
        {
        	NodeList children = node.getChildNodes();

            if (children != null)
            {
                int numChildren = children.getLength();

                for (int i = 0; i < numChildren; i++)
                {
                    PrintNode(children.item(i), out);
                }
            }
            break;
        }
        case Node.ELEMENT_NODE :
        {
        	out.print('<' + node.getNodeName());
        	
            NamedNodeMap attrs = node.getAttributes();
            int len = (attrs != null) ? attrs.getLength() : 0;

            for (int i = 0; i < len; i++)
            {
                Attr attr = (Attr)attrs.item(i);
                String evalue = ClassUtils.escapeXML(attr.getValue());
                out.print(' ' + attr.getNodeName() +"=\"" + evalue + '\"');
            }

            NodeList children = node.getChildNodes();

            if (children != null)
            {
                int numChildren = children.getLength();

                hasChildren = (numChildren > 0);

                if (hasChildren)
                {
                    out.print('>');
                }

                for (int i = 0; i < numChildren; i++)
                {
                    PrintNode(children.item(i), out);
                }
            }
            else
            {
                hasChildren = false;
            }

            if (!hasChildren)
            {
                out.print("/>");
            }

            break;
        }
        case Node.ENTITY_REFERENCE_NODE :
        {
            out.print('&');
            out.print(node.getNodeName());
            out.print(';');
            break;
        }

	    case Node.CDATA_SECTION_NODE :
        {
            out.print("<![CDATA[");
            out.print(node.getNodeValue());
            out.print("]]>");
            break;
        }
	
	    case Node.TEXT_NODE :
        {
            out.print(node.getNodeValue().trim());
            break;
        }
	
	    case Node.COMMENT_NODE :
        {
            out.print("<!--");
            out.print(node.getNodeValue());
            out.print("-->");
            break;
        }
	
	    case Node.PROCESSING_INSTRUCTION_NODE :
        {
            out.print("<?");
            out.print(node.getNodeName());

            String data = node.getNodeValue();

            if (data != null && data.length() > 0)
            {
                out.print(' ');
                out.print(data);
            }

            out.println("?>");
            break;
        }
        }
        
        if (type == Node.ELEMENT_NODE && hasChildren == true)
        {
            out.print("</");
            out.print(node.getNodeName());
            out.print('>');
            hasChildren = false;
        }
	}
	
	public static String GetTextContent(Node node)
	{
		String r = null;
		
		if (node != null && node.hasChildNodes() == true)
		{
			Node cnode = null;
			
			for (int i=0; i < node.getChildNodes().getLength(); i++)
			{
				cnode = node.getChildNodes().item(i);
				
				if (cnode.getNodeType() == Node.TEXT_NODE || cnode.getNodeType() == Node.CDATA_SECTION_NODE)
				{
					r = cnode.getNodeValue();
					break;
				}
				else if (cnode.getNodeType() == Node.ELEMENT_NODE)
				{
					r = GetTextContent(cnode);
				}
			}
		}
		
		return r;
	}

	public static void SetTextContent(Node node, String value, boolean iscdata)
	{
		boolean b_proc = false;
		
		if (node != null && node.hasChildNodes() == true)
		{
			Node cnode = null;
			
			for (int i=0; i < node.getChildNodes().getLength(); i++)
			{
				cnode = node.getChildNodes().item(i);
				
				if (cnode.getNodeType() == Node.TEXT_NODE || cnode.getNodeType() == Node.CDATA_SECTION_NODE)
				{
					cnode.setTextContent(value);
					b_proc = true;
					break;
				}
			}
		}
		
		if (b_proc == false && node != null)
		{
			Document xml = node.getOwnerDocument();
			Node nchild = null;
			if (iscdata == true)
			{
				nchild = xml.createCDATASection(value);
			}
			else
			{
				nchild = xml.createTextNode(value);
			}
			node.appendChild(nchild);
		}
	}
		
	public void appendAttribute(Node node, String name, String value)
	{
		NamedNodeMap attrmap = node.getAttributes();
		Attr attr = xml.createAttribute(name);
		attr.setValue(value);
		attrmap.setNamedItem(attr);
	}
	
	public static boolean HasOwnProperty(Node node, String value)
	{
		boolean inc = false;
		
		for (int i=0; i < node.getChildNodes().getLength(); i++)
		{
			if (node.getChildNodes().item(i).getNodeName().equals(value) == true)
			{
				inc = true;
				break;
			}
		}
		
		return inc;
	}
	
	public static List<Node> GetChildNode(Node node)
	{
		return GetChildNode(node, null);
	}
	
	public static List<Node> GetChildNode(Node node, String ndname)
	{
		List <Node> nlist = new ArrayList <Node> ();
		
		for (int i=0; i < node.getChildNodes().getLength(); i++)
		{
			Node n = node.getChildNodes().item(i);
			String nodename = n.getNodeName();
			Short nodetype = n.getNodeType();
			if (nodename.equals("") == false && nodetype == Node.ELEMENT_NODE && (ndname == null || (ndname != null && ndname.equals(nodename) == true)))
			{
				nlist.add(n);
			}
		}
		
		return nlist;
	}
	
	public static void RemoveChild(Node node)
	{
		List<Node> childs = XMLTransform.GetChildNode(node);
		for (int i=0; i < childs.size(); i++)
		{
			node.removeChild(childs.get(i));
		}
	}
	
	public void saveXML(File f)
	{
		FileOutputStream fo = null;
		OutputStreamWriter ow = null;
		BufferedWriter writer = null;
		
		try
		{
			fo = new FileOutputStream(f);
			ow = new OutputStreamWriter(fo, "UTF-8");
			writer = new BufferedWriter(ow);
			writer.write(XMLTransform.toXMLString(this.rootnode));
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
}