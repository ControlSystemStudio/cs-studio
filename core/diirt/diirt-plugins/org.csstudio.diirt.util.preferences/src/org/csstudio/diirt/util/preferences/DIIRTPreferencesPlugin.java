/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_ADDR_LIST;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_AUTO_ADDR_LIST;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_BEACON_PERIOD;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_CONNECTION_TIMEOUT;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_CUSTOM_MASK;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_HONOR_ZERO_PRECISION;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_MAX_ARRAY_SIZE;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_MONITOR_MASK;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_PURE_JAVA;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_REPEATER_PORT;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_SERVER_PORT;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_VALUE_RTYP_MONITOR;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY;
import static org.csstudio.diirt.util.core.preferences.pojo.DataSources.PREF_DEFAULT;
import static org.csstudio.diirt.util.core.preferences.pojo.DataSources.PREF_DELIMITER;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.diirt.util.core.preferences.DIIRTPreferences;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.service.prefs.BackingStoreException;


/**
 * Handle the DIIRT preferences, reading them from the configuration files
 * and applying the overrides.
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 14 Nov 2016
 */
public class DIIRTPreferencesPlugin extends AbstractUIPlugin {

    public static final Logger LOGGER = Logger.getLogger(DIIRTPreferencesPlugin.class.getName());

    private static DIIRTPreferencesPlugin instance    = null;
    private static boolean                firstAccess = true;

    private final PreferenceStore cancelStore = new PreferenceStore();
    private IPreferenceStore      prefStore   = null;

    public static void copyChannelAccess ( IPreferenceStore source, DIIRTPreferences destination ) {

        destination.setDefaultBoolean(PREF_DBE_PROPERTY_SUPPORTED, source.getDefaultBoolean(PREF_DBE_PROPERTY_SUPPORTED));
        destination.setDefaultBoolean(PREF_HONOR_ZERO_PRECISION,   source.getDefaultBoolean(PREF_HONOR_ZERO_PRECISION));
        destination.setDefaultString(PREF_MONITOR_MASK,            source.getDefaultString(PREF_MONITOR_MASK));
        destination.setDefaultInteger(PREF_CUSTOM_MASK,            source.getDefaultInt(PREF_CUSTOM_MASK));
        destination.setDefaultBoolean(PREF_VALUE_RTYP_MONITOR,     source.getDefaultBoolean(PREF_VALUE_RTYP_MONITOR));
        destination.setDefaultString(PREF_VARIABLE_LENGTH_ARRAY,   source.getDefaultString(PREF_VARIABLE_LENGTH_ARRAY));
        destination.setDefaultString(PREF_ADDR_LIST,               source.getDefaultString(PREF_ADDR_LIST));
        destination.setDefaultBoolean(PREF_AUTO_ADDR_LIST,         source.getDefaultBoolean(PREF_AUTO_ADDR_LIST));
        destination.setDefaultDouble(PREF_BEACON_PERIOD,           source.getDefaultDouble(PREF_BEACON_PERIOD));
        destination.setDefaultDouble(PREF_CONNECTION_TIMEOUT,      source.getDefaultDouble(PREF_CONNECTION_TIMEOUT));
        destination.setDefaultInteger(PREF_MAX_ARRAY_SIZE,         source.getDefaultInt(PREF_MAX_ARRAY_SIZE));
        destination.setDefaultBoolean(PREF_PURE_JAVA,              source.getDefaultBoolean(PREF_PURE_JAVA));
        destination.setDefaultInteger(PREF_REPEATER_PORT,          source.getDefaultInt(PREF_REPEATER_PORT));
        destination.setDefaultInteger(PREF_SERVER_PORT,            source.getDefaultInt(PREF_SERVER_PORT));

        destination.setBoolean(PREF_DBE_PROPERTY_SUPPORTED, source.getBoolean(PREF_DBE_PROPERTY_SUPPORTED));
        destination.setBoolean(PREF_HONOR_ZERO_PRECISION,   source.getBoolean(PREF_HONOR_ZERO_PRECISION));
        destination.setString(PREF_MONITOR_MASK,            source.getString(PREF_MONITOR_MASK));
        destination.setInteger(PREF_CUSTOM_MASK,            source.getInt(PREF_CUSTOM_MASK));
        destination.setBoolean(PREF_VALUE_RTYP_MONITOR,     source.getBoolean(PREF_VALUE_RTYP_MONITOR));
        destination.setString(PREF_VARIABLE_LENGTH_ARRAY,   source.getString(PREF_VARIABLE_LENGTH_ARRAY));
        destination.setString(PREF_ADDR_LIST,               source.getString(PREF_ADDR_LIST));
        destination.setBoolean(PREF_AUTO_ADDR_LIST,         source.getBoolean(PREF_AUTO_ADDR_LIST));
        destination.setDouble(PREF_BEACON_PERIOD,           source.getDouble(PREF_BEACON_PERIOD));
        destination.setDouble(PREF_CONNECTION_TIMEOUT,      source.getDouble(PREF_CONNECTION_TIMEOUT));
        destination.setInteger(PREF_MAX_ARRAY_SIZE,         source.getInt(PREF_MAX_ARRAY_SIZE));
        destination.setBoolean(PREF_PURE_JAVA,              source.getBoolean(PREF_PURE_JAVA));
        destination.setInteger(PREF_REPEATER_PORT,          source.getInt(PREF_REPEATER_PORT));
        destination.setInteger(PREF_SERVER_PORT,            source.getInt(PREF_SERVER_PORT));

    }

