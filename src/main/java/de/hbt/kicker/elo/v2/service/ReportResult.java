package de.hbt.kicker.elo.v2.service;

import java.util.List;

public class ReportResult {

	private final List<RanglistenEintrag> rangliste;
	private final List<RanglistenEintrag> teamRangliste;
	private final List<GespieltesSpiel> spiele;

	public ReportResult(List<RanglistenEintrag> rangliste, List<RanglistenEintrag> teamRangliste, List<GespieltesSpiel> spiele) {
		this.rangliste = rangliste;
		this.teamRangliste = teamRangliste;
		this.spiele = spiele;
	}

	public List<RanglistenEintrag> getRangliste() {
		return rangliste;
	}

	public List<GespieltesSpiel> getSpiele() {
		return spiele;
	}

	public List<RanglistenEintrag> getTeamRangliste() {
		return teamRangliste;
	}

}
