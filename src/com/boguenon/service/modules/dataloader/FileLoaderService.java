package com.boguenon.service.modules.dataloader;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Node;

import com.boguenon.rpc.bogServer;
import com.boguenon.service.system.CommonService;
import com.boguenon.service.system.ProcBase;
import com.boguenon.utility.ClassUtils;
import com.boguenon.utility.BoguenonUtil;
import com.boguenon.utility.XMLTransform;

public class FileLoaderService extends ProcBase
{
	public static final String UTF8_BOM = "\uFEFF";
	
	public static final int n_uid = 0;
	public static final int n_pid = 1;
	public static final int n_type = 2;
	public static final int n_seq = 3;
	public static final int n_cname = 4;
	public static final int n_updatedate = 5;
	public static final int n_content = 6;
	public static final int n_pstatus = 7;
	public static final int n_node = 8;
	public static final int n_desc = 9;
	public static final int n_memo = 10;
	
	public FileLoaderService(bogServer pDaemon, String _token, Integer _purpose, String _address, String _content, boolean isremote, HttpSession _session, boolean is_schedule)
	{
		super(pDaemon, _token, _purpose, _address, _content, isremote, false, _session, is_schedule);
	}
	
	public String processRequest()
	{
		XMLTransform x_addr = new XMLTransform(p_address);
		Node nd_item = x_addr.getNode("/smsg/item");
		
		XMLTransform x_content = new XMLTransform(p_content);
		Node nd_content = x_content.getNode("/smsg/info");
		
		String ret = "<smsg></smsg>";
		
		if (nd_item != null && nd_content != null)
		{
			ret = sendError(300, null, null);
		}
		
		return ret;
	}
	
	public static String removeUTF8BOM(String s)
	{
		if (s.startsWith(UTF8_BOM))
		{
			s = s.substring(1);
		}
		return s;
	}
	
	@SuppressWarnings("unchecked")
	
