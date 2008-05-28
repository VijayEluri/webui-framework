package com.redhat.qe.auto.selenium;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A Formatter for java logging, to print nice timestamped 
 * lines to stdout/stderr.
 * @author jweiss
 *
 */
public class LogFormatter extends Formatter {

	private static final DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm:ss.SSS");
	
	
	@Override
	public String format(LogRecord record) {
		String date = sdf.format(new Date(record.getMillis()));
		String throwable = "";
		if (record.getThrown() != null) throwable = throwableToString(record.getThrown())  + "\n";
		return date + " - " + record.getLevel() + ": " + record.getMessage() + " (" + record.getSourceClassName() + "." 
		+ record.getSourceMethodName() + ")\n" + throwable;
	}

	
	private String throwableToString(Throwable t){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
}

