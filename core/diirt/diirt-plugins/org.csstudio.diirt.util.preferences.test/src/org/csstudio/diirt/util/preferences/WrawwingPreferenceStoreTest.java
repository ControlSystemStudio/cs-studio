/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.diirt.util.core.preferences.DIIRTPreferences;
import org.csstudio.diirt.util.preferences.DIIRTPreferencesPlugin.WrawwingPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 6 Mar 2017
 */
public class WrawwingPreferenceStoreTest {

    private static final Logger LOGGER       = Logger.getLogger(WrawwingPreferenceStoreTest.class.getName());

    private static final String BOOLEAN_PREF = "a.boolean.preference";
    private static final String CURRENT_PREF = "a.current.double.preference.only";
    private static final String DEFAULT_PREF = "a.default.double.preference.only";
    private static final String  DOUBLE_PREF = "a.double.preference";
    private static final String   FLOAT_PREF = "a.float.preference";
    private static final String INTEGER_PREF = "an.integer.preference";
    private static final String    LONG_PREF = "a.long.preference";
    private static final String  STRING_PREF = "a.string.preference";

    private DIIRTPreferences        preferences;
    private WrawwingPreferenceStore store;

    @After
    public void cleanup ( ) {
        store = null;
        preferences = null;
    }

    @Before
    public void initialize ( ) {

        preferences = new DIIRTPreferences(new TestScope());

        preferences.setDefaultBoolean(BOOLEAN_PREF, true);
        preferences.setDefaultDouble(DOUBLE_PREF, 123.0);
        preferences.setDefaultFloat(FLOAT_PREF, 234.0F);
        preferences.setDefaultInteger(INTEGER_PREF, 345);
        preferences.setDefaultLong(LONG_PREF, 456L);
        preferences.setDefaultString(STRING_PREF, "Default string value");

        preferences.setDefaultDouble(DEFAULT_PREF, 1928.0);

        preferences.setBoolean(BOOLEAN_PREF, false);
        preferences.setDouble(DOUBLE_PREF, 987.0);
        preferences.setFloat(FLOAT_PREF, 876.0F);
        preferences.setInteger(INTEGER_PREF, 765);
        preferences.setLong(LONG_PREF, 654L);
        preferences.setString(STRING_PREF, "Current string value");

        preferences.setDouble(CURRENT_PREF, 3746.0);

        store = new WrawwingPreferenceStore(preferences);

    }

    @Test
    public final void testContains ( ) {

        assertTrue(store.contains(BOOLEAN_PREF));
        assertTrue(store.contains(DOUBLE_PREF));
        assertTrue(store.contains(FLOAT_PREF));
        assertTrue(store.contains(INTEGER_PREF));
        assertTrue(store.contains(LONG_PREF));
        assertTrue(store.contains(STRING_PREF));

        assertTrue(store.contains(DEFAULT_PREF));
        assertTrue(store.contains(CURRENT_PREF));

        assertFalse(store.contains("some.preference.name"));

    }

    @Test
    public final void testGet ( ) {

        assertEquals(store.getBoolean(BOOLEAN_PREF), false);
        assertEquals(store.getDouble(DOUBLE_PREF), 987.0, 0.0001);
        assertEquals(store.getFloat(FLOAT_PREF), 876.0F, 0.0001F);
        assertEquals(store.getInt(INTEGER_PREF), 765);
        assertEquals(store.getLong(LONG_PREF), 654L);
        assertEquals(store.getString(STRING_PREF), "Current string value");

        assertEquals(store.getDouble(DEFAULT_PREF), 1928.0, 0.0001);
        assertEquals(store.getDouble(CURRENT_PREF), 3746.0, 0.0001);

        assertEquals(store.getBoolean(BOOLEAN_PREF), preferences.getBoolean(BOOLEAN_PREF));
        assertEquals(store.getDouble(DOUBLE_PREF), preferences.getDouble(DOUBLE_PREF), 0.0001);
        assertEquals(store.getFloat(FLOAT_PREF), preferences.getFloat(FLOAT_PREF), 0.0001F);
        assertEquals(store.getInt(INTEGER_PREF), preferences.getInteger(INTEGER_PREF));
        assertEquals(store.getLong(LONG_PREF), preferences.getLong(LONG_PREF));
        assertEquals(store.getString(STRING_PREF), preferences.getString(STRING_PREF));

        assertEquals(store.getDouble(DEFAULT_PREF), preferences.getDouble(DEFAULT_PREF), 0.0001);
        assertEquals(store.getDouble(CURRENT_PREF), preferences.getDouble(CURRENT_PREF), 0.0001);

        assertEquals(store.getBoolean("a.boolean"), WrawwingPreferenceStore.BOOLEAN_DEFAULT_DEFAULT);
        assertEquals(store.getDouble("a.double"), WrawwingPreferenceStore.DOUBLE_DEFAULT_DEFAULT, 0.0001);
        assertEquals(store.getFloat("a.float"), WrawwingPreferenceStore.FLOAT_DEFAULT_DEFAULT, 0.0001F);
        assertEquals(store.getInt("an.integer"), WrawwingPreferenceStore.INT_DEFAULT_DEFAULT);
        assertEquals(store.getLong("a.long"), WrawwingPreferenceStore.LONG_DEFAULT_DEFAULT);
        assertEquals(store.getString("a.string"), WrawwingPreferenceStore.STRING_DEFAULT_DEFAULT);

    }

