<%@ page session="false" language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%-- store params from form into bean --%>
<jsp:useBean id="spielerFormular"  class="de.hbt.kicker.web.SpielerFormular" scope="request" />
<jsp:setProperty name="spielerFormular" property="name" value="${param.name}" />
<jsp:setProperty name="spielerFormular" property="spielerService" value="${spielerService}" />
<c:choose>
	<c:when test="${spielerFormular.valid}">
		<c:redirect url="/index.jsp" />
	</c:when>
	<c:otherwise>
		<jsp:forward page="spielerForm.jsp" />
	</c:otherwise>
</c:choose>