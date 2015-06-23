package org.csstudio.dal.epics;

import org.csstudio.dal.PropertyCharacteristics;

/**
 *
 * <code>EpicsPropertyCharacteristics</code> provides some of the epics specific
 * characteristics names of a property, such as alarm min/max, number of elements etc.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface EpicsPropertyCharacteristics extends PropertyCharacteristics {

    public static final String EPICS_WARNING_MAX = "HI";
    public static final String EPICS_WARNING_MIN = "LO";
    public static final String EPICS_ALARM_MAX = "HIHI";
    public static final String EPICS_ALARM_MIN = "LOLO";
    public static final String EPICS_MAX = "DRVH";
    public static final String EPICS_MIN = "DRVL";
    public static final String EPICS_OPR_MAX = "HOPR";
    public static final String EPICS_OPR_MIN = "LOPR";
    public static final String EPICS_UNITS = "EGU";
    public static final String EPICS_NUMBER_OF_ELEMENTS = "NELM";
}
