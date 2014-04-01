<%@ page session="false" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String teamId = request.getParameter("TeamId");
de.hbt.kicker.elo.v2.service.TeamService teamService = (de.hbt.kicker.elo.v2.service.TeamService) request.getAttribute("teamService");
request.setAttribute("eloStatistik", teamService.getEloStatistik(teamId, "yyyy-MM-dd HH", "2008-01-01 00"));
%>
[
<c:forEach items="${eloStatistik}" var="elo" varStatus="counter"><c:if test="${!counter.first}">,</c:if>{ "x": ${elo.time}, "y": ${elo.punktzahl} }</c:forEach>
]