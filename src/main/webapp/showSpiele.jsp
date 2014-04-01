<%@ page session="false" language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%
de.hbt.kicker.elo.v2.service.SpielService spielService = (de.hbt.kicker.elo.v2.service.SpielService) request.getAttribute("spielService");
request.setAttribute("spiele", spielService.findAllLetzteSpiele(100));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Spielergebnisse</title>

<jsp:include flush="true" page="imports.jsp" />

</head>
<body>

<jsp:include flush="true" page="menu.jsp" />

<table border="0" align="center" cellspacing="0" cellpadding="3">
    <tr>
		<td colspan="7" align="center"><h1><a href="#" title="Berechnungsgrundlagen anzeigen" onclick="$('.elo_details').toggle()">Die letzten 100 Spiele</a></h1><a href="#" onclick="$('.admin_action').toggle(); return false;">administrieren</a></td>
	</tr>
	<tr>
		<th align="center">Datum</th>
		<th align="center">Sieger</th>
		<th align="center">&nbsp;</td>
		<th align="center">Verlierer</th>
		<th align="center">Ergebnis</th>
		<th align="center">ELO-Punkte</th>
		<th align="center">Team-ELO</th>
		<th class="admin_action">&nbsp;</th> 
	</tr>
	<c:forEach items="${spiele}" var="row" varStatus="counter">
			<c:choose>
				<c:when test="${counter.count%2==0}">
					<tr class="primarycolor">
				</c:when>
				<c:otherwise>
					<tr class="secondarycolor">	
				</c:otherwise>
			</c:choose>	
				<td align="center"><fmt:formatDate value="${row.zeitpunkt}" pattern="dd.MM.yyyy"/></td>
				<td align="center"><span class="spieler_name">${row.gewinner.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.gewinner.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.punktzahl.eloGewinner1} / ${row.punktzahl.eloGewinner2})<br />(${row.teamPunktzahl.eloGewinner1})<small></td>
				<td align="center">VS</td>
				<td align="center"><span class="spieler_name">${row.verlierer.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.verlierer.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.punktzahl.eloVerlierer1} / ${row.punktzahl.eloVerlierer2})<br />(${row.teamPunktzahl.eloVerlierer1})<small></td>
				<td align="center">${row.toreGewinner} : ${row.toreVerlierer}</td>
				<td align="center">${row.punktzahl.punktzahl}<br/><small class="elo_details" style="color: black">(${row.calculationDetails})</small></td>
				<td align="center">${row.teamPunktzahl.punktzahl}<br/><small class="elo_details" style="color: black">(${row.teamCalculationDetails})</small></td>
				<c:url value="spielForm.jsp" var="spielediturl">
					<c:param name="spielerId1" value="${row.gewinner.spieler1.name}" />
					<c:param name="spielerId2" value="${row.gewinner.spieler2.name}" />
					<c:param name="spielerId3" value="${row.verlierer.spieler1.name}" />
					<c:param name="spielerId4" value="${row.verlierer.spieler2.name}" />
					<c:param name="toreTeamA1" value="${row.toreGewinner}" />
					<c:param name="toreTeamB1" value="${row.toreVerlierer}" />
					<c:param name="spielId" value="${row.id}" />
				</c:url>
				<c:url value="removeSpiel.jsp" var="spielremoveurl">
					<c:param name="spielId" value="${row.id}" />
				</c:url>
				<td class="admin_action"><a href="${spielediturl}"><img src="images/Edit.gif" /></a> | <a href="${spielremoveurl}" onclick="return window.confirm('Spiel wirklich löschen?')"><img src="images/Delete.gif" /></a></td>
			</tr>
	</c:forEach>

</table>
</body>
</html>