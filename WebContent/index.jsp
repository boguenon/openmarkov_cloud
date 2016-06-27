<%
    request.setCharacterEncoding("utf-8");
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
    String _d = formatter.format(new java.util.Date());
    
	String redirectURL = "./launcher/main.jsp?lang=en_US";    
	
	response.sendRedirect(redirectURL);
%>