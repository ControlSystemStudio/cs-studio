/**
 * 
 */
package org.csstudio.logbook.util;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.Tag;

/**
 * Utility Class for common operations on an LogEntry/LogEntries
 * 
 * @author shroffk
 * 
 */
public class LogEntryUtil {

	/**
	 * This class is not intended to be instantiated or subclassed.
	 */
	private LogEntryUtil() {

	}

	/**
	 * Return a list of names of all the tags added to this logEntry
	 * 
	 * @param logEntry
	 * @return
	 */
	public static List<String> getTagNames(LogEntry logEntry) {
		List<String> tagNames = new ArrayList<String>();
		for (Tag tag : logEntry.getTags()) {
			tagNames.add(tag.getName());
		}
		return tagNames;
	}

	/**
	 * Return a list of names of all the logbook added to this logEntry
	 * 
	 * @param logEntry
	 * @return
	 */
	public static List<String> getLogbookNames(LogEntry logEntry) {
		List<String> logbookNames = new ArrayList<String>();
		for (Logbook logbook : logEntry.getLogbooks()) {
			logbookNames.add(logbook.getName());
		}
		return logbookNames;
	}

	/**
	 * Return a list of names of all the properties added to this logEntry
	 * 
	 * @param logEntry
	 * @return
	 */
	public static List<String> getPropertyNames(LogEntry logEntry) {
		List<String> propertyNames = new ArrayList<String>();
		for (Property property : logEntry.getProperties()) {
			propertyNames.add(property.getName());
		}
		return propertyNames;
	}
	
	public static Property getProperty(LogEntry logEntry, String PropertyName){
		for (Property property : logEntry.getProperties()) {
			if(property.getName().equalsIgnoreCase(PropertyName)){
				return property;
			}
		}
		return null;
	}

}
