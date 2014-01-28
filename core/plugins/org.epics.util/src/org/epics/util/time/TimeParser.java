/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.util.time;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class to parse user defined time strings to absolute or relative
 * time durations
 * 
 * The following return a TimeInterval - absolute
 * 
 * "last min", "last hour", "last day", "last week"
 * 
 * "last 5 mins", "last 5 hours", "last 5 days", "last 5 weeks"
 * 
 * "5 mins ago", "5 hours ago", "5 days ago", "5 weeks ago"
 * 
 * The following returns a Timestamp "now"
 * 
 * The following returns a TimeDuration - relative
 * 
 * "last min", "last hour", "last day", "last week"
 * 
 * "last 5 mins", "last 5 hours", "last 5 days", "last 5 weeks"
 * 
 * "5 mins ago", "5 hours ago", "5 days ago", "5 weeks ago"
 * 
 * 
 * @author shroffk
 */
public class TimeParser {

    public static TimeDuration getTimeDuration(String time) {
	// TODO this regular expression needs to be reviewed and improved if
	// possible
	int quantity = 0;
	String unit = "";

	Pattern lastNUnitsPattern = Pattern
		.compile(
			"last\\s*(\\d*)\\s*(min|mins|hour|hours|day|days|week|weeks).*",
			Pattern.CASE_INSENSITIVE);
	Matcher lastNUnitsMatcher = lastNUnitsPattern.matcher(time);
	while (lastNUnitsMatcher.find()) {
	    quantity = "".equals(lastNUnitsMatcher.group(1)) ? 1 : Integer
		    .valueOf(lastNUnitsMatcher.group(1));
	    unit = lastNUnitsMatcher.group(2);
	}

	Pattern nUnitsAgoPattern = Pattern.compile(
		"(\\d*)\\s*(min|mins|hour|hours|day|days|week|weeks)\\s*ago",
		Pattern.CASE_INSENSITIVE);
	Matcher nUnitsAgoMatcher = nUnitsAgoPattern.matcher(time);
	while (nUnitsAgoMatcher.find()) {
	    quantity = "".equals(nUnitsAgoMatcher.group(1)) ? 1 : Integer
		    .valueOf(nUnitsAgoMatcher.group(1));
	    unit = nUnitsAgoMatcher.group(2);
	}
	unit = unit.toLowerCase();
	switch (unit) {
	case "min":
	case "mins":
	    return TimeDuration.ofMinutes(quantity);
	case "hour":
	case "hours":
	    return TimeDuration.ofHours(quantity);
	case "day":
	case "days":
	    return TimeDuration.ofHours(quantity * 24);
	case "week":
	case "weeks":
	    return TimeDuration.ofHours(quantity * 24 * 7);
	default:
	    break;
	}
	return null;
    }

    public static TimeInterval getTimeInterval(String time) {
	return getTimeInterval(time, "now");
    }

    public static TimeInterval getTimeInterval(String start, String end) {
	return TimeInterval.between(getTimeStamp(start), getTimeStamp(end));
    }

    /**
     * A Helper function to help you convert various string represented time
     * definition to an absolute Timestamp.
     * 
     * i.e.
     * 
     * @param time
     * @return
     */
    public static Timestamp getTimeStamp(String time) {
	if (time.equalsIgnoreCase("now")) {
	    return Timestamp.now();
	} else {
	    int quantity = 0;
	    String unit = "";
	    Pattern lastNUnitsPattern = Pattern
		    .compile(
			    "last\\s*(\\d*)\\s*(min|mins|hour|hours|day|days|week|weeks).*",
			    Pattern.CASE_INSENSITIVE);
	    Matcher lastNUnitsMatcher = lastNUnitsPattern.matcher(time);
	    while (lastNUnitsMatcher.find()) {
		quantity = "".equals(lastNUnitsMatcher.group(1)) ? 1 : Integer
			.valueOf(lastNUnitsMatcher.group(1));
		unit = lastNUnitsMatcher.group(2).toLowerCase();
		switch (unit) {
		case "min":
		case "mins":
		    return Timestamp.now().minus(
			    TimeDuration.ofMinutes(quantity));
		case "hour":
		case "hours":
		    return Timestamp.now()
			    .minus(TimeDuration.ofHours(quantity));
		case "day":
		case "days":
		    return Timestamp.now().minus(
			    TimeDuration.ofHours(quantity * 24));
		case "week":
		case "weeks":
		    return Timestamp.now().minus(
			    TimeDuration.ofHours(quantity * 24 * 7));
		default:
		    break;
		}
	    }

	    Pattern nUnitsAgoPattern = Pattern
		    .compile(
			    "(\\d*)\\s*(min|mins|hour|hours|day|days|week|weeks)\\s*ago",
			    Pattern.CASE_INSENSITIVE);
	    Matcher nUnitsAgoMatcher = nUnitsAgoPattern.matcher(time);
	    while (nUnitsAgoMatcher.find()) {
		quantity = "".equals(nUnitsAgoMatcher.group(1)) ? 1 : Integer
			.valueOf(nUnitsAgoMatcher.group(1));
		unit = nUnitsAgoMatcher.group(2).toLowerCase();
		switch (unit) {
		case "min":
		case "mins":
		    return Timestamp.now().minus(
			    TimeDuration.ofMinutes(quantity));

		case "hour":
		case "hours":
		    return Timestamp.now()
			    .minus(TimeDuration.ofHours(quantity));
		case "day":
		case "days":
		    return Timestamp.now().minus(
			    TimeDuration.ofHours(quantity * 24));
		case "week":
		case "weeks":
		    return Timestamp.now().minus(
			    TimeDuration.ofHours(quantity * 24 * 7));
		default:
		    break;
		}
	    }
	    TimestampFormat format = new TimestampFormat(
		    "yyyy-MM-dd'T'HH:mm:ss");
	    try {
		return format.parse(time);
	    } catch (ParseException e) {
		return null;
	    }
	}
    }
}
