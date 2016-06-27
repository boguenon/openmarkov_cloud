package com.boguenon.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class OSUtil 
{
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static Runtime runtime = Runtime.getRuntime();
	
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}
	
	
	
	public static boolean createDirectory(File parent, String name) 
	{
		boolean r = false;
		
		File dir = new File(parent.getAbsolutePath() + "/" + name);
		
		if (!dir.exists())
		{
			r = dir.mkdir();
		}
		else
		{
			r = true;
		}
		
		return r;
	}
	
	public static void writeToFile(String path, String fname, String content)
	{
		File file = new File(path + "/" + fname);
		 
		OSUtil.writeToFile(file, content);
	}
	
	public static void writeToFile(File file, String content)
	{
		FileOutputStream fo = null;
		OutputStreamWriter ow = null;
		BufferedWriter bw = null;
		
		try
		{
			if (!file.exists()) 
			{
				file.createNewFile();
			}
			
			// File f = file.getAbsoluteFile();
			fo = new FileOutputStream(file);
			ow = new OutputStreamWriter(fo, "UTF-8");
			// fw = new FileWriter();
			bw = new BufferedWriter(ow);
			bw.write(content);
			bw.flush();
		}
		catch (Exception e)
		{
			com.boguenon.service.common.Logger.logException(e);
		}
		finally
		{
			try
			{
				if (bw != null)
					bw.close();
				bw = null;
				
				if (ow != null)
					ow.close();
				ow = null;
				
				if (fo != null)
					fo.close();
				fo = null;
			}
			catch (Exception ex)
			{
				
			}
		}
	}
	
	public static void writeByteToFile(String path, String fname, byte[] bt)
	{
		File f = new File(path + "/" + fname);
		if (f.exists() == false)
		{
			FileOutputStream fos = null;
			BufferedOutputStream bout = null;
			try
			{
				fos = new FileOutputStream(f);
				bout = new BufferedOutputStream(fos);
				
				bout.write(bt);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally 
			{
				try
				{
					if (fos != null)
						fos.close();
					fos = null;
					
					if (bout != null)
						bout.close();
					bout = null;
				}
				catch (Exception ex)
				{
					
				}
			}
		}
	}
	
	public static String readByteFile(String filename)
	{
		File f = new File(filename);
		
		String r = null;
		
		InputStream is = null;
		FileInputStream fis = null;
		
		try
		{
			if (f.exists() && f.canRead() == true)
			{
				int totalBytesRead = 0;
				byte[] bt = new byte[(int) f.length()];
				
				fis = new FileInputStream(f);
		        is = new BufferedInputStream(fis);
		        
		        while(totalBytesRead < bt.length)
		        {
		        	int bytesRemaining = bt.length - totalBytesRead;
		            //input.read() returns -1, 0, or more :
		            int bytesRead = is.read(bt, totalBytesRead, bytesRemaining); 
		            if (bytesRead > 0)
		            {
		            	totalBytesRead = totalBytesRead + bytesRead;
		            }
		        }
		        
		        r = ClassUtils.base64Encode(bt);
		        
		        if (is != null)
		        {
		        	is.close();
		        }
		        is = null;
		        
		        if (fis != null)
		        {
		        	fis.close();
		        }
		        fis = null;
			}
		}
		catch (Exception ex)
		{
			r = null;
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if (is != null)
		        {
		        	is.close();
		        }
		        is = null;
		        
		        if (fis != null)
		        {
		        	fis.close();
		        }
		        fis = null;
			}
			catch (Exception e)
			{
				
			}
		}
		
		return r;
	}
	
	public static String OSname()
	{
		return System.getProperty("os.name");
	}
	
	public static String OSversion()
	{
		return System.getProperty("os.version");
	}
	
	public static String OSArch()
	{
		return System.getProperty("os.arch");
	}
	
	public static int OSCores()
	{
		return runtime.availableProcessors();
	}
	
	public static long totalMem() 
	{
        return Runtime.getRuntime().totalMemory();
    }
	
	public long usedMem() 
	{
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
	
	public static String GetDiskInfo() {
		/* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();
        StringBuilder sb = new StringBuilder();

        /* For each filesystem root, print some info */
        sb.append("<diskinfo>");
        for (File root : roots) 
        {
        	String diskname = root.getAbsolutePath();
        	diskname = ClassUtils.escapeXML(diskname);
        	sb.append("<disk name='" + diskname + "'");
        	sb.append(" total='" + Long.toString(root.getTotalSpace()) + "'");
        	sb.append(" free='" + Long.toString(root.getFreeSpace()) + "'");
        	sb.append(" usable='" + Long.toString(root.getUsableSpace()) + "'");
        	sb.append("/>");
        }
        sb.append("</diskinfo>");
        return sb.toString();
	}
	
	public static String GetMemInfo() {
        StringBuilder sb = new StringBuilder();
        
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        
        sb.append("<meminfo");
        sb.append(" free='" + Long.toString(freeMemory / 1024) + "'");
        sb.append(" allocated='" + Long.toString(allocatedMemory / 1024) + "'");
        sb.append(" max='" + Long.toString(maxMemory / 1024) + "'");
        sb.append(" appfree='" + Long.toString((maxMemory - allocatedMemory) / 1024) + "'");
        sb.append(" totalfree='" + Long.toString(((freeMemory + (maxMemory - allocatedMemory)) / 1024)) + "'");
        sb.append("></meminfo>");
        return sb.toString();
    }
	
	public static String GetOsInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("<osinfo>");
        sb.append("<n name='osname'>" + OSUtil.OSname() + "</n>");
        sb.append("<n name='version'>" +  OSUtil.OSversion() + "</n>");
        sb.append("<n name='osarch'>" + OSUtil.OSArch() + "</n>");
        sb.append("<n name='cores'>" + OSUtil.OSCores() + "</n>");
        sb.append("</osinfo>");
        return sb.toString();
    }
	
	public static String getSystemResources()
	{
		StringBuilder r = new StringBuilder();
		
		r.append("<smsg><results>");
		r.append(GetOsInfo());
		r.append(GetDiskInfo());
		r.append(GetMemInfo());
		r.append("</results></smsg>");
		
		return r.toString();
	}
	
	public static boolean moveFile(String fsrc, String ftgt)
	{
		return OSUtil.moveFile(fsrc,  ftgt, true);
	}
	
	public static boolean moveFile(String fsrc, String ftgt, boolean delsrc)
	{
		boolean r = true;
		
		File fs = new File(fsrc);
		
		if (fs != null && fs.exists() == true && fs.canRead() == true)
		{
			FileInputStream fis = null;
			FileOutputStream fos = null;
			
			try 
			{
				fis = new FileInputStream(fs);
				fos = new FileOutputStream(ftgt);
	             
				byte[] buffer = new byte[1024];
				int noOfBytes = 0;

				while ((noOfBytes = fis.read(buffer)) != -1) 
				{
					fos.write(buffer, 0, noOfBytes);
				}
				
				if (fos != null)
					fos.close();
				fos = null;
				
				if (fis != null)
					fis.close();
				fis = null;
				
				if (delsrc == true)
				{
					fs.delete();
				}
			}
			catch (Exception ex)
			{
				r = false;
				com.boguenon.service.common.Logger.logException(ex);
			}
			finally
			{
				try
				{
					if (fos != null)
						fos.close();
					fos = null;
					
					if (fis != null)
						fis.close();
					fis = null;
				}
				catch (Exception e)
				{
					
				}
			}
		}
		
		return r;
	}
	
	public static String readFile(File f)
	{
		return readFile(f, "UTF-8");
	}
	
	public static String readFile(File f, String encode)
	{
		FileInputStream fi = null;
		InputStreamReader is = null;
		BufferedReader dis = null;
		
		StringBuilder sb = new StringBuilder();
		
		try
		{
			fi = new FileInputStream(f);
			if (encode == null)
			{
				is = new InputStreamReader(fi);
			}
			else
			{
				is = new InputStreamReader(fi, encode);
			}
			dis = new BufferedReader(is);
			
			String line = null;
			
			while ((line = dis.readLine()) != null)
			{
				sb.append(line + "\n");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
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
			}
			catch (Exception e)
			{
				
			}
		}
		
		return sb.toString();
	}
	
	
	public static final int D_SUCCESS = 0;
	public static final int D_NOT_EXIST = 1;
	public static final int D_FAILED = 2;
	
	public static int deleteFile(String fname)
	{
		boolean b_success = false;
		int m_file = D_FAILED;
		
		File f = new File(fname);
		
		if (f.exists() == true && f.canWrite() == true)
		{
			b_success = f.delete();
			f = null;
			
			if (b_success == true)
			{
				m_file = D_SUCCESS;
			}
		}
		else
		{
			m_file = D_NOT_EXIST;
		}
		
		return m_file;
	}
}
