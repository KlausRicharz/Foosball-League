<%@ page session="false" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String spielerName = request.getParameter("SpielerName");
de.hbt.kicker.elo.v2.service.SpielerService spielerService = (de.hbt.kicker.elo.v2.service.SpielerService) request.getAttribute("spielerService");
request.setAttribute("spieler", spielerService.findSpielerByName(spielerName));
request.setAttribute("spiele", spielerService.getSpiele(spielerName, 100));
request.setAttribute("zusammenfassung", spielerService.getStatistikZusammenfassung(spielerName));
request.setAttribute("removable", spielerService.isSpielerRemoveable(spielerName));
de.hbt.kicker.elo.v2.service.TeamService teamService = (de.hbt.kicker.elo.v2.service.TeamService) request.getAttribute("teamService");
request.setAttribute("teams", teamService.getTopNTeams(spielerName, 10));
request.setAttribute("lieblingsmitspieler", spielerService.getStatistikLieblingsmitspieler(spielerName, 10));
request.setAttribute("angstmitspieler", spielerService.getStatistikAngstmitspieler(spielerName, 10));
request.setAttribute("lieblingsgegner", spielerService.getStatistikLieblingsgegner(spielerName, 10));
request.setAttribute("angstgegner", spielerService.getStatistikAngstgegner(spielerName, 10));

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<title>Spieler: ${spieler.name}</title>

<jsp:include flush="true" page="imports.jsp" />

<script>
$( document ).ready(function() {

  var graphWidth = Math.max(800, screen.availWidth - 300);
  
  $('#chart_div').width(graphWidth);

  $('#chart_loading').show();
  
  var eloseries = null;
  
  $.ajax( "eloStatistik.json.jsp?SpielerName=${spieler.name}")
    .success(function(data, status) {
    
      eloseries = data;
      
      /*
      $.ajax( "rangStatistik.json.jsp?SpielerName=${spieler.name}")
        .success(function(data, status) {
        
          var rangseries = data;
          */
        
          var min = Number.MAX_VALUE;
          var max = Number.MIN_VALUE;
          for (i = 0, len = eloseries.length; i < len; i++) {
            var point = eloseries[i];
            min = Math.min(min, point.y);
            max = Math.max(max, point.y);
          }
          var eloscale = d3.scale.linear().domain([min, max]).nice();
          
          /*
          var min = Number.MAX_VALUE;
          var max = Number.MIN_VALUE;
          for (i = 0, len = rangseries.length; i < len; i++) {
            var point = rangseries[i];
            min = Math.min(min, point.y);
            max = Math.max(max, point.y);
          }
          var rangscale = d3.scale.linear().domain([max, min]).nice();
          */
    
          var graph = new Rickshaw.Graph( {
            element: document.querySelector("#chart_div"),
            width: graphWidth,
            height: 300,
            interpolation: 'linear',
            renderer: 'area',
            series: [ {
              name: 'ELO',
              color: 'steelblue',
              data: eloseries,
              scale: eloscale
            }
            /*, {
              name: 'Rang',
              color: 'green',
              data: rangseries,
              scale: rangscale
            }
            */
             ]
          });

          var axes = new Rickshaw.Graph.Axis.Time( { graph: graph } );
          
          var y_axis = new Rickshaw.Graph.Axis.Y.Scaled( { 
            graph: graph,
            scale: eloscale
          } );
          
          var detail = new Rickshaw.Graph.HoverDetail({
            graph: graph,
            formatter: function(series, x, y) {
		      var date = '<span class="date">' + new Date(x * 1000).toLocaleString() + '</span>';
		      var content = series.name + ": <b>" + parseInt(y) + '</b><br/>' + date;
		      return content;
	        } 
          });
          
          /*
          var legend = new Rickshaw.Graph.Legend( {
	        graph: graph,
	        element: document.getElementById('legend')
          } );
          */
          
          var slider = new Rickshaw.Graph.RangeSlider({
	        graph: graph,
	        element: $('#slider')
          });

          $('#chart_loading').hide();
          $('#chart_div').show();
          graph.render();
          
          /*
      });
      */
  });
});	
</script>

</head>

<body>
<jsp:include flush="true"  page="menu.jsp" />

<table border="0" align="center" cellspacing="0" cellpadding="0">
    <tr>
		<td align="center">
		<h1>${spieler.name} - ELO: ${spieler.punktzahl.punktzahl}</h1>
<c:url value="activateSpieler.jsp" var="activateurl">
	<c:param name="spielerName" value="${spieler.name}" />
</c:url>
<c:url value="deactivateSpieler.jsp" var="deactivateurl">
	<c:param name="spielerName" value="${spieler.name}" />
</c:url>
<c:url value="toggleTopFlop.jsp" var="topfloptoggleurl">
	<c:param name="spielerName" value="${spieler.name}" />
</c:url>
<c:url value="removeSpieler.jsp" var="removeurl">
	<c:param name="spielerName" value="${spieler.name}" />