    @Test
    public final void testGetDefaults ( ) {

        assertEquals(store.getDefaultBoolean(BOOLEAN_PREF), true);
        assertEquals(store.getDefaultDouble(DOUBLE_PREF), 123.0, 0.0001);
        assertEquals(store.getDefaultFloat(FLOAT_PREF), 234.0F, 0.0001F);
        assertEquals(store.getDefaultInt(INTEGER_PREF), 345);
        assertEquals(store.getDefaultLong(LONG_PREF), 456L);
        assertEquals(store.getDefaultString(STRING_PREF), "Default string value");

        assertEquals(store.getDefaultDouble(DEFAULT_PREF), 1928.0, 0.0001);

        assertEquals(store.getDefaultBoolean(BOOLEAN_PREF), preferences.getDefaultBoolean(BOOLEAN_PREF));
        assertEquals(store.getDefaultDouble(DOUBLE_PREF), preferences.getDefaultDouble(DOUBLE_PREF), 0.0001);
        assertEquals(store.getDefaultFloat(FLOAT_PREF), preferences.getDefaultFloat(FLOAT_PREF), 0.0001F);
        assertEquals(store.getDefaultInt(INTEGER_PREF), preferences.getDefaultInteger(INTEGER_PREF));
        assertEquals(store.getDefaultLong(LONG_PREF), preferences.getDefaultLong(LONG_PREF));
        assertEquals(store.getDefaultString(STRING_PREF), preferences.getDefaultString(STRING_PREF));

        assertEquals(store.getDefaultDouble(DEFAULT_PREF), preferences.getDefaultDouble(DEFAULT_PREF), 0.0001);

        assertEquals(store.getDefaultBoolean("a.boolean"), WrawwingPreferenceStore.BOOLEAN_DEFAULT_DEFAULT);
        assertEquals(store.getDefaultDouble("a.double"), WrawwingPreferenceStore.DOUBLE_DEFAULT_DEFAULT, 0.0001);
        assertEquals(store.getDefaultFloat("a.float"), WrawwingPreferenceStore.FLOAT_DEFAULT_DEFAULT, 0.0001F);
        assertEquals(store.getDefaultInt("an.integer"), WrawwingPreferenceStore.INT_DEFAULT_DEFAULT);
        assertEquals(store.getDefaultLong("a.long"), WrawwingPreferenceStore.LONG_DEFAULT_DEFAULT);
        assertEquals(store.getDefaultString("a.string"), WrawwingPreferenceStore.STRING_DEFAULT_DEFAULT);

        assertEquals(store.getDefaultDouble(CURRENT_PREF), WrawwingPreferenceStore.DOUBLE_DEFAULT_DEFAULT, 0.0001);

    }

    @Test
    public final void testIsDefault ( ) {

        assertFalse(store.isDefault(BOOLEAN_PREF));
        assertFalse(store.isDefault(DOUBLE_PREF));
        assertFalse(store.isDefault(FLOAT_PREF));
        assertFalse(store.isDefault(INTEGER_PREF));
        assertFalse(store.isDefault(LONG_PREF));
        assertFalse(store.isDefault(STRING_PREF));

        assertTrue(store.isDefault(DEFAULT_PREF));
        assertFalse(store.isDefault(CURRENT_PREF));

    }