    public static void copyChannelAccess ( DIIRTPreferences source, IPreferenceStore destination ) {

        destination.setDefault(PREF_DBE_PROPERTY_SUPPORTED, source.getDefaultBoolean(PREF_DBE_PROPERTY_SUPPORTED));
        destination.setDefault(PREF_HONOR_ZERO_PRECISION,   source.getDefaultBoolean(PREF_HONOR_ZERO_PRECISION));
        destination.setDefault(PREF_MONITOR_MASK,           source.getDefaultString(PREF_MONITOR_MASK));
        destination.setDefault(PREF_CUSTOM_MASK,            source.getDefaultInteger(PREF_CUSTOM_MASK));
        destination.setDefault(PREF_VALUE_RTYP_MONITOR,     source.getDefaultBoolean(PREF_VALUE_RTYP_MONITOR));
        destination.setDefault(PREF_VARIABLE_LENGTH_ARRAY,  source.getDefaultString(PREF_VARIABLE_LENGTH_ARRAY));
        destination.setDefault(PREF_ADDR_LIST,              source.getDefaultString(PREF_ADDR_LIST));
        destination.setDefault(PREF_AUTO_ADDR_LIST,         source.getDefaultBoolean(PREF_AUTO_ADDR_LIST));
        destination.setDefault(PREF_BEACON_PERIOD,          source.getDefaultDouble(PREF_BEACON_PERIOD));
        destination.setDefault(PREF_CONNECTION_TIMEOUT,     source.getDefaultDouble(PREF_CONNECTION_TIMEOUT));
        destination.setDefault(PREF_MAX_ARRAY_SIZE,         source.getDefaultInteger(PREF_MAX_ARRAY_SIZE));
        destination.setDefault(PREF_PURE_JAVA,              source.getDefaultBoolean(PREF_PURE_JAVA));
        destination.setDefault(PREF_REPEATER_PORT,          source.getDefaultInteger(PREF_REPEATER_PORT));
        destination.setDefault(PREF_SERVER_PORT,            source.getDefaultInteger(PREF_SERVER_PORT));

        destination.setValue(PREF_DBE_PROPERTY_SUPPORTED, source.getBoolean(PREF_DBE_PROPERTY_SUPPORTED));
        destination.setValue(PREF_HONOR_ZERO_PRECISION,   source.getBoolean(PREF_HONOR_ZERO_PRECISION));
        destination.setValue(PREF_MONITOR_MASK,           source.getString(PREF_MONITOR_MASK));
        destination.setValue(PREF_CUSTOM_MASK,            source.getInteger(PREF_CUSTOM_MASK));
        destination.setValue(PREF_VALUE_RTYP_MONITOR,     source.getBoolean(PREF_VALUE_RTYP_MONITOR));
        destination.setValue(PREF_VARIABLE_LENGTH_ARRAY,  source.getString(PREF_VARIABLE_LENGTH_ARRAY));
        destination.setValue(PREF_ADDR_LIST,              source.getString(PREF_ADDR_LIST));
        destination.setValue(PREF_AUTO_ADDR_LIST,         source.getBoolean(PREF_AUTO_ADDR_LIST));
        destination.setValue(PREF_BEACON_PERIOD,          source.getDouble(PREF_BEACON_PERIOD));
        destination.setValue(PREF_CONNECTION_TIMEOUT,     source.getDouble(PREF_CONNECTION_TIMEOUT));
        destination.setValue(PREF_MAX_ARRAY_SIZE,         source.getInteger(PREF_MAX_ARRAY_SIZE));
        destination.setValue(PREF_PURE_JAVA,              source.getBoolean(PREF_PURE_JAVA));
        destination.setValue(PREF_REPEATER_PORT,          source.getInteger(PREF_REPEATER_PORT));
        destination.setValue(PREF_SERVER_PORT,            source.getInteger(PREF_SERVER_PORT));

    }

