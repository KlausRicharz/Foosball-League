<%@ page session="false" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%
de.hbt.kicker.elo.v2.service.SpielerService spielerService = (de.hbt.kicker.elo.v2.service.SpielerService) request.getAttribute("spielerService");
de.hbt.kicker.elo.v2.service.StatistikService statistikService = (de.hbt.kicker.elo.v2.service.StatistikService) request.getAttribute("statistikService");
request.setAttribute("rangliste", spielerService.getSpielerRangliste());
request.setAttribute("crazyStatistik", statistikService.getCrazyStats());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<title>Rangliste</title>

<jsp:include flush="true" page="imports.jsp" />

</head>

<body>
<jsp:include flush="true"  page="menu.jsp" />

<table border="0" align="center" cellspacing="0" cellpadding="3">
  	<tr>
		<td colspan="6" align="center"><h1>ELO-Werte (Spieler)</h1></td>
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
	<c:forEach items="${rangliste}" var="row" varStatus="counter">
		<c:choose>
			<c:when test="${counter.count%2==0}">
		<tr class="primarycolor"> 
			</c:when>
			<c:otherwise>
		<tr class="secondarycolor">
			</c:otherwise>
		</c:choose>
			<td align="center">${row.rang}</td>
			<td align="center"><span class="spieler_name">${row.name}</span></td>
			<td align="center">${row.punktzahl}</td>
			<td align="center">${row.spiele}</td>
			<td align="center">${row.siege}</td>
			<td align="center">${row.siegeProzent}</td>
		</tr>
	</c:forEach>
</table>

<br/>
<br/>

<table border="0" align="center" cellspacing="0" cellpadding="3">
	<tr>
		<td align="center"><h1>Funny Stats ;-)</h1></td>
	</tr>
	<c:forEach items="${crazyStatistik}" var="row" varStatus="counter">
  	<tr>
		<td align="center">
			${row.meldung}
		</td>
	</tr>
	</c:forEach>
</table>

</body>
</html>