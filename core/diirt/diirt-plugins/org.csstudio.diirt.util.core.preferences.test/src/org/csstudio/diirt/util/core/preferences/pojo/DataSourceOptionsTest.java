/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences.pojo;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.MonitorMask;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.VariableArraySupport;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;


/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 14 Dec 2016
 */
public class DataSourceOptionsTest {

    @Test
    public void testConstructor ( ) {

        DataSourceOptions dso1 = new DataSourceOptions();

        assertFalse(dso1.dbePropertySupported);
        assertTrue(dso1.honorZeroPrecision);
        assertEquals(MonitorMask.VALUE.name(), dso1.monitorMaskValue);
        assertFalse(dso1.rtypeValueOnly);
        assertEquals(VariableArraySupport.AUTO.representation(), dso1.varArraySupported);

        dso1.dbePropertySupported = true;
        dso1.honorZeroPrecision = false;
        dso1.monitorMaskValue = MonitorMask.ALARM.name();
        dso1.rtypeValueOnly = true;
        dso1.varArraySupported = VariableArraySupport.FALSE.representation();

        DataSourceOptions dso2 = new DataSourceOptions(true, false, MonitorMask.ALARM, 1234, true, VariableArraySupport.FALSE);

        assertEquals(dso1,  dso2);

    }

    @Test
    public void testMonitorMask ( ) {

        assertEquals(MonitorMask.VALUE, MonitorMask.fromString("VALUE"));
        assertEquals(MonitorMask.ARCHIVE, MonitorMask.fromString("ARCHIVE"));
        assertEquals(MonitorMask.ALARM, MonitorMask.fromString("ALARM"));
        assertEquals(MonitorMask.CUSTOM, MonitorMask.fromString("CUSTOM"));
        assertEquals(MonitorMask.CUSTOM, MonitorMask.fromString("fuffa"));

        DataSourceOptions dso1 = new DataSourceOptions(true, false, MonitorMask.VALUE, 1234, true, VariableArraySupport.AUTO);

        assertEquals(MonitorMask.VALUE, dso1.monitorMask());
        assertEquals(VariableArraySupport.AUTO, dso1.variableArraySupport());

        DataSourceOptions dso2 = new DataSourceOptions(true, false, MonitorMask.ARCHIVE, 2345, true, VariableArraySupport.TRUE);

        assertEquals(MonitorMask.ARCHIVE, dso2.monitorMask());
        assertEquals(VariableArraySupport.TRUE, dso2.variableArraySupport());

        DataSourceOptions dso3 = new DataSourceOptions(true, false, MonitorMask.ALARM, 3456, true, VariableArraySupport.FALSE);

        assertEquals(MonitorMask.ALARM, dso3.monitorMask());
        assertEquals(VariableArraySupport.FALSE, dso3.variableArraySupport());

        DataSourceOptions dso4 = new DataSourceOptions(true, false, MonitorMask.CUSTOM, 4567, true, VariableArraySupport.FALSE);

        assertEquals(MonitorMask.CUSTOM, dso4.monitorMask());
        assertEquals(4567, dso4.monitorMaskCustomValue());

    }

    /**
     * This test is made to fail if the structure of {@link DataSourceOptions}
     * changed, ensuring that also the test classes are changed too.
     */
    @Test
    public void testStructure ( ) throws NoSuchFieldException, SecurityException {

        assertEquals(5, Arrays.asList(DataSourceOptions.class.getDeclaredFields()).stream().filter(f -> !f.isSynthetic()).count());

        assertEquals(boolean.class, DataSourceOptions.class.getDeclaredField("dbePropertySupported").getType());
        assertEquals(boolean.class, DataSourceOptions.class.getDeclaredField("honorZeroPrecision").getType());
        assertEquals(String.class, DataSourceOptions.class.getDeclaredField("monitorMaskValue").getType());
        assertEquals(boolean.class, DataSourceOptions.class.getDeclaredField("rtypeValueOnly").getType());
        assertEquals(String.class, DataSourceOptions.class.getDeclaredField("varArraySupported").getType());

    }

    @Test
    public void testVariableArraySupport ( ) {

        assertEquals(VariableArraySupport.AUTO, VariableArraySupport.fromString("auto"));
        assertEquals(VariableArraySupport.FALSE, VariableArraySupport.fromString("false"));
        assertEquals(VariableArraySupport.TRUE, VariableArraySupport.fromString("true"));
        assertEquals(VariableArraySupport.AUTO, VariableArraySupport.fromString("fuffa"));

        assertEquals(VariableArraySupport.AUTO, VariableArraySupport.representationOf("auto"));
        assertEquals(VariableArraySupport.FALSE, VariableArraySupport.representationOf("false"));
        assertEquals(VariableArraySupport.TRUE, VariableArraySupport.representationOf("true"));

        try {
            VariableArraySupport.representationOf("fuffa");
        } catch ( IllegalArgumentException ex ) {
            assertThat(ex.getMessage(), new StringStartsWith("Illegal variable array support representation:"));
        }

        assertEquals("auto", VariableArraySupport.AUTO.representation());
        assertEquals("false", VariableArraySupport.FALSE.representation());
        assertEquals("true", VariableArraySupport.TRUE.representation());

    }

}
