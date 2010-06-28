package org.csstudio.alarm.dbaccess.archivedb;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.csstudio.platform.logging.CentralLogger;

import com.sun.istack.internal.Nullable;

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
    private ArrayList<FilterItem> _filterItems = new ArrayList<FilterItem>();

    /**
     * Begin of the time period.
     */
    private GregorianCalendar _from;

    /**
     * End of the time period.
     */
    private GregorianCalendar _to;

    /**
     * Maximum number of messages for the database request.
     */
    private int _maximumMessageNumber;

    /**
     * Name of the filter. Important for stored filters to display name in combo box.
     * For current filter setting the name is null.
     */
    private String _filterName;

    public Filter(String name) {
        this(name, null, null, null, 5000);
    }
    
    public Filter(GregorianCalendar from, GregorianCalendar to) {
        this(null, from, to);
    }

    public Filter(ArrayList<FilterItem> filterItems, GregorianCalendar from,
            GregorianCalendar to) {
        this(null, filterItems, from, to, 5000);
    }

    public Filter(String name, ArrayList<FilterItem> filterItems, GregorianCalendar from,
                  GregorianCalendar to) {
        this(name, filterItems, from, to, 5000);
    }

    public Filter(@Nullable String name, ArrayList<FilterItem> filterItems, GregorianCalendar from,
            GregorianCalendar to, int maxMsgSize) {
        if (filterItems != null) {
            this._filterItems = filterItems;
        }
        _filterName = name;
        _from = from;
        _to = to;
        _maximumMessageNumber = maxMsgSize;
    }

    public GregorianCalendar getTo() {
        return _to;
    }

    public void setTo(GregorianCalendar to) {
        this._to = to;
    }

    public void clearFilter() {
        _filterItems.clear();
    }

    public void addFilterItem(String property, String value, String relation) {
        _filterItems.add(new FilterItem(property, value, relation));
    }
    
    public ArrayList<FilterItem> getFilterItems() {
        return _filterItems;
    }

    public void setFilterItems(ArrayList<FilterItem> filterItems) {
        this._filterItems = filterItems;
    }

    public void setFilterItem(FilterItem filterItem) {
        _filterItems.add(filterItem);
    }

    public GregorianCalendar getFrom() {
        return _from;
    }

    public void setFrom(GregorianCalendar from) {
        this._from = from;
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
        if ((_filterItems == null) || (_filterItems.size() == 0)) {
            filterSettingsAndAssociated = new ArrayList<FilterItem>();
            separatedFilterSettings.add(filterSettingsAndAssociated);
            return separatedFilterSettings;
        }
        String association = "BEGIN";
        for (FilterItem setting : _filterItems) {
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
        GregorianCalendar newFrom = null;
        GregorianCalendar newTo = null;
        if ((_from != null) && (_to != null)) {
            newFrom = (GregorianCalendar) _from.clone();
            newTo = (GregorianCalendar) _to.clone();
        }
        ArrayList<FilterItem> newFilterItems = new ArrayList<FilterItem>();
        if (this._filterItems != null) {
            for (FilterItem filterItem : this.getFilterItems()) {
                FilterItem f = new FilterItem(filterItem.getProperty(),
                        filterItem.getOriginalValue(), filterItem.getRelation());
                newFilterItems.add(f);
            }
        } else {
            newFilterItems = null;
        }
        return new Filter(_filterName, newFilterItems, newFrom, newTo, _maximumMessageNumber);
    }

    public int getMaximumMessageSize() {
        return _maximumMessageNumber;
    }

    public void setMaximumMessageNumber(int messageNumber) {
        _maximumMessageNumber = messageNumber;
    }

    @Nullable
    public String getName() {
        return _filterName;
    }

    public void setName(@Nullable String name) {
        _filterName = name;
    }

    /**
     * Check weather the THIS filter has the same setting as the given one.
     * Without time settings
	 */ 
    public boolean compareWithoutTime(Filter filter) {
        if ((_filterName.equals(filter.getName()) == false) ||
            (_filterItems.size() != filter.getFilterItems().size())) {
            return false;
        }
        for (FilterItem localItem : _filterItems) {
            boolean isIncluded = false;
            for (FilterItem newItem : filter.getFilterItems()) {
                if (newItem.compare(localItem)) {
                    isIncluded = true;
                    break;
                }
            }
            //THIS filter and given filter are different because one item is
            //not included in THIS filter.
            if (isIncluded == false) {
                return false;
            }
        }
        return true;
    }
}
