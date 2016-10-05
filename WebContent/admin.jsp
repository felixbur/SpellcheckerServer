<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html lang="de">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<c:out value="${charEnc}"/>">
<meta charset="<c:out value="${charEnc}"/>">
<title>Server Admin</title>
<link rel="stylesheet" type="text/css" href="format.css" />
</head>
<body onload="javascript:document.mainForm.sendQuery.focus()">
	<p>
		<a href="/<c:out value="${appName}"/>/index.jsp">test interface</a><br />
		<a href="/<c:out value="${appName}"/>/addUserVocab.jsp">add user vocab interface</a>
	</p>

	<iframe rel="noreferer" id="output" style="display: none"></iframe>
	<header>
		<h1><c:out value="${maintenanceManager.contextName}"/> Admininterface <c:out value="${version}"/></h1>
	</header>
	<table border=0>	
	<tr>
	<td>
		<b>Upload files</b><br/>
	<p>
	
	<c:forEach items="${maintenanceManager.uploads}" var="l"
		varStatus="status">
		<form method="POST" enctype="multipart/form-data" action="/SpellcheckerServer/UploadFile">
				<input type="hidden" name="filename" value="<c:url value="${l.value}"/>">
  	<input type="file" name="upfile"><input type="submit" value="Upload <c:out value="${l.key}" />">
	</form>
	</c:forEach>
	</p>
	
	
		<b>Links</b><br/>
	<p>

	<c:forEach items="${maintenanceManager.links}" var="l"
		varStatus="status">
	<a href="<c:url value="${l.value}"/>"><c:out value="${l.key}" /></a><br/>
	</c:forEach>
	</p>
	<p>
		<b>Commands</b><br/>
	<c:forEach items="${maintenanceManager.actions}" var="a"
		varStatus="status">
			<form action="/<c:out value="${maintenanceManager.contextName}"/>/Maintain" method="POST"
				TARGET="_parent">
				<input type="hidden" name="targetPage" value="/admin.jsp">
				<input type="hidden" name="userID" value="webtest"/>
				<input type="hidden" name="command" value="<c:out value="${a.value}"/>">
				<input type="submit" value="<c:out value="${a.key}" />" />
			</form>
	</c:forEach>
	</p>
	<p>
		<b>Files</b>
	</p>
	<ul>
	<c:forEach items="${maintenanceManager.files}" var="a"
		varStatus="status">
		<li><a
			href="/<c:out value="${maintenanceManager.contextName}"/>/Maintain?targetPage=/admin.jsp&command=show&file=<c:out value="${a.value}"/>&userID=webtest"><c:out
				value="${a.key}" /></a>
	</c:forEach>
	</ul>

	</td><td>

	<p>

		<c:if test="${!empty result}">
		
			<form action="/<c:out value="${maintenanceManager.contextName}"/>/Maintain" method="POST"
				TARGET="_parent">
				<input type="hidden" name="targetPage" value="/admin.jsp">
				<input type="hidden" name="file" value="<c:out value="${maintenanceManager.actFile.name}"/>">
				<input type="hidden" name="command" value="save">
				<input type="hidden" name="userID" value="webtest"/>
			<p>
<c:if test="${not maintenanceManager.actFile.isReadOnly}">
			<b><c:out value="${maintenanceManager.actFile.name}"/></b><input type="submit" value="write changes to disk" />
			<p>
</c:if>
				
				<textarea type="text" cols="120" rows="40" name="content"
				<c:if test="${maintenanceManager.actFile.isReadOnly}">readOnly</c:if>
					accept-charset="<c:out value="${charEnc}"/>"><c:forEach items="${maintenanceManager.actFile.lines}" var="l"><c:out value="${l}" />
</c:forEach></textarea>
				
			</form>
	</p>
	</c:if>
	</td></tr>
</table>
</body>
</html>
