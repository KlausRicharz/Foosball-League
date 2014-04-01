<%@ page session="false" language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
de.hbt.kicker.elo.v2.service.SpielerService spielerService = (de.hbt.kicker.elo.v2.service.SpielerService) request.getAttribute("spielerService");
spielerService.activateSpieler(request.getParameter("spielerName"));
%>
<c:redirect url="spielerDetails.jsp">
	<c:param name="SpielerName" value="${param.spielerName}" />
</c:redirect>