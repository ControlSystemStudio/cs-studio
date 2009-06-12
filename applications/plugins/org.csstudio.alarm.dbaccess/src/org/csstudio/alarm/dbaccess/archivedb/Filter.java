package org.csstudio.alarm.dbaccess.archivedb;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.csstudio.platform.logging.CentralLogger;

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
    private ArrayList<FilterItem> filterItems = new ArrayList<FilterItem>();

    /**
     * Begin of the time period.
     */
    private GregorianCalendar from;

    /**
     * End of the time period.
     */
    private GregorianCalendar to;

    /**
     * Maximum number of messages for the database request.
     */
    private int _maximumMessageNumber;

    public Filter(GregorianCalendar from, GregorianCalendar to) {
        this(null, from, to, 5000);
    }

    public Filter(ArrayList<FilterItem> filterItems, GregorianCalendar from,
            GregorianCalendar to) {
        this(filterItems, from, to, 5000);
    }

    public Filter(ArrayList<FilterItem> filterItems, GregorianCalendar from,
            GregorianCalendar to, int maxMsgSize) {
        if (filterItems != null) {
            this.filterItems = filterItems;
        }
        this.from = from;
        this.to = to;
        this._maximumMessageNumber = maxMsgSize;
    }

    public GregorianCalendar getTo() {
        return to;
    }

    public void setTo(GregorianCalendar to) {
        this.to = to;
    }

    public void clearFilter() {
        filterItems.clear();
    }

    public void addFilterItem(String property, String value, String relation) {
        filterItems.add(new FilterItem(property, value, relation));
    }
    
    public ArrayList<FilterItem> getFilterItems() {
        return filterItems;
    }

    public void setFilterItems(ArrayList<FilterItem> filterItems) {
        this.filterItems = filterItems;
    }

    public void setFilterItem(FilterItem filterItem) {
        filterItems.add(filterItem);
    }

    public GregorianCalendar getFrom() {
        return from;
    }

    public void setFrom(GregorianCalendar from) {
        this.from = from;
    }

    /**
     * The list of Filter settings consists of property value pairs associated
     * with AND or OR. Because the sql statement is created only for the AND
     * parts (for a better performance the or will be merged in java) this
     * method splits the FilterSetting list on the OR association and returns a
     * list of lists of filter settings associated only with AND.
     * 
     * @param filter
     * @return
     */
    public ArrayList<ArrayList<FilterItem>> getSeparatedFilterSettings() {

        // list of list of AND associated Filter settings we want to return.
        ArrayList<ArrayList<FilterItem>> separatedFilterSettings = new ArrayList<ArrayList<FilterItem>>();
        // list of filterSettings associated with AND to put in
        // separatedFilterSettings.
        ArrayList<FilterItem> filterSettingsAndAssociated = new ArrayList<FilterItem>();
        // if filter is null (user searches only for time period) set one
        // empty list of filter settings.
        if ((filterItems == null) || (filterItems.size() == 0)) {
            filterSettingsAndAssociated = new ArrayList<FilterItem>();
            separatedFilterSettings.add(filterSettingsAndAssociated);
            return separatedFilterSettings;
        }
        String association = "BEGIN";
        for (FilterItem setting : filterItems) {
            if (association.equalsIgnoreCase("AND")) {
                if (filterSettingsAndAssociated != null) {
                    association = setting.getRelation();
                    filterSettingsAndAssociated.add(setting);
                } else {
                    CentralLogger.getInstance().error(this,
                            "invalid filter configuration");
                }
                continue;
            }
            if (association.equalsIgnoreCase("OR")) {
                separatedFilterSettings.add(filterSettingsAndAssociated);
                association = setting.getRelation();
                filterSettingsAndAssociated = new ArrayList<FilterItem>();
                filterSettingsAndAssociated.add(setting);
                continue;
            }
            if (association.equalsIgnoreCase("BEGIN")) {
                filterSettingsAndAssociated = new ArrayList<FilterItem>();
                filterSettingsAndAssociated.add(setting);
                association = setting.getRelation();
                continue;
            }

        }
        separatedFilterSettings.add(filterSettingsAndAssociated);

        return separatedFilterSettings;
    }

    /**
     * A deep copy of this filter.
     * 
     * @return The copy of this filter
     */
    public Filter copy() {
        GregorianCalendar newFrom = (GregorianCalendar) this.getFrom().clone();
        GregorianCalendar newTo = (GregorianCalendar) this.getTo().clone();
        ArrayList<FilterItem> newFilterItems = new ArrayList<FilterItem>();
        if (this.filterItems != null) {
            for (FilterItem filterItem : this.getFilterItems()) {
                FilterItem f = new FilterItem(filterItem.getProperty(),
                        filterItem.getValue(), filterItem.getRelation());
                newFilterItems.add(f);
            }
        } else {
            newFilterItems = null;
        }
        return new Filter(newFilterItems, newFrom, newTo, _maximumMessageNumber);
    }

    public int getMaximumMessageSize() {
        return _maximumMessageNumber;
    }

    public void setMaximumMessageNumber(int messageNumber) {
        _maximumMessageNumber = messageNumber;
    }
}
