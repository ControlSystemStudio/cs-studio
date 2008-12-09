package org.csstudio.alarm.dbaccess.archivedb;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Filter for JMSMessages in DB archive. The filter is set by the user in
 * LogViewArchive.
 * 
 * @author jhatje
 * 
 */
public class Filter {

	/**
	 * List of filter items. An item is e.g. 'SEVERITY == MAJOR' and the
	 * connection with the next item e.g. AND,OR,end.
	 */
	private ArrayList<FilterItem> filterItems;

	/**
	 * Begin of the time period.
	 */
	private GregorianCalendar from;

	/**
	 * End of the time period.
	 */
	private GregorianCalendar to;

	public Filter(ArrayList<FilterItem> filterItems, GregorianCalendar from,
			GregorianCalendar to) {
		super();
		this.filterItems = filterItems;
		this.from = from;
		this.to = to;
	}

	public GregorianCalendar getTo() {
		return to;
	}

	public void setTo(GregorianCalendar to) {
		this.to = to;
	}

	public ArrayList<FilterItem> getFilterItems() {
		return filterItems;
	}

	public void setFilterItems(ArrayList<FilterItem> filterItems) {
		this.filterItems = filterItems;
	}

	public GregorianCalendar getFrom() {
		return from;
	}

	public void setFrom(GregorianCalendar from) {
		this.from = from;
	}

	public Filter copy() {
		GregorianCalendar newFrom = (GregorianCalendar) this.getFrom().clone();
		// newFrom.set(GregorianCalendar.HOUR_OF_DAY, filter.getFrom().get(
		// GregorianCalendar.HOUR_OF_DAY));
		// newFrom.set(GregorianCalendar.YEAR,
		// filter.getFrom().get(GregorianCalendar.YEAR));
		// newFrom.set(GregorianCalendar.MONTH,
		// filter.getFrom().get(GregorianCalendar.MONTH));
		// newFrom.set(GregorianCalendar.DAY_OF_MONTH, filter.getFrom().get(
		// GregorianCalendar.DAY_OF_MONTH));
		// newFrom.set(GregorianCalendar.MINUTE,
		// filter.getFrom().get(GregorianCalendar.MINUTE));
		// newFrom.set(GregorianCalendar.SECOND,
		// filter.getFrom().get(GregorianCalendar.SECOND));
		// newFrom.set(GregorianCalendar.MILLISECOND, filter.getFrom().get(
		// GregorianCalendar.MILLISECOND));

		GregorianCalendar newTo = (GregorianCalendar) this.getTo().clone();
		// newTo.set(GregorianGregorianCalendar.HOUR_OF_DAY, filter.getTo()
		// .get(GregorianGregorianCalendar.HOUR_OF_DAY));
		// newTo.set(GregorianGregorianCalendar.YEAR,
		// filter.getTo().get(GregorianGregorianCalendar.YEAR));
		// newTo.set(GregorianGregorianCalendar.MONTH,
		// filter.getTo().get(GregorianGregorianCalendar.MONTH));
		// newTo.set(GregorianGregorianCalendar.DAY_OF_MONTH,
		// filter.getTo().get(
		// GregorianGregorianCalendar.DAY_OF_MONTH));
		// newTo.set(GregorianGregorianCalendar.MINUTE,
		// filter.getTo().get(GregorianGregorianCalendar.MINUTE));
		// newTo.set(GregorianGregorianCalendar.SECOND,
		// filter.getTo().get(GregorianGregorianCalendar.SECOND));
		// newTo.set(GregorianGregorianCalendar.MILLISECOND, filter.getTo()
		// .get(GregorianGregorianCalendar.MILLISECOND));

		ArrayList<FilterItem> newFilterItems = new ArrayList<FilterItem>();
		if (this.filterItems != null) {
			for (FilterItem filterItem : this.getFilterItems()) {
				FilterItem f = new FilterItem(filterItem.get_property(),
						filterItem.get_value(), filterItem.get_relation());
				newFilterItems.add(f);
			}
		} else {
			newFilterItems = null;
		}
		return new Filter(newFilterItems, newFrom, newTo);
	}
}
