package org.csstudio.alarm.beast.ui.alarmtable;

import org.csstudio.alarm.beast.ui.Messages;

/** Description of one column in the alarm table
 * 
 *  @author Kay Kasemir
 *  @author Jaka Bobnar - Extracted inner class of AlarmTableLabelProvider
 */
public enum ColumnInfo
{
    ACK(Messages.AcknowledgeColumnHeader,35,0),
    ICON(Messages.AlarmIconColumnHeader,20,0),
    PV(Messages.AlarmPV, 80, 50),
    DESCRIPTION(Messages.AlarmDescription, 80, 100),
    TIME(Messages.AlarmTime, 80, 70),
    CURRENT_SEVERITY(Messages.AlarmCurrentSeverity, 50, 30),
    CURRENT_STATUS(Messages.AlarmCurrentMessage, 45, 30),
    SEVERITY(Messages.AlarmSeverity, 50, 30),
    STATUS(Messages.AlarmMessage, 45, 30),
    VALUE(Messages.AlarmValue, 45, 30),
    ACTION(Messages.AlarmAction,45,30),
    ID(Messages.AlarmID,45,30);

    final private String title;

    final private int width, weight;

    /** Initialize Column
     *  @param title Column title
     */
    ColumnInfo(final String title, final int widths, final int weight)
    {
        this.title = title;
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
}