    public static void copyDataSources ( IPreferenceStore source, DIIRTPreferences destination ) {

        destination.setDefaultString(PREF_DEFAULT,   source.getDefaultString(PREF_DEFAULT));
        destination.setDefaultString(PREF_DELIMITER, source.getDefaultString(PREF_DELIMITER));

        destination.setString(PREF_DEFAULT,   source.getString(PREF_DEFAULT));
        destination.setString(PREF_DELIMITER, source.getString(PREF_DELIMITER));

    }

    public static void copyDataSources ( DIIRTPreferences source, IPreferenceStore destination ) {

        destination.setDefault(PREF_DEFAULT,   source.getDefaultString(PREF_DEFAULT));
        destination.setDefault(PREF_DELIMITER, source.getDefaultString(PREF_DELIMITER));

        destination.setValue(PREF_DEFAULT,   source.getString(PREF_DEFAULT));
        destination.setValue(PREF_DELIMITER, source.getString(PREF_DELIMITER));

    }

    public static DIIRTPreferencesPlugin get ( ) {
        return instance;
    }

    public DIIRTPreferencesPlugin ( ) {
        instance = this;
    }

    @Override
    public IPreferenceStore getPreferenceStore ( ) {

        if ( prefStore == null ) {
            prefStore = new InMemoryPreferenceStore(DIIRTPreferences.QUALIFIER);
        }

        IPreferenceStore store = prefStore;

        if ( firstAccess && store != null ) {

            DIIRTPreferences dp = DIIRTPreferences.get();

            //  Can be null when getPreferenceStore() is automatically called
            //  while DIIRTPreferences is constructed.
            if ( dp != null ) {

                copyDataSources(dp, store);
                copyChannelAccess(dp, store);

                firstAccess = false;

            }

        }

        return store;

    }

    /**
     * Initialize the cancel store copying in it the relevant information from
     * the preference store.
     */
    public void initializeCancelStore ( ) {
        copyDataSources(DIIRTPreferences.get(), cancelStore);
        copyChannelAccess(DIIRTPreferences.get(), cancelStore);
    }

    /**
     * Perform cancel operation copying from the cancel store the relevant
     * information into the preference store.
     */
    public void performCancel ( ) {

        copyDataSources(cancelStore, DIIRTPreferences.get());
        copyChannelAccess(cancelStore, DIIRTPreferences.get());

        try {
            DIIRTPreferences.get().flush();
        } catch ( BackingStoreException ex ) {
            LOGGER.log(Level.WARNING, "Unable to flush tge preferences backing store.", ex);
        }

    }

    /**
     * An in-memory implementation of an {@link IPreferenceStore}.
     */
    private class InMemoryPreferenceStore implements IPreferenceStore {

        private final String currentPrefix;
        private final String defaultPrefix;
        private final Map<String, Object> store = Collections.synchronizedMap(new TreeMap<>());
        private final ListenerList<IPropertyChangeListener> propertyChangeListeners = new ListenerList<>();

