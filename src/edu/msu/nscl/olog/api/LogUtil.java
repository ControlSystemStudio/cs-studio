/**
 * 
 */
package edu.msu.nscl.olog.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * @author Eric Beryman taken from shroffk
 * 
 */
public class LogUtil {

	/**
	 * This class is not meant to be instantiated or extended
	 */
	private LogUtil(){
		
	}
	/**
	 * Return a list of tag names associated with this log
	 * 
	 * @param log
	 * @return string collection of tag names
	 */
	public static Collection<String> getTagNames(Log log) {
		// Create LinkedHashSet to maintain order
		Collection<String> tagNames = new LinkedHashSet<String>();
		for (Tag tag : log.getTags()) {
			tagNames.add(tag.getName());
		}
		return tagNames;
	}

	/**
	 * Return a union of tag names associated with logs
	 * 
	 * @param logs
	 * @return string collection of tag names
	 */
	public static Collection<String> getAllTagNames(Collection<Log> logs) {
		// Create LinkedHashSet to maintain order
		Collection<String> tagNames = new LinkedHashSet<String>();
		for (Log log : logs) {
			tagNames.addAll(getTagNames(log));
		}
		return tagNames;
	}

	/**
	 * Return a list of logbook names associated with this log
	 * 
	 * @param log
	 * @return string collection of logbook names
	 */
	public static Collection<String> getLogbookNames(Log log) {
		// Create LinkedHashSet to maintain order
		Collection<String> logbookNames = new LinkedHashSet<String>();
		for (Logbook logbook : log.getLogbooks()) {
			logbookNames.add(logbook.getName());
		}
		return logbookNames;
	}

	/**
	 * Return a union of logbook names associated with logs
	 * 
	 * @param logs
	 * @return string collection of logbook names
	 */
	public static Collection<String> getLogbookNames(
			Collection<Log> logs) {
		// Create LinkedHashSet to maintain order
		Collection<String> logbookNames = new LinkedHashSet<String>();
		for (Log log : logs) {
			logbookNames.addAll(getLogbookNames(log));
		}
		return logbookNames;
	}

	/**
	 * Returns all the log ids
	 * 
	 * @param logs
	 * @return long collection of log ids
	 */
	public static Collection<Long> getLogIds(
			Collection<Log> logs) {
		// Create LinkedHashSet to maintain order
		Collection<Long> logIds = new LinkedHashSet<Long>();
		for (Log log : logs) {
			logIds.add(log.getId());
		}
		return logIds;
	}

	/**
	 * TODO evaluate need/correctness
	 * Returns a collection of objects of Type Log derived from the
	 * collection of Log.Builders <tt>logBuilders</tt>
	 * 
	 * @param logBuilders
	 * @return
	 */
	static Collection<Log> toLogs(
			Collection<LogBuilder> logBuilders) {
		// Create LinkedHashSet to maintain order
		Collection<Log> logs = new LinkedHashSet<Log>();
		for (LogBuilder builder : logBuilders) {
			logs.add(builder.build());
		}
		return Collections.unmodifiableCollection(logs);
	}
	
	static Collection<Log> toLogs(XmlLogs xmlLogs){
		// Create LinkedHashSet to maintain order
		Collection<Log> logs = new LinkedHashSet<Log>();
		for (XmlLog xmlLog : xmlLogs.getLogs()) {
			logs.add(new Log(xmlLog));
		}
		return logs;
	}
	
	public static Collection<String> getLogDescriptions(
			Collection<Log> logs) {
		Collection<String> logDescriptions = new ArrayList<String>();
		for (Log log : logs) {
			logDescriptions.add(log.getDescription());
		}
		return logDescriptions;
	}

}