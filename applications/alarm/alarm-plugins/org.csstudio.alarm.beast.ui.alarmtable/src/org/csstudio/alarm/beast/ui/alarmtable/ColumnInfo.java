package org.csstudio.alarm.beast.ui.alarmtable;


/** Description of one column in the alarm table
 *
 *  @author Kay Kasemir
 *  @author Jaka Bobnar - Extracted inner class of AlarmTableLabelProvider
 */
public enum ColumnInfo
{
    ACK(Messages.ColumnHeaderAcknowledge, Messages.ColumnHeaderAcknowledgeTooltip ,35, 0),
    ICON(Messages.ColumnHeaderIcon, Messages.ColumnHeaderIconTooltip, 20, 0),
    PV(Messages.ColumnHeaderPV, Messages.ColumnHeaderPVTooltip, 80, 50),
    DESCRIPTION(Messages.ColumnHeaderDescription, Messages.ColumnHeaderDescriptionTooltip, 80, 100),
    TIME(Messages.ColumnHeaderAlarmTime, Messages.ColumnHeaderAlarmTimeTooltip, 80, 70),
    CURRENT_SEVERITY(Messages.ColumnHeaderCurrentSeverity, Messages.ColumnHeaderCurrentSeverityTooltip, 50, 30),
    CURRENT_STATUS(Messages.ColumnHeaderCurrentStatus, Messages.ColumnHeaderCurrentStatusTooltip, 45, 30),
    SEVERITY(Messages.ColumnHeaderSeverity, Messages.ColumnHeaderSeverityTooltip, 50, 30),
    STATUS(Messages.ColumnHeaderStatus, Messages.ColumnHeaderStatusTooltip, 45, 30),
    VALUE(Messages.ColumnHeaderValue, Messages.ColumnHeaderValueTooltip, 45, 30),
    ACTION(Messages.ColumnHeaderAction, Messages.ColumnHeaderActionTooltip, 45, 30),
    ID(Messages.ColumnHeaderAlarmID, Messages.ColumnHeaderAlarmIDTooltip, 45, 30);

    final private String title;
    final private String tooltip;

    final private int width, weight;

    /** Initialize Column
     *  @param title Column title
     */
    ColumnInfo(final String title, final String tooltip, final int widths, final int weight)
    {
        this.title = title;
        this.tooltip = tooltip;
        this.width = widths;
        this.weight = weight;
    }

    /** @return Column title */
    public String getTitle()
    {
        return title;
    }

    /** @return Minimum column width */
    public int getMinWidth()
    {
        return width;
    }

    /** @return Column resize weight */
    public int getWeight()
    {
        return weight;
    }

    /** @return column header tooltip */
    public String getTooltip()
    {
        return tooltip;
    }

    /**
     * Returns the array of all names of column info objects. These names can be used to create a column info object using
     * {@link ColumnInfo#valueOf(String)}.
     *
     * @return the names of all column info objects
     */
    public static String[] stringValues(){
        ColumnInfo[] vals = values();
        String[] sv = new String[vals.length];
        for(int i = 0; i < sv.length; i++)
            sv[i] = vals[i].name();
        return sv;
    }
}
