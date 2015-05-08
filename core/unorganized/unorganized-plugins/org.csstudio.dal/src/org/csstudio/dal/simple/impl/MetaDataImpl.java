package org.csstudio.dal.simple.impl;

import org.csstudio.dal.AccessType;
import org.csstudio.dal.simple.MetaData;

/**
 *
 * <code>MetaDataImpl</code> is a default implementation of the {@link MetaData}
 * interface, which receives all data through a constructor and returns it
 * through the interface methods.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class MetaDataImpl implements MetaData {

    private final String name;
    private final String description;
    private final double displayLow;
    private final double displayHigh;
    private final double warningLow;
    private final double warningHigh;
    private final double alarmLow;
    private final double alarmHigh;
    private final String[] enumDescriptions;
    private final Object[] enumValues;
    private final String format;
    private final String units;
    private final int precision;
    private final String dataType;
    private final AccessType accessType;
    private final String hostname;
    private final int sequenceLength;

    protected MetaDataImpl(String name, String description, Number displayLow, Number displayHigh,
            Number warningLow, Number warningHigh, Number alarmLow, Number alarmHigh,
            String[] enumDescriptions, Object[] enumValues,    String format, String units,
            Integer sequenceLength, Integer precision, String dataType,
            AccessType accessType, String hostname) {
        this.name = name;
        this.description = description;
        this.displayLow = displayLow != null ? displayLow.doubleValue() : Double.NaN;
        this.displayHigh = displayHigh != null ? displayHigh.doubleValue() : Double.NaN;
        this.warningLow = warningLow != null ? warningLow.doubleValue() : Double.NaN;
        this.warningHigh = warningHigh != null ? warningHigh.doubleValue() : Double.NaN;
        this.alarmLow = alarmLow != null ? alarmLow.doubleValue() : Double.NaN;
        this.alarmHigh = alarmHigh != null ? alarmHigh.doubleValue() : Double.NaN;
        this.enumDescriptions = enumDescriptions;
        this.enumValues = enumValues;
        this.format = format;
        this.units = units;
        this.precision = precision != null ? precision : 0;
        this.dataType = dataType;
        this.accessType = accessType != null ? accessType : AccessType.NONE;
        this.hostname = hostname;
        this.sequenceLength = sequenceLength != null ? sequenceLength : 1;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getAccessType()
     */
    public AccessType getAccessType() {
        return accessType;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getAlarmHigh()
     */
    public double getAlarmHigh() {
        return alarmHigh;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getAlarmLow()
     */
    public double getAlarmLow() {
        return alarmLow;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getDataType()
     */
    public String getDataType() {
        return dataType;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getDisplayHigh()
     */
    public double getDisplayHigh() {
        return displayHigh;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getDisplayLow()
     */
    public double getDisplayLow() {
        return displayLow;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getState(int)
     */
    public String getState(int index) {
        if (enumDescriptions == null) return null;
        return enumDescriptions[index];
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getStates()
     */
    public String[] getStates() {
        if (enumDescriptions == null) return null;
        String[] s = new String[enumDescriptions.length];
        System.arraycopy(enumDescriptions, 0, s, 0, s.length);
        return s;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getStateValue(int)
     */
    public Object getStateValue(int index) {
        if (enumValues == null) return null;
        return enumValues[index];
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getStateValues()
     */
    public Object[] getStateValues() {
        if (enumValues == null) return null;
        String[] s = new String[enumValues.length];
        System.arraycopy(enumValues, 0, s, 0, s.length);
        return s;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getFormat()
     */
    public String getFormat() {
        return format;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getHostname()
     */
    public String getHostname() {
        return hostname;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getPrecision()
     */
    public int getPrecision() {
        return precision;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getUnits()
     */
    public String getUnits() {
        return units;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getWarnHigh()
     */
    public double getWarnHigh() {
        return warningHigh;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getWarnLow()
     */
    public double getWarnLow() {
        return warningLow;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.simple.MetaData#getSequenceLength()
     */
    public int getSequenceLength() {
        return sequenceLength;
    }

    public static MetaData createUninitializedMetaData() {
        return new MetaDataImpl(null, null, null, null, null, null, null, null,
                null, null,    null, null, null, null, null, null, null);
    }
}
