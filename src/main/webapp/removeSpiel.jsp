<%@ page session="false" language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
de.hbt.kicker.elo.v2.service.SpielService spielService = (de.hbt.kicker.elo.v2.service.SpielService) request.getAttribute("spielService");
spielService.removeSpiel(request.getParameter("spielId"));
%>
<c:redirect url="/showSpiele.jsp"/>