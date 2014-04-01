<%@ page session="false" language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
<title>Neuen ELO-Wert berechen</title>

<jsp:include flush="true" page="imports.jsp" />

</head>
<body>

<jsp:include flush="true" page="menu.jsp" />

<c:url value="spielFormValidate.jsp" var="formurl" />
<form name="spiel" action="${formurl}" method="post">

<table border="0" align="center" cellspacing="0" cellpadding="0">
    <c:if test="${not empty spielFormular.errorMessages}">
		<tr class="primarycolor error">
			<td colspan="5">
			Fehler:
			<ul>
			<c:forEach items="${spielFormular.errorMessages}" var="errormsg">
				<li>${errormsg}</li>
			</c:forEach>
			</ul>
			</td>
		</tr>
	</c:if>
	<tr class="primarycolor">
		<td>&nbsp;</td>
		<td>
		<h3>Team Alpha</h3>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>
		<h3>Team Beta</h3>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr class="primarycolor">
		<td><b>Spieler 1</b></td>
		<td align="center"><select name="spielerId1" size="1" tabindex="1">
			<c:forEach items="${spieler}" var="row" varStatus="counter">
				<c:choose>
					<c:when test="${row.name eq param.spielerId1}">
						<option value="${row.name}" selected="selected">${row.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${row.name}">${row.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select></td>
		<td></td>
		<td align="center"><select name="spielerId3" size="1" tabindex="3">
			<c:forEach items="${spieler}" var="row" varStatus="counter">
				<c:choose>
					<c:when test="${row.name eq param.spielerId3}">
						<option value="${row.name}" selected="selected">${row.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${row.name}">${row.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select></td>
		<td><b>Spieler 1</b></td>
	</tr>
	<tr class="primarycolor">
		<td><b>Spieler 2</b></td>
		<td align="center"><select name="spielerId2" size="1" tabindex="2">
			<c:forEach items="${spieler}" var="row" varStatus="counter">
				<c:choose>
					<c:when test="${row.name eq param.spielerId2}">
						<option value="${row.name}" selected="selected">${row.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${row.name}">${row.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select></td>
		<td></td>
		<td align="center"><select name="spielerId4" size="1" tabindex="4">
			<c:forEach items="${spieler}" var="row" varStatus="counter">
				<c:choose>
					<c:when test="${row.name eq param.spielerId4}">
						<option value="${row.name}" selected="selected">${row.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${row.name}">${row.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select></td>
		<td><b>Spieler 2</b></td>
	</tr>
	<tr class="primarycolor">
		<td colspan="5">&nbsp;</td>
	</tr>
	<tr class="primarycolor">
		<td>&nbsp;</td>
		<td align="center"><b>Tore Team Alpha</b></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="center"><b>Tore Team Beta</b></td>
		<td>&nbsp;</td>
	</tr>
	<tr class="primarycolor">
		<td>&nbsp;</td>

		<c:choose>
			<c:when
				test="${empty param.toreTeamA1}">
				<td align="center"><input CLASS="txtarea" type="Text"
					name="toreTeamA1" size="2" maxlength="2" tabindex="5"></td>
			</c:when>
			<c:otherwise>
				<td align="center"><input CLASS="txtarea" type="Text"
					name="toreTeamA1" value="${param.toreTeamA1}" size="2" maxlength="2" tabindex="5">
				</td>
			</c:otherwise>
		</c:choose>

		<td>&nbsp;</td>
		
		<c:choose>
			<c:when
				test="${empty param.toreTeamB1}">
				<td align="center"><input CLASS="txtarea" type="Text"
					name="toreTeamB1" size="2" maxlength="2" tabindex="6"></td>
			</c:when>
			<c:otherwise>
				<td align="center"><input CLASS="txtarea" type="Text"
					name="toreTeamB1" value="${param.toreTeamB1}" size="2" maxlength="2" tabindex="6">
				</td>
			</c:otherwise>
		</c:choose> 
		<td>&nbsp;</td>
	</tr>
	<c:if test="${empty param.spielId}">
	<tr class="primarycolor">
		<td colspan="5" align="center"><a style="cursor: default; text-decoration: underline" onclick="$('#spiel2form').toggle(); return false">weiteres Spiel (an/aus)</a></td>
	</tr>
	<tr id="spiel2form" class="primarycolor">
		<td>&nbsp;</td>

		<c:choose>
			<c:when
				test="${empty param.toreTeamA2}">
				<td align="center"><input CLASS="txtarea" type="Text"
					name="toreTeamA2" size="2" maxlength="2" tabindex="7"></td>
			</c:when>
			<c:otherwise>
				<td align="center"><input CLASS="txtarea" type="Text"
					name="toreTeamA2" value="${param.toreTeamA2}" size="2" maxlength="2" tabindex="7">
				</td>
			</c:otherwise>
		</c:choose>

		<td>&nbsp;</td>
		
		<c:choose>
			<c:when
				test="${empty param.toreTeamB2}">
				<td align="center"><input CLASS="txtarea" type="Text"
					name="toreTeamB2" size="2" maxlength="2" tabindex="8"></td>
			</c:when>
			<c:otherwise>
				<td align="center"><input CLASS="txtarea" type="Text"
					name="toreTeamB2" value="${param.toreTeamB2}" size="2" maxlength="2" tabindex="8">
				</td>
			</c:otherwise>
		</c:choose>
		<td>&nbsp;</td>
	</tr>
	</c:if>
	<tr class="primarycolor">
		<td colspan="5" align="center"><input class="button"
			type="submit" name="ELO-Wert" value="ELO-Werte berechnen" tabindex="9"></td>
	</tr>

</table>

<input type="hidden" name="spielId" value="${param.spielId}" />

</form>

</body>
</html>