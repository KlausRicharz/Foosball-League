<%@ page session="false" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%-- store params from form into bean --%>
<jsp:useBean id="spielFormular" class="de.hbt.kicker.web.SpielFormular" scope="request" />
<jsp:setProperty name="spielFormular" property="spielId" value="${param.spielId}" />
<jsp:setProperty name="spielFormular" property="toreTeamA1" value="${param.toreTeamA1}" />
<jsp:setProperty name="spielFormular" property="toreTeamB1" value="${param.toreTeamB1}" />
<jsp:setProperty name="spielFormular" property="toreTeamA2" value="${param.toreTeamA2}" />
<jsp:setProperty name="spielFormular" property="toreTeamB2" value="${param.toreTeamB2}" />
<jsp:setProperty name="spielFormular" property="gewinner1" value="${param.spielerId1}" />
<jsp:setProperty name="spielFormular" property="gewinner2" value="${param.spielerId2}" />
<jsp:setProperty name="spielFormular" property="verlierer1" value="${param.spielerId3}" />
<jsp:setProperty name="spielFormular" property="verlierer2" value="${param.spielerId4}" />	
<jsp:setProperty name="spielFormular" property="spielService" value="${spielService}" />	
<jsp:setProperty name="spielFormular" property="spielerService" value="${spielerService}" />	
<jsp:setProperty name="spielFormular" property="teamService" value="${teamService}" />	
<c:choose>
	<c:when test="${spielFormular.valid}">
		<c:redirect url="/showSpiele.jsp" />
	</c:when>
	<c:otherwise>
		<jsp:forward page="spielForm.jsp" />
	</c:otherwise>
</c:choose>