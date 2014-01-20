/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import java.util.Objects;

/**
 * Helper class that provides functions to test value equality.
 *
 * @author carcassi
 */
public class VTypeValueEquals {

    private VTypeValueEquals() {
        // Do not create
    }

    public static boolean alarmEquals(Alarm alarm1, Alarm alarm2) {
	if (alarm1 == null && alarm2 == null) {
	    return true;
	}

	if (alarm1 == null || alarm2 == null) {
	    return false;
	}
        
        return alarm1.getAlarmSeverity().equals(alarm2.getAlarmSeverity()) &&
                alarm1.getAlarmName().equals(alarm2.getAlarmName());
    }

    public static boolean timeEquals(Time time1, Time time2) {
	if (time1 == null && time2 == null) {
	    return true;
	}

	if (time1 == null || time2 == null) {
	    return false;
	}
        
        return Objects.equals(time1.getTimestamp(), time2.getTimestamp()) &&
                Objects.equals(time1.getTimeUserTag(), time2.getTimeUserTag()) &&
                time1.isTimeValid() == time2.isTimeValid();
    }

    public static boolean typeEquals(Object obj1, Object obj2) {
	if (obj1 == null && obj2 == null) {
	    return true;
	}

	if (obj1 == null || obj2 == null) {
	    return false;
	}
        
        return Objects.equals(ValueUtil.typeOf(obj1), ValueUtil.typeOf(obj2));
    }
    
    /**
     * Checks whether the two table have the same data: equal column names, number
     * of rows and columns and their value.
     * 
     * @param arg1 a table
     * @param arg2 another table
     * @return true if the values are equals
     */
    public static boolean valueEquals(VTable arg1, VTable arg2) {
        if (arg1.getColumnCount() != arg2.getColumnCount()) {
            return false;
        }
        
        if (arg1.getRowCount() != arg2.getRowCount()) {
            return false;
        }
        
        for (int i = 0; i < arg1.getColumnCount(); i++) {
            if (!arg1.getColumnName(i).equals(arg2.getColumnName(i))) {
                return false;
            }

            if (!arg1.getColumnType(i).equals(arg2.getColumnType(i))) {
                return false;
            }
            
            if (!arg1.getColumnData(i).equals(arg2.getColumnData(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean valueEquals(VNumberArray array1, VNumberArray array2) {
        return array1.getData().equals(array2.getData()) && array1.getSizes().equals(array2.getSizes());
    }

    public static boolean valueEquals(VNumber number1, VNumber number2) {
        return number1.getValue().equals(number2.getValue());
    }

    public static boolean valueEquals(VString str1, VString str2) {
        return str1.getValue().equals(str2.getValue());
    }

    public static boolean valueEquals(VEnum enum1, VEnum enum2) {
        return enum1.getValue().equals(enum2.getValue()) && enum1.getLabels().equals(enum2.getLabels());
    }

    public static boolean valueEquals(VStringArray array1, VStringArray array2) {
        return array1.getData().equals(array2.getData());
    }

    public static boolean valueEquals(Object obj1, Object obj2) {
	if (obj1 == null && obj2 == null) {
	    return true;
	}

	if (obj1 == null || obj2 == null) {
	    return false;
	}

	if ((obj1 instanceof VNumberArray) && (obj2 instanceof VNumberArray)) {
	    return valueEquals((VNumberArray) obj1, (VNumberArray) obj2);
	}

	if ((obj1 instanceof VStringArray) && (obj2 instanceof VStringArray)) {
	    return valueEquals((VStringArray) obj1, (VStringArray) obj2);
	}

	if ((obj1 instanceof VString) && (obj2 instanceof VString)) {
	    return valueEquals((VString) obj1, (VString) obj2);
	}

	if ((obj1 instanceof VEnum) && (obj2 instanceof VEnum)) {
	    return valueEquals((VEnum) obj1, (VEnum) obj2);
	}
	
	if((obj1 instanceof VNumber) && (obj2 instanceof VNumber)) {
	    return valueEquals((VNumber) obj1, (VNumber) obj2);
	}

	return false;
    }

}
