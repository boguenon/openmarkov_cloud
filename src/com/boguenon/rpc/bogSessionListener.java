package com.boguenon.rpc;

import javax.servlet.http.*;

public class bogSessionListener implements HttpSessionListener
{
	public void sessionCreated(HttpSessionEvent event) 
	{
		HttpSession session = event.getSession();
		System.out.println("Session created: " + session);
	}
	
	public void sessionDestroyed(HttpSessionEvent event)
	{
		HttpSession session = event.getSession();
		
		System.out.println("Session invalidated: " + session);
		
		try
		{
			if (session != null)
			{
				long createdTime = session.getCreationTime();
			    long lastAccessedTime = session.getLastAccessedTime();
			    int maxInactiveTime = session.getMaxInactiveInterval();
			    long currentTime = System.currentTimeMillis();
			    
				System.out.println("Session Session Id :" + session.getId() );
			    System.out.println("Session Created Time : " + createdTime);
			    System.out.println("Session Last Accessed Time : " + lastAccessedTime);
			    System.out.println("Session Current Time : " + currentTime);
			    boolean possibleSessionTimeout = (currentTime-lastAccessedTime) >= (maxInactiveTime*1000);
		
			    System.out.println("Session Possbile Timeout : " + possibleSessionTimeout);
			}
			else
			{
				System.out.println("Session information destroyed -- ");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}