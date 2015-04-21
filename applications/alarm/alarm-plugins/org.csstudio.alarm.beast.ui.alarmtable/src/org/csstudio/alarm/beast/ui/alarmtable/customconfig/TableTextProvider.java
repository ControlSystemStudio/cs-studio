package org.csstudio.alarm.beast.ui.alarmtable.customconfig;

import org.csstudio.alarm.beast.client.AlarmTreePV;

/**
 * 
 * <code>TableDataprovider</code> is an extension to the alarm table through which
 * other plugins can provide their own text to be displayed in individual table cells.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface TableTextProvider {

    /** Implementation that always returns null */
    public static TableTextProvider EMPTY_PROVIDER = new TableTextProvider() {
        
        @Override
        public String getContentForColumn(String columnId, AlarmTreePV item) {
            return null;
        }
    };
    
    /** The extension point id */
    public static final String EXTENSION_ID = DoubleClickHandler.EXTENSION_ID;
    /** The name of the element */
    public static final String NAME = "tableTextProvider";
    
    /**
     * Returns the text for the table cell in the given column for the given pv.
     * If the provider returns null, the default values will be used.
     * 
     * @param columnId the column id, identical to the name of the ColumnInfo
     * @param item the item for which the text is requested
     * @return the text, can be null
     */
    public String getContentForColumn(String columnId, AlarmTreePV item);
}
