package org.csstudio.alarm.table.preferences;

public interface ISeverityMapping {
    /**
     * returns the severity value for the severity key of this message.
     * 
     * @return
     */
    public String findSeverityValue(String severityKey);

    /**
     * Returns the number of the severity. The number represents the level of
     * the severity.
     * 
     * @return
     */
    public int getSeverityNumber(String severityKey);
}
