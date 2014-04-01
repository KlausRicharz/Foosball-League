package de.hbt.kicker.elo.util;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
	
	private static final NumberFormat DOUBLE_FORMATTER = NumberFormat.getNumberInstance(Locale.GERMAN);
	private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance(Locale.GERMAN);
	private static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);
	static {
		DOUBLE_FORMATTER.setMaximumFractionDigits(3);
		DOUBLE_FORMATTER.setGroupingUsed(false);
		DOUBLE_FORMATTER.setRoundingMode(RoundingMode.HALF_UP);
		PERCENT_FORMATTER.setMaximumFractionDigits(1);
		PERCENT_FORMATTER.setGroupingUsed(false);
		PERCENT_FORMATTER.setRoundingMode(RoundingMode.HALF_UP);
	}
	
	public static synchronized String formatDouble(double value) {
		return DOUBLE_FORMATTER.format(value);
	}
	
	public static synchronized String formatPercent(double value) {
		return PERCENT_FORMATTER.format(value);
	}
	
	public static synchronized String formatDatetime(Date value) {
		return DATETIME_FORMATTER.format(value);
		
	}
	
	public static synchronized boolean isDatetimeParsable(String value) {
		try {
			return DATETIME_FORMATTER.parse(value) != null;
		} catch (ParseException e) {
			return false;
		}
	}
	
	public static synchronized Date parseDatetime(String value) {
		try {
			return DATETIME_FORMATTER.parse(value);
		} catch (ParseException e) {
			throw new RuntimeException("Cannot parse " + value);
		}
	}

}
