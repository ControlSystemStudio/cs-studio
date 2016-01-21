package org.csstudio.archive.diirt.datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.diirt.datasource.DataSourceTypeAdapter;
import org.diirt.datasource.ValueCache;
import org.diirt.util.array.ListByte;
import org.diirt.util.array.ListDouble;
import org.diirt.util.array.ListFloat;
import org.diirt.util.array.ListInt;
import org.diirt.util.array.ListLong;
import org.diirt.util.array.ListNumber;
import org.diirt.util.array.ListShort;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.Time;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VStatistics;
import org.diirt.vtype.VString;
import org.diirt.vtype.VTable;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.diirt.vtype.VEnum;

/**
 *
 * <code>ArchiveTypeAdapter</code> is an adapter for transforming the archiver data to {@link VTable}. The table has one
 * single column named values and the values themselves are {@link VType} objects. The objects that the archive reader
 * returns are transformed into pure {@link VType} objects from the diirt plugin so that there are no issues if the
 * client of the archive datasource does not depend on the archive vtypes.
 *
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ArchiveTypeAdapter implements DataSourceTypeAdapter<Boolean, List<VType>> {

    private static final String VALUES = "values";

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.DataSourceTypeAdapter#getSubscriptionParameter(org.diirt.datasource.ValueCache,
     * java.lang.Object)
     */
    @Override
    public Object getSubscriptionParameter(ValueCache<?> cache, Boolean connection) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.DataSourceTypeAdapter#updateCache(org.diirt.datasource.ValueCache, java.lang.Object,
     * java.lang.Object)
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    public boolean updateCache(ValueCache<?> cache, Boolean connection, List<VType> values) {
        if (VTable.class.isAssignableFrom(cache.getType()) || VType.class.equals(cache.getType())) {
            if (values.isEmpty()) {
                VTable table = ValueFactory.newVTable(Arrays.asList(VType.class), Arrays.asList(VALUES),
                    Arrays.asList(values));
                ((ValueCache<VTable>)cache).writeValue(table);
            } else {
                // Transform the archive vtypes to standard vtypes, so that there is absolutely no dependency on the
                // archive.vtype plugin
                final List<VType> newValues = new ArrayList<>(values.size());
                VType value = values.get(0);
                if (value instanceof VNumber) {
                    Number numVal = ((VNumber) value).getValue();
                    VNumber t;
                    if (numVal instanceof Double) {
                        for (VType v : values) {
                            t = ((VNumber) v);
                            newValues.add(ValueFactory.newVDouble(t.getValue().doubleValue(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    } else if (numVal instanceof Float) {
                        for (VType v : values) {
                            t = ((VNumber) v);
                            newValues.add(ValueFactory.newVFloat(t.getValue().floatValue(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    } else if (numVal instanceof Long) {
                        for (VType v : values) {
                            t = ((VNumber) v);
                            newValues.add(ValueFactory.newVLong(t.getValue().longValue(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    } else if (numVal instanceof Integer) {
                        for (VType v : values) {
                            t = ((VNumber) v);
                            newValues.add(ValueFactory.newVInt(t.getValue().intValue(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    } else if (numVal instanceof Short) {
                        for (VType v : values) {
                            t = ((VNumber) v);
                            newValues.add(ValueFactory.newVShort(t.getValue().shortValue(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    } else if (numVal instanceof Byte) {
                        for (VType v : values) {
                            t = ((VNumber) v);
                            newValues.add(ValueFactory.newVByte(t.getValue().byteValue(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    }
                } else if (value instanceof VString) {
                    VString t;
                    for (VType v : values) {
                        t = ((VString) v);
                        newValues.add(ValueFactory.newVString(t.getValue(),
                            ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                            ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid())));
                    }
                } else if (value instanceof VEnum) {
                    VEnum t;
                    for (VType v : values) {
                        t = ((VEnum) v);
                        Alarm a = (Alarm) t;
                        Time ti = (Time) t;
                        newValues.add(ValueFactory.newVEnum(t.getIndex(), t.getLabels(),
                            ValueFactory.newAlarm(a.getAlarmSeverity(), a.getAlarmName()),
                            ValueFactory.newTime(ti.getTimestamp(), ti.getTimeUserTag(), ti.isTimeValid())));
                    }
                } else if (value instanceof VNumberArray) {
                    ListNumber numVal = ((VNumberArray) value).getData();
                    VNumberArray t;
                    if (numVal instanceof ListByte) {
                        for (VType v : values) {
                            t = ((VNumberArray) v);
                            newValues.add(ValueFactory.newVNumberArray(t.getData(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    } else if (numVal instanceof ListDouble) {
                        for (VType v : values) {
                            t = ((VNumberArray) v);
                            newValues.add(ValueFactory.newVDoubleArray((ListDouble) t.getData(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    } else if (numVal instanceof ListFloat) {
                        for (VType v : values) {
                            t = ((VNumberArray) v);
                            newValues.add(ValueFactory.newVFloatArray((ListFloat) t.getData(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    } else if (numVal instanceof ListInt) {
                        for (VType v : values) {
                            t = ((VNumberArray) v);
                            newValues.add(ValueFactory.newVIntArray((ListInt) t.getData(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    } else if (numVal instanceof ListLong) {
                        for (VType v : values) {
                            t = ((VNumberArray) v);
                            newValues.add(ValueFactory.newVLongArray((ListLong) t.getData(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    } else if (numVal instanceof ListShort) {
                        for (VType v : values) {
                            t = ((VNumberArray) v);
                            newValues.add(ValueFactory.newVShortArray((ListShort) t.getData(),
                                ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                                ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                                ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                    t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                    t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                    t.getUpperCtrlLimit())));
                        }
                    }
                } else if (value instanceof VStatistics) {
                    VStatistics t;
                    for (VType v : values) {
                        t = ((VStatistics) v);
                        newValues.add(ValueFactory.newVStatistics(t.getAverage(), t.getStdDev(), t.getMin(), t.getMax(),
                            t.getNSamples(), ValueFactory.newAlarm(t.getAlarmSeverity(), t.getAlarmName()),
                            ValueFactory.newTime(t.getTimestamp(), t.getTimeUserTag(), t.isTimeValid()),
                            ValueFactory.newDisplay(t.getLowerDisplayLimit(), t.getLowerAlarmLimit(),
                                t.getLowerWarningLimit(), t.getUnits(), t.getFormat(), t.getUpperWarningLimit(),
                                t.getUpperAlarmLimit(), t.getUpperDisplayLimit(), t.getLowerCtrlLimit(),
                                t.getUpperCtrlLimit())));
                    }
                }

                VTable table = ValueFactory.newVTable(Arrays.asList(VType.class), Arrays.asList(VALUES),
                    Arrays.asList(newValues));
                ((ValueCache<VTable>)cache).writeValue(table);
            }
            return true;
        } else {
            throw new IllegalArgumentException("Requested type " + cache.getType() + " is incompatible with VTable.");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.DataSourceTypeAdapter#match(org.diirt.datasource.ValueCache, java.lang.Object)
     */
    @Override
    public int match(ValueCache<?> cache, Boolean connection) {
        return 1;
    }
}
