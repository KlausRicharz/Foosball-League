<%@ page session="false" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%
de.hbt.kicker.elo.v2.service.SpielerService spielerService = (de.hbt.kicker.elo.v2.service.SpielerService) request.getAttribute("spielerService");
request.setAttribute("inaktiveSpieler", spielerService.findAllInaktiveSpieler());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<title>Inaktive Spieler</title>

<jsp:include flush="true" page="imports.jsp" />

</head>

<body>
<jsp:include flush="true"  page="menu.jsp" />

<table border="0" align="center" cellspacing="0" cellpadding="3">
  	<tr>
		<td colspan="4" align="center"><h1>Inaktive Spieler</h1></td>
	</tr>
	<tr>
		<th>
			Name 
		</th>
		<th>
			ELO-Wert
		</th>
		<th>
			Spiele
		</th>
		<th>
			Siege
		</th>
	</tr>
	<c:forEach items="${inaktiveSpieler}" var="row" varStatus="counter">
		<c:choose>
			<c:when test="${counter.count%2==0}">
		<tr class="primarycolor"> 
			</c:when>
			<c:otherwise>
		<tr class="secondarycolor">
			</c:otherwise>
		</c:choose>
			<td align="center"><span class="spieler_name">${row.name}</a></td>
			<td align="center">${row.punktzahl.punktzahl}</td>
			<td align="center">${row.anzahlSpiele}</td>
			<td align="center">${row.anzahlSiege}</td>
		</tr>
	</c:forEach>
</table>

</body>
</html>