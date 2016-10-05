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
	</p>

	<iframe rel="noreferer" id="output" style="display: none"></iframe>
	<header>
		<h1><c:out value="${maintenanceManager.contextName}"/> <c:out value="${version}"/></h1>
	</header>
	<p>Das User Vokabular erweitern indem neu zu erkennende W&ouml;rter in das Textfeld unten eintragen werden.<br/>
	Kein Undo  m&ouml;glich!
	</p>	
	<c:if test="${!empty result}">
	<p><c:out value="${result}"/><p/>
	</c:if>
	<table border=0>	
	<tr><td>
	<p>
			<form action="/<c:out value="${maintenanceManager.contextName}"/>/AddUserVocab" method="POST"
				TARGET="_parent">
				<p><input type="submit" value="Zum Server senden und neu starten" /><p/>				
				<input type="hidden" name="targetPage" value="/addUserVocab.jsp">
				<input type="hidden" name="file" value="useradd.txt">
				<textarea type="text" cols="60" rows="20" name="content" accept-charset="<c:out value="${charEnc}"/>"><c:forEach items="${userVocabAdd.actFile.lines}" var="l"><c:out value="${l}" />
</c:forEach></textarea>
			</form>
	</p>
	</td></tr>
</table>
</body>
</html>
