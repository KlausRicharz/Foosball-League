<%@ page session="false" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%
de.hbt.kicker.elo.v2.service.ReportService reportService = (de.hbt.kicker.elo.v2.service.ReportService) request.getAttribute("reportService");
request.setAttribute("reportResult", reportService.getFilteredReport(request.getParameterValues("SpielerName"), request.getParameter("Start"), request.getParameter("Ende")));
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
	<c:forEach items="${reportResult.rangliste}" var="row" varStatus="counter">
		<c:choose>
			<c:when test="${counter.count%2==0}">
		<tr class="primarycolor"> 
			</c:when>
			<c:otherwise>
		<tr class="secondarycolor">
			</c:otherwise>
		</c:choose>
			<td align="center">${row.rang}</td>
			<td align="center"><span class="spieler_name">${row.name}</a></td>
			<td align="center">${row.punktzahl}</td>
			<td align="center">${row.spiele}</td>
			<td align="center">${row.siege}</td>
			<td align="center">${row.siegeProzent}</td>
		</tr>
	</c:forEach>
</table>

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
	<c:forEach items="${reportResult.teamRangliste}" var="row" varStatus="counter">
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

<table border="0" align="center" cellspacing="0" cellpadding="3">
    <tr>
		<td colspan="7" align="center"><h1 onclick="$('.elo_details').toggle()" style="cursor: default">Spiele</h1></td>
	</tr>
	<tr>
		<th align="center">Datum</th>
		<th align="center">Sieger</th>
		<th align="center">&nbsp;</td>
		<th align="center">Verlierer</th>
		<th align="center">Ergebnis</th>
		<th align="center">ELO-Punkte</th>
		<th align="center">Team-ELO</th>
	</tr>
	<c:forEach items="${reportResult.spiele}" var="row" varStatus="counter">
			<c:choose>
				<c:when test="${counter.count%2==0}">
					<tr class="primarycolor">
				</c:when>
				<c:otherwise>
					<tr class="secondarycolor">	
				</c:otherwise>
			</c:choose>	
				<td align="center"><fmt:formatDate value="${row.zeitpunkt}" pattern="dd.MM.yyyy"/></td>
				<td align="center"><span class="spieler_name">${row.gewinner.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.gewinner.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.punktzahl.eloGewinner1} / ${row.punktzahl.eloGewinner2})<small></td>
				<td align="center">VS</td>
				<td align="center"><span class="spieler_name">${row.verlierer.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.verlierer.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.punktzahl.eloVerlierer1} / ${row.punktzahl.eloVerlierer2})<small></td>
				<td align="center">${row.toreGewinner} : ${row.toreVerlierer}</td>
				<td align="center">${row.punktzahl.zuwachs}<br/><small class="elo_details" style="color: black">(${row.calculationDetails})</small></td>
				<td align="center">${row.teamPunktzahl.zuwachs}<br/><small class="elo_details" style="color: black">(${row.teamCalculationDetails})</small></td>
			</tr>
	</c:forEach>

</table>

</body>
</html>