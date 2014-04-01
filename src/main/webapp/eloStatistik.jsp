<%@ page session="false" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
de.hbt.kicker.elo.v2.service.SpielerService spielerService = (de.hbt.kicker.elo.v2.service.SpielerService) request.getAttribute("spielerService");
request.setAttribute("spieler", spielerService.findAllVisibleSpieler());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<title>ELO-Statistik</title>

<jsp:include flush="true" page="imports.jsp" />

<script>
var graph = null;
var legend = null;
var palette = new Rickshaw.Color.Palette( { scheme: 'munin' } );
var scale = d3.scale.linear().domain([0, 2500]).nice();
var graphWidth = Math.max(800, screen.availWidth - 300);
var graphHeight = Math.max(400, screen.availHeight - 400);

function addSpieler(spielerName) {
  if(graph == null) {
    
    $('#chart_div').width(graphWidth);
    $('#chart_div').height(graphHeight);
    $('#chart_loading').show();
  }
  
  $.ajax( "eloStatistik.json.jsp?SpielerName="+spielerName)
    .success(function(data, status) {
    
      var series = data;
     
      
      if(graph == null) {
        graph = new Rickshaw.Graph( {
          element: document.querySelector('#chart_div'),
          width: graphWidth,
          height: graphHeight,
          interpolation: 'linear',
          renderer: 'line',
          series: [ {
            name: spielerName,
            color: palette.color(),
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
	      element: document.querySelector('#slider')
        });
        legend = new Rickshaw.Graph.Legend({
			graph: graph,
			element: document.querySelector('#legend')
		});

        $('#chart_loading').hide();
        $('#chart_div').show();
        $('#slider').show();
        $('#legend').show();
      } else {
        graph.series.push({
          name: spielerName,
          color: palette.color(),
          data: data,
          scale: scale
        });
        legend.render();
      }
      
      graph.render();
    });
    
}

function resetGraph() {
  $('#chart_div').empty();
  $('#slider').empty();
  $('#legend').empty();
  $('#chart_div').hide();
  $('#slider').hide();
  $('#legend').hide();
  $('#spielerSelect').prop('selectedIndex',0);
  graph = null;
  legend = null;
  var palette = new Rickshaw.Color.Palette( { scheme: 'munin' } );
}
</script>

</head>

<body>
<jsp:include flush="true"  page="menu.jsp" />

<table border="0" align="center" cellspacing="0" cellpadding="0">
    <tr>
		<td align="center">		
		
		Spieler hinzufügen: 
		
		<select id="spielerSelect" size="1" tabindex="1" onchange="if(this.selectedIndex != 0) addSpieler(this.options[this.selectedIndex].value)">
		    <option value="">--- Spieler wählen ---</option>
			<c:forEach items="${spieler}" var="row" varStatus="counter">
				<option value="${row.name}">${row.name}</option>
			</c:forEach>
		</select>
		
		<input type="submit" value="Reset" onclick="resetGraph();" />
		
		<div id="chart_loading" style="color: red; display: none"><h2>Graph wird erzeugt ...</h2></div>
		<div id="chart_div" style="background-color: white; display: none"></div>
		<div id="slider" style="display: none"></div>
		
		<div id="legend" style="display: none"></div>
		
		</td>
	</tr>
</table>

</body>
</html>