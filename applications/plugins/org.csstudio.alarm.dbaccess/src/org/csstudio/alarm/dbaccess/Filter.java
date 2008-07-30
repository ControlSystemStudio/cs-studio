package org.csstudio.alarm.dbaccess;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Filter for JMSMessages in DB archive.
 * The filter is set by the user in LogViewArchive.
 * 
 * @author jhatje
 *
 */
public class Filter {

	/**
	 * List of filter items. An item is e.g. 'SEVERITY == MAJOR'
	 * and the connection with the next item e.g. AND,OR,end.
	 */
	private ArrayList<FilterItem> filterItems;
	
	/**
	 * Begin of the time period.
	 */
	private Calendar from; 

	/**
	 * End of the time period.
	 */
	private Calendar to;

	public Calendar getTo() {
		return to;
	}

	public void setTo(Calendar to) {
		this.to = to;
	}

	public ArrayList<FilterItem> getFilterItems() {
		return filterItems;
	}

	public void setFilterItems(ArrayList<FilterItem> filterItems) {
		this.filterItems = filterItems;
	}

	public Calendar getFrom() {
		return from;
	}

	public void setFrom(Calendar from) {
		this.from = from;
	} 
	
}