	public String uploadFile(String path, HttpServletRequest request, HttpServletResponse response)
	{
		String muid = ClassUtils.generateUID();
		
		File tmpDir = new File(path);
		File destinationDir = new File(path);
		
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		fileItemFactory.setSizeThreshold(1*1024*1024); //1 MB
		fileItemFactory.setRepository(tmpDir);

		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		String ret = null;
		
		String filenamemode = request.getParameter("filenamemode");
		
		try 
		{
			/*
			 * Parse the request
			 */
			String _usertoken = this.token;;
			// String _purpose = null;
			String _filename = null;

			List <FileItem> items = (List <FileItem>) uploadHandler.parseRequest(request);
			Iterator<FileItem> itr = items.iterator();
						
			while(itr.hasNext()) 
			{
				FileItem item = itr.next();
				// FileItemStream item = itr.next();
				/*
				 * Handle Form Fields.
				 */
				// InputStream stream = item.openStream();
				if(item.isFormField()) 
				{
					// System.out.println("File Name = "+item.getFieldName()+", Value = "+item.getString());
					String fieldname = item.getFieldName();
					String fieldvalue = item.getString();
					// fieldvalue = Streams.asString(stream, "UTF-8");
					
					if ("AccessToken".equals(fieldname))
					{
						// _usertoken = fieldvalue;
						_usertoken = this.token;
					}
					else if ("ack".equals(fieldname))
					{
						//_purpose = fieldvalue;
					}
					else if ("targetfolder".equals(fieldname))
					{
						destinationDir = new File(CommonService.m_base.BOG_HOME + "/" + fieldvalue);
					}
					else if ("filenamemode".equals(fieldname))
					{
						filenamemode = fieldvalue;
					}
					else if ("orig_filename".equals(fieldname))
					{
						_filename = fieldvalue;
					}
				} 
				else 
				{
					//Handle Uploaded files.
					_filename = item.getName();
					// _filename = Streams.asString(strea)
					// _filename = ClassUtils.getUTF8(_filename);
					
					System.out.println("Field Name = "+item.getFieldName()+
						", File Name = " + item.getName() +
						", Content type = "+item.getContentType()+
						", File Size = "+item.getSize());
					/*
					 * Write file to the ultimate location.
					 */
					// filecontent = item.get();
					File file = null;
					
					if (filenamemode != null && filenamemode.equals("original"))
					{
						File[] subfiles = destinationDir.listFiles();
						int n = _filename.lastIndexOf(".");
						String f_n = (n > -1) ? _filename.substring(0, n) : _filename;
						String f_e = (n > -1) ? _filename.substring(n+1) : "";
						
						int nmax = 0;

						for (int i=0; i < subfiles.length; i++)
						{
							String fname = subfiles[i].getName();
							if (fname.startsWith(f_n))
							{
								n = fname.lastIndexOf(".");
								String _n = (n > -1) ? fname.substring(0, n) : fname;
								// String _e = (n > -1) ? fname.substring(n+1) : "";
								
								_n = _n.substring(f_n.length());
								
								if (_n.equals("") == true)
								{
									nmax = 1;
								}
								else if (_n.startsWith("_") == true)
								{
									_n = _n.substring(1);
								}
								int m = ClassUtils.isInt(_n);
								if (m > -1)
								{
									nmax = Math.max(nmax, m + 1);
								}
							}
						}
						
						if (nmax > 0)
						{
							_filename = f_n + "_" + nmax + "." + f_e;
						}
						file = new File(destinationDir, _filename);
					}
					else
					{
						file = new File(destinationDir, muid);
					}
					
					item.write(file);
				}
			}
			
			if (_usertoken != null)
			{
				String wlog = writeUploadLog(_usertoken, response, _filename, muid);
				_filename = "{ENC}" + ClassUtils.base64Encode(_filename.getBytes("UTF-8"));
				
				if (wlog != null)
				{
					ret = wlog;
				}
			}
			
			if (muid == null)
			{
				ret = BoguenonUtil.printErr(900, p_daemon, "Error encountered while uploading file", user_locale, null);
			}
			else
			{
				ret = "<smsg><result uid='" + muid + "' filename='" + _filename + "'/></smsg>";
			}
		}
		catch(FileUploadException ex) 
		{
			com.boguenon.service.common.Logger.logException(ex);
			// System.out.println("Error encountered while parsing the request" + ex.getMessage());
			ret = BoguenonUtil.printErr(900, p_daemon, ex.getMessage(), user_locale, ex);
		} 
		catch(Exception ex) 
		{
			com.boguenon.service.common.Logger.logException(ex);
			// System.out.println("Error encountered while uploading file" + ex.getMessage());
			ret = BoguenonUtil.printErr(900, p_daemon, ex.getMessage(), user_locale, ex);
		}
		
		try
		{
			response.setContentType("text/xml; charset=UTF-8");
			PrintWriter out = new PrintWriter(new java.io.OutputStreamWriter(response.getOutputStream(), "UTF8"), true); // .getWriter();
			out.print(ret);
			out.flush();
			out.close();
			out = null;
		}
		catch (Exception err)
		{
			
		}
		
		return ret;
	}
	
	private String writeUploadLog(String token, HttpServletResponse response, String filename, String mid)
	{
		String result = null;
		
		try
		{
			String fileext = "";
			
			if (filename.lastIndexOf(".") > -1)
			{
				fileext = filename.substring(filename.lastIndexOf(".") + 1);
				fileext = fileext.toUpperCase();
				
				if (fileext.length() > 9)
				{
					fileext.substring(0, 9);
				}
			}
			
			filename = "{ENC}" + ClassUtils.base64Encode(filename.getBytes("UTF-8"));
		}
		catch (Exception ei)
		{
			com.boguenon.service.common.Logger.logException(ei);
			result = BoguenonUtil.printErr(900, p_daemon, "Error while save upload file info " + ei.getMessage(), user_locale, ei);
		}
		finally
		{
			
		}
		
		return result;
	}
}
