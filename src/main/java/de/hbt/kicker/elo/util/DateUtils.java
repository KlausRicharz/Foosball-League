package de.hbt.kicker.elo.util;

import java.util.Date;

public class DateUtils {
	
	public static Date min(Date... dates) {
		if(dates != null && dates.length > 0) {
			Date minDate = dates[0];
			for (Date date : dates) {
				if(date.before(minDate)) {
					minDate = date;
				}
			}
			return minDate;
		}
		return null;
	}

}
