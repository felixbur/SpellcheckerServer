<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE HTML>
<html lang="de">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<c:out value="${charEnc}"/>">
<meta charset="<c:out value="${charEnc}"/>">
<title><c:out value="${appName}"/>  Server</title>
<link rel="stylesheet" type="text/css" href="format.css" />
</head>
<body onload="javascript:document.mainForm.sendQuery.focus()">

	<p>
		<a href="/<c:out value="${appName}"/>/admin.jsp">admin interface</a><br />
	</p>

	<iframe rel="noreferer" id="output" style="display: none"></iframe>
	<header>
		<h1><c:out value="${appName}"/> Testinterface <c:out value="${version}"/></h1>
	</header>
	<form name="mainForm" action="/<c:out value="${appName}"/>/CheckSpelling" method="get"
		accept-charset="<c:out value="${charEnc}"/>">
		<section>
			<p>Click the microphone symbol (on Chrome browser only) to speak
				a search string (or type).</p>
			<p>
			Dictionaries: 
	  		<input type="checkbox" name="dicDEAT" value="de_DEAT" checked>de_DEAT
  			<input type="checkbox" name="dicCX" value="CX" checked>CX
  			<input type="checkbox" name="dicCXDE" value="CXDE">CXDE<br>
  			
			<input type="hidden" name="userID" value="webtest">
				<input type="hidden" name="targetPage" value="/index.jsp">
				<input type="hidden" name="lang" value="<c:out
					value="${testQueries.language}"/>">
				<input type="hidden" name="userId" value="webtest">					
				<input
				id="q" name="q" speech x-webkit-speech size="80"
				onchange="document.getElementById('form').submit();"
				onwebkitspeechchange="document.getElementById('form').submit();" 
				value="<c:out value="${query}"/>"
				/>
			<p>
				<input type="submit" value="search" name="sendQuery" />
			</p>
	</form>
	</section>
	
	
		<c:if test="${!empty result}">
		<p>
			Result:</br>
			<c:out value="${result}" />
		</p>
	</c:if>
	<c:if test="${!empty time}">
	Used time: <c:out value="${time}" />
	</c:if>
	<c:if test="${!empty result}">
	</c:if>
	
	
	
	<footer> </footer>

</body>
</html>