</c:url>
<div>
<c:if test="${!spieler.visible}"><a href="${activateurl}">aktivieren</a></c:if>
<c:if test="${spieler.visible}"><a href="${deactivateurl}">deaktivieren</a></c:if>
<c:if test="${!spieler.topFlopAktiviert}"><a href="${topfloptoggleurl}">TOP/FLOP aktivieren</a></c:if>
<c:if test="${spieler.topFlopAktiviert}"><a href="${topfloptoggleurl}">TOP/FLOP deaktivieren</a></c:if>
<c:if test="${removable}"><a href="${removeurl}">löschen</a></c:if>
</div>
        <div>
		ELO Min/Max: ${zusammenfassung.eloMinimum}/${zusammenfassung.eloMaximum} |
		Spiele: ${zusammenfassung.spiele} |
		Siege: ${zusammenfassung.siege} (<fmt:formatNumber value="${zusammenfassung.siegeProzent}" pattern="#.##" />%) |
		Tore +/-: ${zusammenfassung.toreGeschossen}/${zusammenfassung.toreGefangen} (${zusammenfassung.toreGeschossen - zusammenfassung.toreGefangen}) |
		TPS +/-: <fmt:formatNumber value="${zusammenfassung.toreGeschossenProSpiel}" pattern="#.##" />/<fmt:formatNumber value="${zusammenfassung.toreGefangenProSpiel}" pattern="#.##" />
		</div>
		
		<div id="chart_loading" style="color: red; display: none"><h2>Graph wird erzeugt ...</h2></div>
		<div id="chart_div" style="background-color: white; height:300px; display: none"></div>
		<div id="slider"></div>
		</td>
	</tr>
	<tr>
		<td align="center">
			<h2><a href="#" onclick="$('.elo_stats').toggle(); return false">Statistiken (an/aus)</a></h2>
		</td>
	</tr>
</table>

<div class="elo_stats" style="display: none">
<c:if test="${ not empty teams}">
<table border="0" align="center" cellspacing="0" cellpadding="3">
  	<tr>
		<td colspan="6" align="center"><h1>TOP 10 Teams</h1></td>
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
	<c:forEach items="${teams}" var="row" varStatus="counter">
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
</c:if>

<table align="center" width="90%">
<tr>
<td align="center" valign="top">
<c:if test="${ not empty lieblingsgegner}">
<table border="0" align="center" cellspacing="0" cellpadding="3">
  	<tr>
		<td colspan="6" align="center"><h1>Lieblingsmitspieler</h1></td>
	</tr>
	<tr>
		<th>
			Rang
		</th>
		<th>
			Name 
		</th>
		<th>
			ELO-Punkte
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
	<c:forEach items="${lieblingsmitspieler}" var="row" varStatus="counter">
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
</c:if>
</td>
<td align="center" valign="top">
<c:if test="${ not empty angstgegner}">
<table border="0" align="center" cellspacing="0" cellpadding="3">
  	<tr>
		<td colspan="6" align="center"><h1>Angstmitspieler</h1></td>
	</tr>
	<tr>
		<th>
			Rang
		</th>
		<th>
			Name 
		</th>
		<th>
			ELO-Punkte
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
	<c:forEach items="${angstmitspieler}" var="row" varStatus="counter">
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
</c:if>
</td>
</tr>
<tr>
<td align="center" valign="top">
<c:if test="${ not empty lieblingsgegner}">
<table border="0" align="center" cellspacing="0" cellpadding="3">
  	<tr>
		<td colspan="6" align="center"><h1>Lieblingsgegner</h1></td>
	</tr>
	<tr>
		<th>
			Rang
		</th>
		<th>
			Name 
		</th>
		<th>
			ELO-Punkte
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
	<c:forEach items="${lieblingsgegner}" var="row" varStatus="counter">
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
</c:if>
</td>
<td align="center" valign="top">
<c:if test="${ not empty angstgegner}">
<table border="0" align="center" cellspacing="0" cellpadding="3">
  	<tr>
		<td colspan="6" align="center"><h1>Angstgegner</h1></td>
	</tr>
	<tr>
		<th>
			Rang
		</th>
		<th>
			Name 
		</th>
		<th>
			ELO-Punkte
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
	<c:forEach items="${angstgegner}" var="row" varStatus="counter">
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
</c:if>
</td>
</tr>
</table>
</div>

<table border="0" align="center" cellspacing="0" cellpadding="3">
    <tr>
		<td colspan="7" align="center"><h1><a href="#" title="Berechnungsgrundlagen anzeigen" onclick="$('.elo_details').toggle(); return false">Die letzten 100 Spiele</a></h1></td>
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
					<td align="center"><span class="spieler_name">${row.gewinner.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.gewinner.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.punktzahl.eloGewinner1} / ${row.punktzahl.eloGewinner2})<small></td>
					<td align="center">VS</td>
					<td align="center"><span class="spieler_name">${row.verlierer.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.verlierer.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.punktzahl.eloVerlierer1} / ${row.punktzahl.eloVerlierer2})<small></td>
					<td align="center" style="color: green">gewonnen</td>
					<td align="center">${row.toreGewinner} : ${row.toreVerlierer}</td>
					<td align="center" style="color: green">+ ${row.punktzahl.punktzahl}<br/><small class="elo_details" style="color: black">(${row.calculationDetails})</small></td>
				</c:when>
				<c:otherwise>
					<td align="center"><fmt:formatDate value="${row.zeitpunkt}" pattern="dd.MM.yyyy"/></td>
					<td align="center"><span class="spieler_name">${row.verlierer.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.verlierer.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.punktzahl.eloVerlierer1} / ${row.punktzahl.eloVerlierer2})<small></td>
					<td align="center">VS</td>
					<td align="center"><span class="spieler_name">${row.gewinner.spieler1.name}</span>&nbsp;&amp;&nbsp;<span class="spieler_name">${row.gewinner.spieler2.name}</span><br /><small class="elo_details" style="color: black">(${row.punktzahl.eloGewinner1} / ${row.punktzahl.eloGewinner2})<small></td>
					<td align="center" style="color: red">verloren</td>
					<td align="center">${row.toreVerlierer} : ${row.toreGewinner}</td>
					<td align="center" style="color: red">- ${row.punktzahl.punktzahl}<br/><small class="elo_details" style="color: black">(${row.calculationDetails})<small></td>
				</c:otherwise>
			</c:choose>
				
			</tr>
	</c:forEach>
	
</table>

</body>
</html>