    @Test
    public final void testPutValue ( ) {

        store.putValue("boolean.preference", Boolean.toString(true));

        assertTrue(store.contains("boolean.preference"));
        assertFalse(store.isDefault("boolean.preference"));
        assertEquals(true, store.getBoolean("boolean.preference"));

        store.putValue("double.preference", Double.toString(357.89));

        assertTrue(store.contains("double.preference"));
        assertFalse(store.isDefault("double.preference"));
        assertEquals(357.89, store.getDouble("double.preference"), 0.0001);

        store.putValue("float.preference", Float.toString(246.46F));

        assertTrue(store.contains("float.preference"));
        assertFalse(store.isDefault("float.preference"));
        assertEquals(246.46F, store.getFloat("float.preference"), 0.0001F);

        store.putValue("integer.preference", Integer.toString(345678));

        assertTrue(store.contains("integer.preference"));
        assertFalse(store.isDefault("integer.preference"));
        assertEquals(345678, store.getInt("integer.preference"));

        store.putValue("long.preference", Long.toString(14253647586L));

        assertTrue(store.contains("long.preference"));
        assertFalse(store.isDefault("long.preference"));
        assertEquals(14253647586L, store.getLong("long.preference"));

        store.putValue("string.preference", "some value");

        assertTrue(store.contains("string.preference"));
        assertFalse(store.isDefault("string.preference"));
        assertEquals("some value", store.getString("string.preference"));

    }

    @Test
    public void testSetBoolean ( ) {

        IPropertyChangeListener pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(BOOLEAN_PREF, e.getProperty());
            assertFalse((boolean) e.getOldValue());
            assertTrue((boolean) e.getNewValue());
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(BOOLEAN_PREF, true);
        assertTrue(store.getDefaultBoolean(BOOLEAN_PREF));
        assertTrue(store.getBoolean(BOOLEAN_PREF));
        store.removePropertyChangeListener(pcl);

        store.setDefault(BOOLEAN_PREF, false);
        assertFalse(store.getDefaultBoolean(BOOLEAN_PREF));
        assertFalse(store.getBoolean(BOOLEAN_PREF));

        pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(BOOLEAN_PREF, e.getProperty());
            assertFalse((boolean) e.getOldValue());
            assertTrue((boolean) e.getNewValue());
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(BOOLEAN_PREF, true);
        assertFalse(store.getDefaultBoolean(BOOLEAN_PREF));
        assertTrue(store.getBoolean(BOOLEAN_PREF));
        store.removePropertyChangeListener(pcl);

        pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(BOOLEAN_PREF, e.getProperty());
            fail("Property change listener should not be invoked.");
        };

