<%@ page session="false" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%
de.hbt.kicker.elo.v2.service.TeamService teamService = (de.hbt.kicker.elo.v2.service.TeamService) request.getAttribute("teamService");
request.setAttribute("teamRangliste", teamService.getTeamRangliste());
/*
request.setAttribute("inaktiveTeams", teamService.findAllInaktiveTeams());
*/
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<title>Team-Rangliste</title>

<jsp:include flush="true" page="imports.jsp" />

</head>

<body>
<jsp:include flush="true"  page="menu.jsp" />

<table border="0" align="center" cellspacing="0" cellpadding="3">
  	<tr>
		<td colspan="6" align="center"><h1>ELO-Werte (Teams)</h1></td>
	</tr>
	<tr>
		<th>
			Rang
		</th>
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
		<th>
			Siege (%)
		</th>
	</tr>
	<c:forEach items="${teamRangliste}" var="row" varStatus="counter">
		<c:choose>
			<c:when test="${counter.count%2==0}">
		<tr class="primarycolor"> 
			</c:when>
			<c:otherwise>
		<tr class="secondarycolor">
			</c:otherwise>
		</c:choose>
		<c:url value="teamDetails.jsp" var="detailsurl">
			<c:param name="teamId" value="${row.id}" />
		</c:url>
			<td align="center">${row.rang}</td>
			<td align="center"><a href="${detailsurl}">${row.name}</a></td>
			<td align="center">${row.punktzahl}</td>
			<td align="center">${row.spiele}</td>
			<td align="center">${row.siege}</td>
			<td align="center">${row.siegeProzent}</td>
		</tr>
	</c:forEach>
</table>

<%--

<table border="0" align="center" cellspacing="0" cellpadding="3">
  	<tr>
		<td colspan="5" align="center"><h1>Inaktive Teams</h1></td>
	</tr>
	<tr>
		<th>
			Name 
		</th>
		<th>
			ELO-Wert
		</th>
	</tr>
	<c:forEach items="${inaktiveTeams}" var="row" varStatus="counter">
		<c:choose>
			<c:when test="${counter.count%2==0}">
		<tr class="primarycolor"> 
			</c:when>
			<c:otherwise>
		<tr class="secondarycolor">
			</c:otherwise>
		</c:choose>
		<c:url value="teamDetails.jsp" var="detailsurl">
			<c:param name="teamId" value="${row.id}" />
		</c:url>
			<td align="center"><a href="${detailsurl}">${row.spieler1.name} / ${row.spieler2.name}</a></td>
			<td align="center">${row.punktzahl.punktzahl}</td>
		</tr>
	</c:forEach>
</table>

--%>

</body>
</html>