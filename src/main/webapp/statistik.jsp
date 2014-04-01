<%@ page session="false" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%
de.hbt.kicker.elo.v2.service.StatistikService statistikService = (de.hbt.kicker.elo.v2.service.StatistikService) request.getAttribute("statistikService");
request.setAttribute("top3Wochenstatistik", statistikService.calculateTop3Wochenstatistik());
request.setAttribute("top3Monatsstatistik", statistikService.calculateTop3Monatsstatistik());
request.setAttribute("top3Jahresstatistik", statistikService.calculateTop3Jahresstatistik());
request.setAttribute("flop3Wochenstatistik", statistikService.calculateFlop3Wochenstatistik());
request.setAttribute("flop3Monatsstatistik", statistikService.calculateFlop3Monatsstatistik());
request.setAttribute("flop3Jahresstatistik", statistikService.calculateFlop3Jahresstatistik());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<title>Statistiken</title>

<jsp:include flush="true" page="imports.jsp" />

</head>

<body>
<jsp:include flush="true"  page="menu.jsp" />

<div style="width: 100%; text-align: center">
<table style="width: 100%">
<tr class="primarycolor">
<td>
<h1>Top 3 der Woche</h1>

<h2><span class="spieler_name">${top3Wochenstatistik.spieler1.name}</span>: ${top3Wochenstatistik.punkte1} Punkte</h2>
<h2><span class="spieler_name">${top3Wochenstatistik.spieler2.name}</span>: ${top3Wochenstatistik.punkte2} Punkte</h2>
<h2><span class="spieler_name">${top3Wochenstatistik.spieler3.name}</span>: ${top3Wochenstatistik.punkte3} Punkte</h2>

</td>
<td>

<h1>Top 3 des Monats</h1>

<h2><span class="spieler_name">${top3Monatsstatistik.spieler1.name}</span>: ${top3Monatsstatistik.punkte1} Punkte</h2>
<h2><span class="spieler_name">${top3Monatsstatistik.spieler2.name}</span>: ${top3Monatsstatistik.punkte2} Punkte</h2>
<h2><span class="spieler_name">${top3Monatsstatistik.spieler3.name}</span>: ${top3Monatsstatistik.punkte3} Punkte</h2>
</td>
<td>
<h1>Top 3 des Jahres</h1>

<h2><span class="spieler_name">${top3Jahresstatistik.spieler1.name}</span>: ${top3Jahresstatistik.punkte1} Punkte</h2>
<h2><span class="spieler_name">${top3Jahresstatistik.spieler2.name}</span>: ${top3Jahresstatistik.punkte2} Punkte</h2>
<h2><span class="spieler_name">${top3Jahresstatistik.spieler3.name}</span>: ${top3Jahresstatistik.punkte3} Punkte</h2>
</td>
</tr>
<tr class="primarycolor">
<td>
<h1>Flop 3 der Woche</h1>
<h2><span class="spieler_name">${flop3Wochenstatistik.spieler1.name}</span>: ${flop3Wochenstatistik.punkte1} Punkte</h2>
<h2><span class="spieler_name">${flop3Wochenstatistik.spieler2.name}</span>: ${flop3Wochenstatistik.punkte2} Punkte</h2>
<h2><span class="spieler_name">${flop3Wochenstatistik.spieler3.name}</span>: ${flop3Wochenstatistik.punkte3} Punkte</h2>
</td>
<td>
<h1>Flop 3 des Monats</h1>
<h2><span class="spieler_name">${flop3Monatsstatistik.spieler1.name}</span>: ${flop3Monatsstatistik.punkte1} Punkte</h2>
<h2><span class="spieler_name">${flop3Monatsstatistik.spieler2.name}</span>: ${flop3Monatsstatistik.punkte2} Punkte</h2>
<h2><span class="spieler_name">${flop3Monatsstatistik.spieler3.name}</span>: ${flop3Monatsstatistik.punkte3} Punkte</h2>
</td>
<td>
<h1>Flop 3 des Jahres</h1>
<h2><span class="spieler_name">${flop3Jahresstatistik.spieler1.name}</span>: ${flop3Jahresstatistik.punkte1} Punkte</h2>
<h2><span class="spieler_name">${flop3Jahresstatistik.spieler2.name}</span>: ${flop3Jahresstatistik.punkte2} Punkte</h2>
<h2><span class="spieler_name">${flop3Jahresstatistik.spieler3.name}</span>: ${flop3Jahresstatistik.punkte3} Punkte</h2>
</td>
</tr>
<tr>
<td colspan="3" align="center">
	<h1><a href="eloStatistik.jsp"> >>> Spieler miteinander vergleichen <<< </a></h1>
</td>
</tr>
</table>
</body>
</html>