        store.addPropertyChangeListener(pcl);
        store.setDefault(BOOLEAN_PREF, true);
        assertTrue(store.getDefaultBoolean(BOOLEAN_PREF));
        assertTrue(store.getBoolean(BOOLEAN_PREF));
        store.removePropertyChangeListener(pcl);

    }

    @Test
    public void testSetDouble ( ) {

        IPropertyChangeListener pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(DOUBLE_PREF, e.getProperty());
            assertEquals(987.0, (double) e.getOldValue(), 0.0001);
            assertEquals(123.0, (double) e.getNewValue(), 0.0001);
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(DOUBLE_PREF, 123.0);
        assertEquals(store.getDefaultDouble(DOUBLE_PREF), 123.0, 0.0001);
        assertEquals(store.getDouble(DOUBLE_PREF), 123.0, 0.0001);
        store.removePropertyChangeListener(pcl);

        store.setDefault(DOUBLE_PREF, 135.0);
        assertEquals(store.getDefaultDouble(DOUBLE_PREF), 135.0, 0.0001);
        assertEquals(store.getDouble(DOUBLE_PREF), 135.0, 0.0001);

        pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(DOUBLE_PREF, e.getProperty());
            assertEquals(135.0, (double) e.getOldValue(), 0.0001);
            assertEquals(468.0, (double) e.getNewValue(), 0.0001);
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(DOUBLE_PREF, 468.0);
        assertEquals(store.getDefaultDouble(DOUBLE_PREF), 135.0, 0.0001);
        assertEquals(store.getDouble(DOUBLE_PREF), 468.0, 0.0001);
        store.removePropertyChangeListener(pcl);

        pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(DOUBLE_PREF, e.getProperty());
            fail("Property change listener should not be invoked.");
        };

        store.addPropertyChangeListener(pcl);
        store.setDefault(DOUBLE_PREF, 596.0);
        assertEquals(store.getDefaultDouble(DOUBLE_PREF), 596.0, 0.0001);
        assertEquals(store.getDouble(DOUBLE_PREF), 468.0, 0.0001);
        store.removePropertyChangeListener(pcl);

    }

    @Test
    public void testSetFloat ( ) {

        IPropertyChangeListener pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(FLOAT_PREF, e.getProperty());
            assertEquals(876.0F, (float) e.getOldValue(), 0.0001F);
            assertEquals(234.0F, (float) e.getNewValue(), 0.0001F);
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(FLOAT_PREF, 234.0F);
        assertEquals(store.getDefaultFloat(FLOAT_PREF), 234.0F, 0.0001F);
        assertEquals(store.getFloat(FLOAT_PREF), 234.0F, 0.0001F);
        store.removePropertyChangeListener(pcl);

        store.setDefault(FLOAT_PREF, 135.0F);
        assertEquals(store.getDefaultFloat(FLOAT_PREF), 135.0F, 0.0001F);
        assertEquals(store.getFloat(FLOAT_PREF), 135.0F, 0.0001F);

        pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(FLOAT_PREF, e.getProperty());
            assertEquals(135.0F, (float) e.getOldValue(), 0.0001F);
            assertEquals(468.0F, (float) e.getNewValue(), 0.0001F);
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(FLOAT_PREF, 468.0F);
        assertEquals(store.getDefaultFloat(FLOAT_PREF), 135.0F, 0.0001F);
        assertEquals(store.getFloat(FLOAT_PREF), 468.0F, 0.0001F);
        store.removePropertyChangeListener(pcl);

        pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(FLOAT_PREF, e.getProperty());
            fail("Property change listener should not be invoked.");
        };

        store.addPropertyChangeListener(pcl);
        store.setDefault(FLOAT_PREF, 596.0F);
        assertEquals(store.getDefaultFloat(FLOAT_PREF), 596.0F, 0.0001F);
        assertEquals(store.getFloat(FLOAT_PREF), 468.0F, 0.0001F);
        store.removePropertyChangeListener(pcl);

    }

    @Test
    public void testSetInteger ( ) {

        IPropertyChangeListener pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
           assertEquals(INTEGER_PREF, e.getProperty());
            assertEquals(765, (int) e.getOldValue());
            assertEquals(345, (int) e.getNewValue());
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(INTEGER_PREF, 345);
        assertEquals(store.getDefaultInt(INTEGER_PREF), 345);
        assertEquals(store.getInt(INTEGER_PREF), 345);
        store.removePropertyChangeListener(pcl);

        store.setDefault(INTEGER_PREF, 135);
        assertEquals(store.getDefaultInt(INTEGER_PREF), 135);
        assertEquals(store.getInt(INTEGER_PREF), 135);

        pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(INTEGER_PREF, e.getProperty());
            assertEquals(135, (int) e.getOldValue());
            assertEquals(468, (int) e.getNewValue());
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(INTEGER_PREF, 468);
        assertEquals(store.getDefaultInt(INTEGER_PREF), 135);
        assertEquals(store.getInt(INTEGER_PREF), 468);
        store.removePropertyChangeListener(pcl);

        pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(INTEGER_PREF, e.getProperty());
            fail("Property change listener should not be invoked.");
        };

        store.addPropertyChangeListener(pcl);
        store.setDefault(INTEGER_PREF, 596);
        assertEquals(store.getDefaultInt(INTEGER_PREF), 596);
        assertEquals(store.getInt(INTEGER_PREF), 468);
        store.removePropertyChangeListener(pcl);

    }

    @Test
    public void testSetLong ( ) {

        IPropertyChangeListener pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(LONG_PREF, e.getProperty());
            assertEquals(654L, (long) e.getOldValue());
            assertEquals(456L, (long) e.getNewValue());
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(LONG_PREF, 456L);
        assertEquals(store.getDefaultLong(LONG_PREF), 456L);
        assertEquals(store.getLong(LONG_PREF), 456L);
        store.removePropertyChangeListener(pcl);

        store.setDefault(LONG_PREF, 135L);
        assertEquals(store.getDefaultLong(LONG_PREF), 135L);
        assertEquals(store.getLong(LONG_PREF), 135L);

        pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(LONG_PREF, e.getProperty());
            assertEquals(135L, (long) e.getOldValue());
            assertEquals(468L, (long) e.getNewValue());
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(LONG_PREF, 468L);
        assertEquals(store.getDefaultLong(LONG_PREF), 135L);
        assertEquals(store.getLong(LONG_PREF), 468L);
        store.removePropertyChangeListener(pcl);

        pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(LONG_PREF, e.getProperty());
            fail("Property change listener should not be invoked.");
        };

        store.addPropertyChangeListener(pcl);
        store.setDefault(LONG_PREF, 596L);
        assertEquals(store.getDefaultLong(LONG_PREF), 596L);
        assertEquals(store.getLong(LONG_PREF), 468L);
        store.removePropertyChangeListener(pcl);

    }

    @Test
    public void testSetString ( ) {

        IPropertyChangeListener pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(STRING_PREF, e.getProperty());
            assertEquals("Current string value", e.getOldValue());
            assertEquals("Default string value", e.getNewValue());
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(STRING_PREF, "Default string value");
        assertEquals(store.getDefaultString(STRING_PREF), "Default string value");
        assertEquals(store.getString(STRING_PREF), "Default string value");
        store.removePropertyChangeListener(pcl);

        store.setDefault(STRING_PREF, "Some other default value");
        assertEquals(store.getDefaultString(STRING_PREF), "Some other default value");
        assertEquals(store.getString(STRING_PREF), "Some other default value");

        pcl = e -> {
            LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(STRING_PREF, e.getProperty());
            assertEquals("Some other default value", e.getOldValue());
            assertEquals("Another current value", e.getNewValue());
        };

        store.addPropertyChangeListener(pcl);
        store.setValue(STRING_PREF, "Another current value");
        assertEquals(store.getDefaultString(STRING_PREF), "Some other default value");
        assertEquals(store.getString(STRING_PREF), "Another current value");
        store.removePropertyChangeListener(pcl);

        pcl = e -> {
           LOGGER.log(Level.INFO, "Property change listener invoked\n  name: {0}\n   old: {1}\n   new: {2}", new Object[] { e.getProperty(), e.getOldValue(), e.getNewValue() });
            assertEquals(STRING_PREF, e.getProperty());
            fail("Property change listener should not be invoked.");
        };

        store.addPropertyChangeListener(pcl);
        store.setDefault(STRING_PREF, "The last final default value");
        assertEquals(store.getDefaultString(STRING_PREF), "The last final default value");
        assertEquals(store.getString(STRING_PREF), "Another current value");
        store.removePropertyChangeListener(pcl);

    }

    @Test
    public void testSetToDefault ( ) {

        store.setToDefault(BOOLEAN_PREF);
        assertTrue(store.getBoolean(BOOLEAN_PREF));

        store.setToDefault(DOUBLE_PREF);
        assertEquals(123.0, store.getDouble(DOUBLE_PREF), 0.0001);

        store.setToDefault(FLOAT_PREF);
        assertEquals(234.0F, store.getFloat(FLOAT_PREF), 0.0001F);

        store.setToDefault(INTEGER_PREF);
        assertEquals(345, store.getInt(INTEGER_PREF));

        store.setToDefault(LONG_PREF);
        assertEquals(456L, store.getLong(LONG_PREF));

        store.setToDefault(STRING_PREF);
        assertEquals("Default string value", store.getString(STRING_PREF));

        store.setToDefault(DEFAULT_PREF);
        assertEquals(1928.0, store.getDouble(DEFAULT_PREF), 0.0001);

        store.setToDefault(CURRENT_PREF);
        assertEquals(WrawwingPreferenceStore.DOUBLE_DEFAULT_DEFAULT, store.getDouble(CURRENT_PREF), 0.0001);

    }

}
