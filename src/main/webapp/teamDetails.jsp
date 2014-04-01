<%@ page session="false" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String teamId = request.getParameter("teamId");
de.hbt.kicker.elo.v2.service.TeamService teamService = (de.hbt.kicker.elo.v2.service.TeamService) request.getAttribute("teamService");
request.setAttribute("team", teamService.findTeamById(teamId));
request.setAttribute("spiele", teamService.getSpiele(teamId, 100));
request.setAttribute("zusammenfassung", teamService.getStatistikZusammenfassung(teamId));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<title>Team: ${team.spieler1.name}/${team.spieler2.name}</title>

<jsp:include flush="true" page="imports.jsp" />

<script>
$( document ).ready(function() {

  var graphWidth = Math.max(800, screen.availWidth - 300);
  $('#chart1_div').width(graphWidth);

  $('#chart1_loading').show();
  $.ajax( "teamEloStatistik.json.jsp?TeamId=${team.id}")
    .success(function(data, status) {
    
      var series = data;
      var min = Number.MAX_VALUE;
      var max = Number.MIN_VALUE;
      for (i = 0, len = series.length; i < len; i++) {
        var point = series[i];
        min = Math.min(min, point.y);
        max = Math.max(max, point.y);
      }
      var scale = d3.scale.linear().domain([min, max]).nice();
    
      var graph = new Rickshaw.Graph( {
        element: document.querySelector("#chart1_div"),
        width: graphWidth,
        height: 300,
        interpolation: 'linear',
        renderer: 'area',
        series: [ {
          name: 'ELO',
          color: 'steelblue',
          data: data,
          scale: scale
        } ]
      });
      
      var axes = new Rickshaw.Graph.Axis.Time( { graph: graph } );
      var y_axis = new Rickshaw.Graph.Axis.Y.Scaled( { graph: graph, scale: scale } );
      var detail = new Rickshaw.Graph.HoverDetail({
        graph: graph,
        formatter: function(series, x, y) {
		  var date = '<span class="date">' + new Date(x * 1000).toLocaleString() + '</span>';
		  var content = series.name + ": <b>" + parseInt(y) + '</b><br/>' + date;
		  return content;
	    } 
      });
      var slider = new Rickshaw.Graph.RangeSlider({
	    graph: graph,
	    element: $('#slider1')
      });

      $('#chart1_loading').hide();
      $('#chart1_div').show();
      graph.render();
    });
});	
</script>

</head>

<body>
<jsp:include flush="true"  page="menu.jsp" />

<table border="0" align="center" cellspacing="0" cellpadding="0">
    <tr>
		<td align="center">
		<h1><span class="spieler_name">${team.spieler1.name}</span> / <span class="spieler_name">${team.spieler2.name}</span> - ELO: ${team.punktzahl.punktzahl}</h1>
		
        <div>
		ELO Min/Max: ${zusammenfassung.eloMinimum}/${zusammenfassung.eloMaximum} |
		Spiele: ${zusammenfassung.spiele} |
		Siege: ${zusammenfassung.siege} (<fmt:formatNumber value="${zusammenfassung.siegeProzent}" pattern="#.##" />%) |
		Tore +/-: ${zusammenfassung.toreGeschossen}/${zusammenfassung.toreGefangen} (${zusammenfassung.toreGeschossen - zusammenfassung.toreGefangen}) |
		TPS +/-: <fmt:formatNumber value="${zusammenfassung.toreGeschossenProSpiel}" pattern="#.##" />/<fmt:formatNumber value="${zusammenfassung.toreGefangenProSpiel}" pattern="#.##" />
		</div>
		
		<div id="chart1_loading" style="color: red; display: none"><h2>Elo-Statistik wird erzeugt ...</h2></div>
		<div id="chart1_div" style="background-color: white; height:300px; display: none"></div>
		<div id="slider1"></div>
		
		</td>
	</tr>
</table>


<table border="0" align="center" cellspacing="0" cellpadding="3">
    <tr>
		<td colspan="7" align="center"><h1><a href="#" title="Berechnungsgrundlagen anzeigen" onclick="$('.elo_details').toggle()">Die letzten 100 Spiele</a></h1></td>
	</tr>
	<tr>
		<th align="center">Datum</th>
		<th align="center">Eigenes Team</th>
		<th align="center">&nbsp;</td>
		<th align="center">Gegner</th>
		<th align="center">Ergebnis</th>
		<th align="center">Punktestand</th>
		<th align="center">ELO-Punkte</th>
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
			<c:choose>
				<c:when test="${row.gewonnen}">
					<td align="center"><fmt:formatDate value="${row.zeitpunkt}" pattern="dd.MM.yyyy"/></td>
					<td align="center"><span class="spieler_name">${row.gewinner.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.gewinner.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.teamPunktzahl.eloGewinner1})<small></td>
					<td align="center">VS</td>
					<td align="center"><span class="spieler_name">${row.verlierer.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.verlierer.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.teamPunktzahl.eloVerlierer1})<small></td>
					<td align="center" style="color: green">gewonnen</td>
					<td align="center">${row.toreGewinner} : ${row.toreVerlierer}</td>
					<td align="center" style="color: green">+ ${row.teamPunktzahl.punktzahl}<br/><small class="elo_details" style="color: black">(${row.teamCalculationDetails})</small></td>
				</c:when>
				<c:otherwise>
					<td align="center"><fmt:formatDate value="${row.zeitpunkt}" pattern="dd.MM.yyyy"/></td>
					<td align="center"><span class="spieler_name">${row.verlierer.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.verlierer.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.teamPunktzahl.eloVerlierer1})<small></td>
					<td align="center">VS</td>
					<td align="center"><span class="spieler_name">${row.gewinner.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.gewinner.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.teamPunktzahl.eloGewinner1})<small></td>
					<td align="center" style="color: red">verloren</td>
					<td align="center">${row.toreVerlierer} : ${row.toreGewinner}</td>
					<td align="center" style="color: red">- ${row.teamPunktzahl.punktzahl}<br/><small class="elo_details" style="color: black">(${row.teamCalculationDetails})</small></td>
				</c:otherwise>
			</c:choose>
				
			</tr>
	</c:forEach>
	
</table>

</body>
</html>