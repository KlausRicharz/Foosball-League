<%@ page session="false" language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Neuer Spieler</title>

<jsp:include flush="true" page="imports.jsp" />

</head>
<body>
<jsp:include flush="true" page="menu.jsp" />
<c:url value="spielerFormValidate.jsp" var="formurl" />
<form action="${formurl}" method="post">
	<table border="0" align="center">
	    <c:if test="${not empty spielerFormular.errorMessages}">
			<tr class="primarycolor error">
				<td colspan="3">
				Fehler:
				<ul>
				<c:forEach items="${spielerFormular.errorMessages}" var="errormsg">
					<li>${errormsg}</li>
				</c:forEach>
				</ul>
				</td>
			</tr>
		</c:if>
		<tr>
			<td colspan="3" align="center"><h1>Neuen Spieler anlegen</h1></td>
		</tr>
		<tr class="primarycolor">
			<td align="right"><b>Spielername</b></td>
			<td><input CLASS="txtarea" type="Text" name="name" size="20" maxlength="32"/></td>
		</tr>
		<tr>
			<td>&nbsp;</td>	
			<td align="center"><input class="button" type="submit" name="save" value="Speichern"/></td>	
		</tr>
	</table>
</form>
</body>
</html>