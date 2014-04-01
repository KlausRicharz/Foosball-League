<%@ page session="false" language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>

<!-- Einbindung der Versionsnummer -->
<jsp:include flush="true" page="version.jsp" />

<table border="0" align="center" >
	<tr>
	    <td>
			<input class="button" type="submit" onclick="location.href='spielForm.jsp'" name="produceNewElo" value="ELO-Spiel eintragen" />
		</td>
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		
		<td>
			<input class="button" type="submit" onclick="location.href='index.jsp'" name="showElo" value="Rangliste" />
		</td>
		<td>
			<input class="button" type="submit" onclick="location.href='teamRangliste.jsp'" value="Team-Rangliste" />
		</td>
		<td>
			<input class="button" type="submit" onclick="location.href='showSpiele.jsp'" name="showScore" value="Spielergebnisse" />
		</td>
		<td>
			<input class="button" type="submit" onclick="location.href='statistik.jsp'" name="statistik" value="Statistiken" />
		</td>
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		
		<td>			
			<input class="button" type="submit" onclick="location.href='spielerForm.jsp'" name="nueSpieler" value="Neuen Spieler anlegen">
		</td>
		<td>			
			<input class="button" type="submit" onclick="location.href='inaktiveSpieler.jsp'" name="nueSpieler" value="Inaktive Spieler">
		</td>
	</tr>
</table>