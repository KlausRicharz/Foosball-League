<%@ page session="false" language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<script type="text/javascript">

	function showTt() {
  		document.getElementById("version").style.display = "block";
	}

	function hideTt() {
		document.getElementById("version").style.display = "none";
	}
</script>

<table border="0" align="center" width="100%">
	<tr>
		<td align="right">
			<font size="1">
				<a onmouseover="JavaScript:showTt()" onMouseOut="JavaScript:hideTt()">
					Version 2.0.7.0 copyright Tim Zielasko &amp; Klaus Richarz
				</a>
			</font>
			<table class="tooltip" id="version">
				<tr>
					<td align="center">
						<u>Versions-Historie</u><p>
						<table style="font-family:Courier New;	font-size: 9pt;">
							<tr>
								<td valign="top"><font size=1>2.0.7.0 &nbsp;</font></td>
								<td>
								<font size=1>
									-Spieler-Details: Statistiken können nun ein- und ausgeblendet werden.<br/>
									-Neue Statistiken unterhalb der Rangliste (experimental).
								</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>2.0.6.0 &nbsp;</font></td>
								<td>
								<font size=1>
									-Spieler-Details um Lieblings- und Angstgegner erweitert
								</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>2.0.5.0 &nbsp;</font></td>
								<td>
								<font size=1>
									-Report1 hat jetzt auch Team-ELO-Werte<br/>
									-ELO-Statistik korrigiert (Ende-Datum war zu früh)
								</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>2.0.4.0 &nbsp;</font></td>
								<td>
								<font size=1>
									-Team-ELO wird nun getrennt berechnet<br/>
									-Neu: Siege % in den Ranglisten
								</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>2.0.3.1 &nbsp;</font></td>
								<td>
								<font size=1>
									-kleine Fehlerkorrekturen in der ELO-Berechnung (nach Bearbeitung)<br/>
								</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>2.0.3.0 &nbsp;</font></td>
								<td>
								<font size=1>
									-Formeldetails für ELO-Berechnung in alle Bereiche eingebunden (ELOs, Erwartungswert, Faktor) - Teamdetails, Spielergebnisse<br/>
									-Spielergebnisse: Administration abgesichert<br/>
									-Spielerdetails: Top 10 Mitspieler<br/>
									-Spielerdetails: TOP/FLOP aktivieren/deaktivieren<br/>
									-kleine Fehlerkorrekturen<br/>
									-Punkteberechnung verändert (Faktor immer 30, ab 2000 20)<br/>
									-Report eingeführt, z.B. <a href="report1.jsp?Start=2013-01-01&SpielerName=Klaus&SpielerName=Timm&SpielerName=Tim&SpielerName=ThomasD&SpielerName=Ilja&SpielerName=Stefan&SpielerName=Jannick&SpielerName=Claas">Nur Ligaspieler 2013</a>
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>2.0.2.0 &nbsp;</font></td>
								<td>
								<font size=1>
									-Statistik verbessert (Spieler-Vergleich)<br/>
									-Flops entfernt<br/>
									-Spieler stärker verlinkt
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>2.0.1.1 &nbsp;</font></td>
								<td>
								<font size=1>
									-Rickshaw Graphen<br/>
									-Aktivierung von Spielern korrigiert<br/>
									-Zwei Spiele können nun auf einmal eingetragen werden
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>2.0.1.0 &nbsp;</font></td>
								<td>
								<font size=1>
									-Performance verbssert<br/>
									-Inaktive Spieler wieder hinzugenommen<br/>
									-Rang-Statistik wieder hinzugenommen
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>2.0.0.2 &nbsp;</font></td>
								<td>
								<font size=1>
									-Neue Funktion "Spiel löschen"<br/>
									-Inaktive Spieler herausgenommen (Performance)<br/>
									-Rang-Statistik herausgenommen (Performance)
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>2.0.0.1 &nbsp;</font></td>
								<td>
								<font size=1>
									-URL-Bug bei Integration in externer Seite behoben.
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>2.0.0.0 &nbsp;</font></td>
								<td>
								<font size=1>
									-komplett überarbeitet<br>
									-Korrektur Elo-Berechnung<br>
									-Migration der Daten
									</font>
								</td>
							</tr>
							<%--
							<tr>
								<td valign="top"><font size=1>1.0.5.2 &nbsp;</font></td>
								<td>
								<font size=1>
									-Favicon erstellt<br>
									-Add SPieler überarbeitet<br>
									-Bugfixes im Code<br>
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>1.0.5.1 &nbsp;</font></td>
								<td>
									<font size=1>
									-Statistik hinzugefügt<br>
									-Bugfixes im Graphen<br>
									-Spielübersicht: Einschränkungs<br>
									&nbsp;möglichkeit ergänzung<br>
									-Sortierfunktion in der Rangliste<br>
									&nbsp;Grafik geändert<br>
									-Bugfixes im Code<br>
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>1.0.4.0 &nbsp;</font></td>
								<td>
									<font size=1>
									-Datenbank erweitert<br>
									-Den Graphen überarbeitet<br>
									-Wiederherstellungsfunktion<br>
									&nbsp;eines Spielers<br>
									-Bugfixes im Code<br>
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>1.0.3.0 &nbsp;</font></td>
								<td>
									<font size=1>
										-Graphen (Graphen von den ELO-Werte)<br>
										-Link probleme behoben in der <br>
										&nbsp;Bearbeitungs- und Löschfunktion <br>
										-Überarbeitung des Menus <br>
										-Login für den Admin <br>
										-Was in der Datenbank geändert<br>
										-Bugfixes im Code<br>
									</font>
								</td>
							</tr>
							
							<tr>
								<td valign="top"><font size=1>1.0.2.0 &nbsp;</font></td>
								<td>
									<font size=1>
										-Spieler-Filter in der <br>
										&nbsp;Spieleübersicht<br>
										-Bearbeitungs- und Löschfunktion <br>
										&nbsp;für Spiele<br>
										-ELO-Neuberechnungsfunktion <br>
										&nbsp;(nur Admin) <br>
										-Menüstruktur überarbeitet<br>
										-Bugfixes im Code<br>
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>1.0.1.1 &nbsp;</font></td>
								<td>
									<font size=1>
										-Bugfixes in der Sortierfunktion<br>
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>1.0.1.0 &nbsp;</font></td>
								<td>
									<font size=1>
										-Sortierfunktion in der Rangliste<br>
										&nbsp;hinzu gekommen
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>1.0.0.1 &nbsp;</font></td>
								<td>
									<font size=1>
										-Bugfixes
									</font>
								</td>
							</tr>
							<tr>
								<td valign="top"><font size=1>1.0.0.0 &nbsp;</font></td>
								<td>
									<font size=1>
										-Initial-Release
									</font>
								</td>
							</tr>
							
							--%>
							
						</table>
					</td>
				</tr>
			</table>	
		</td>
	</tr>
</table>