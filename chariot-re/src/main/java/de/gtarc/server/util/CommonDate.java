package de.gtarc.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonDate {
	public static String HTML_DATE = "yyyy-MM-dd";
	public static String HTML_DATETIME = "yyyy-MM-dd'T'HH:mm";
	public static String DATABASE_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
	public static String DATABASE_DATE = "yyyy-MM-dd";
	public static String WEARABLE_TIMESTAMP = "dd-MM-yyyy HH:mm:ss";
	private Date date;
	//private String format;

	public CommonDate(String format, String datetime) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		//this.format = format;
		this.date = formatter.parse(datetime);
	}

	public CommonDate(Date date) {
		this.date = date;
		//this.format = HTML_DATETIME;
	}

	public String format(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	public Date getDate() {
		return date;
	}

//	@Override
//	public String toString() {
//		return format(this.format);
//	}
}
