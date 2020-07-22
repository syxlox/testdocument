<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.polarion.alm.tracker.model.IWorkItem" %>

<%
	    Object[] info  = (Object[])request.getAttribute("properties");
		String display = (String) info[0];
%>
Return: <%=display%>