        InMemoryPreferenceStore ( String qualifier ) {
            this.currentPrefix = qualifier + ".current.";
            this.defaultPrefix = qualifier + ".default.";
        }

        @Override
        public void addPropertyChangeListener ( IPropertyChangeListener listener ) {
            propertyChangeListeners.add(listener);
        }

        @Override
        public boolean contains ( String name ) {
            return store.containsKey(currentName(name));
        }

        @Override
        public void firePropertyChangeEvent ( String name, Object oldValue, Object newValue ) {

            PropertyChangeEvent e = new PropertyChangeEvent(this, name, oldValue, newValue);

            for ( IPropertyChangeListener pcl : propertyChangeListeners ) {
                pcl.propertyChange(e);
            }

        }

        @Override
        public boolean getBoolean ( String name ) {

            Object value = store.get(currentName(name));

            if ( value == null ) {
                value = store.get(defaultName(name));
            }

            if ( value == null ) {
                return BOOLEAN_DEFAULT_DEFAULT;
            } else {
                try {
                    return (boolean) value;
                } catch ( Exception ex ) {
                    return BOOLEAN_DEFAULT_DEFAULT;
                }
            }

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultBoolean(java.lang.String)
         */
        @Override
        public boolean getDefaultBoolean ( String arg0 ) {
            // TODO Auto-generated method stub
            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultDouble(java.lang.String)
         */
        @Override
        public double getDefaultDouble ( String arg0 ) {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultFloat(java.lang.String)
         */
        @Override
        public float getDefaultFloat ( String arg0 ) {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultInt(java.lang.String)
         */
        @Override
        public int getDefaultInt ( String arg0 ) {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultLong(java.lang.String)
         */
        @Override
        public long getDefaultLong ( String arg0 ) {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultString(java.lang.String)
         */
        @Override
        public String getDefaultString ( String arg0 ) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#getDouble(java.lang.String)
         */
        @Override
        public double getDouble ( String arg0 ) {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#getFloat(java.lang.String)
         */
        @Override
        public float getFloat ( String arg0 ) {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#getInt(java.lang.String)
         */
        @Override
        public int getInt ( String arg0 ) {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#getLong(java.lang.String)
         */
        @Override
        public long getLong ( String arg0 ) {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#getString(java.lang.String)
         */
        @Override
        public String getString ( String arg0 ) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#isDefault(java.lang.String)
         */
        @Override
        public boolean isDefault ( String arg0 ) {
            // TODO Auto-generated method stub
            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#needsSaving()
         */
        @Override
        public boolean needsSaving ( ) {
            // TODO Auto-generated method stub
            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#putValue(java.lang.String, java.lang.String)
         */
        @Override
        public void putValue ( String arg0, String arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#removePropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
         */
        @Override
        public void removePropertyChangeListener ( IPropertyChangeListener arg0 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, double)
         */
        @Override
        public void setDefault ( String arg0, double arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, float)
         */
        @Override
        public void setDefault ( String arg0, float arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, int)
         */
        @Override
        public void setDefault ( String arg0, int arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, long)
         */
        @Override
        public void setDefault ( String arg0, long arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, java.lang.String)
         */
        @Override
        public void setDefault ( String arg0, String arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, boolean)
         */
        @Override
        public void setDefault ( String arg0, boolean arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setToDefault(java.lang.String)
         */
        @Override
        public void setToDefault ( String arg0 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, double)
         */
        @Override
        public void setValue ( String arg0, double arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, float)
         */
        @Override
        public void setValue ( String arg0, float arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, int)
         */
        @Override
        public void setValue ( String arg0, int arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, long)
         */
        @Override
        public void setValue ( String arg0, long arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, java.lang.String)
         */
        @Override
        public void setValue ( String arg0, String arg1 ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, boolean)
         */
        @Override
        public void setValue ( String arg0, boolean arg1 ) {
            // TODO Auto-generated method stub

        }

        private String currentName ( String name ) {
            return currentPrefix + name;
        }

        private String defaultName ( String name ) {
            return defaultPrefix + name;
        }

    }

}
