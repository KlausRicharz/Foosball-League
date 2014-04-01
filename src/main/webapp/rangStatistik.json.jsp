<%@ page session="false" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String spielerName = request.getParameter("SpielerName");
String dateFormat = request.getParameter("DateFormat");
if(dateFormat == null) {
	dateFormat = "yyyy-MM-dd HH";
}
String dateStart = request.getParameter("DateStart");
if(dateStart == null) {
	dateStart = "2008-01-01 00";
}
de.hbt.kicker.elo.v2.service.SpielerService spielerService = (de.hbt.kicker.elo.v2.service.SpielerService) request.getAttribute("spielerService");
request.setAttribute("rangStatistik", spielerService.getRangStatistik(spielerName, "yyyy-MM-dd", "2008-01-01"));
%>
[
<c:forEach items="${rangStatistik}" var="rang" varStatus="counter"><c:if test="${!counter.first}">,</c:if>{ "x": ${rang.time}, "y": ${rang.rang} }</c:forEach>
]