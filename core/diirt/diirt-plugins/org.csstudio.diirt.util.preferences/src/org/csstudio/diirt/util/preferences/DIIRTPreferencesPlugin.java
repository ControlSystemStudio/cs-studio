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
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_REPEATER_PORT;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_SERVER_PORT;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_VALUE_RTYP_MONITOR;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY;
import static org.csstudio.diirt.util.core.preferences.pojo.DataSources.PREF_DEFAULT;
import static org.csstudio.diirt.util.core.preferences.pojo.DataSources.PREF_DELIMITER;

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

    private static DIIRTPreferencesPlugin instance = null;

    private final PreferenceStore cancelStore = new PreferenceStore();
    private IPreferenceStore      prefStore   = new WrawwingPreferenceStore(DIIRTPreferences.get());

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
        return prefStore;
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

        performFlush();

    }

    /**
     * Flush the backing store.
     */
    public void performFlush ( ) {
        try {
            DIIRTPreferences.get().flush();
        } catch ( BackingStoreException ex ) {
            LOGGER.log(Level.WARNING, "Unable to flush tge preferences backing store.", ex);
        }
    }

    /**
     * An {@link IPreferenceStore} wrapped around {@link DIIRTPreferences}.
     */
    static class WrawwingPreferenceStore implements IPreferenceStore {

        private final ListenerList propertyChangeListeners = new ListenerList();
        private final DIIRTPreferences preferences;

        WrawwingPreferenceStore ( DIIRTPreferences preferences ) {
            this.preferences = preferences;
        }

        @Override
        public void addPropertyChangeListener ( IPropertyChangeListener listener ) {
            propertyChangeListeners.add(listener);
        }

        @Override
        public boolean contains ( String name ) {
            return preferences.contains(name);
        }

        @Override
        public void firePropertyChangeEvent ( String name, Object oldValue, Object newValue ) {

            PropertyChangeEvent e = new PropertyChangeEvent(this, name, oldValue, newValue);

            for ( Object pcl : propertyChangeListeners.getListeners() ) {
                ((IPropertyChangeListener) pcl).propertyChange(e);
            }

        }

        @Override
        public boolean getBoolean ( String name ) {
            return preferences.getBoolean(name);
        }

        @Override
        public boolean getDefaultBoolean ( String name ) {
            return preferences.getDefaultBoolean(name);
        }

        @Override
        public double getDefaultDouble ( String name ) {
            return preferences.getDefaultDouble(name);
        }

        @Override
        public float getDefaultFloat ( String name ) {
            return preferences.getDefaultFloat(name);
        }

        @Override
        public int getDefaultInt ( String name ) {
            return preferences.getDefaultInteger(name);
        }

        @Override
        public long getDefaultLong ( String name ) {
            return preferences.getDefaultLong(name);
        }

        @Override
        public String getDefaultString ( String name ) {
            return preferences.getDefaultString(name);
        }

        @Override
        public double getDouble ( String name ) {
            return preferences.getDouble(name);
        }

        @Override
        public float getFloat ( String name ) {
            return preferences.getFloat(name);
        }

        @Override
        public int getInt ( String name ) {
            return preferences.getInteger(name);
        }

        @Override
        public long getLong ( String name ) {
            return preferences.getLong(name);
        }

        @Override
        public String getString ( String name ) {
            return preferences.getString(name);
        }

        @Override
        public boolean isDefault ( String name ) {
            if ( contains(name) ) {
                return ( preferences.getString(name).equals(preferences.getDefaultString(name)) );
            } else {
                return false;
            }
        }

        @Override
        public boolean needsSaving ( ) {
            return false;
        }

        @Override
        public void putValue ( String name, String value ) {
            preferences.setString(name, value);
        }

        @Override
        public void removePropertyChangeListener ( IPropertyChangeListener listener ) {
            propertyChangeListeners.remove(listener);
        }

        @Override
        public void setDefault ( String name, boolean value ) {

            boolean valueWasDefault = ( preferences.getBoolean(name) == preferences.getDefaultBoolean(name) );

            preferences.setDefaultBoolean(name, value);

            if ( valueWasDefault ) {
                setValue(name, value);
            }

        }

        @Override
        public void setDefault ( String name, double value ) {

            boolean valueWasDefault = ( preferences.getDouble(name) == preferences.getDefaultDouble(name) );

            preferences.setDefaultDouble(name, value);

            if ( valueWasDefault ) {
                setValue(name, value);
            }

        }

        @Override
        public void setDefault ( String name, float value ) {

            boolean valueWasDefault = ( preferences.getFloat(name) == preferences.getDefaultFloat(name) );

            preferences.setDefaultFloat(name, value);

            if ( valueWasDefault ) {
                setValue(name, value);
            }

        }

        @Override
        public void setDefault ( String name, int value ) {

            boolean valueWasDefault = ( preferences.getInteger(name) == preferences.getDefaultInteger(name) );

            preferences.setDefaultInteger(name, value);

            if ( valueWasDefault ) {
                setValue(name, value);
            }

        }

        @Override
        public void setDefault ( String name, long value ) {

            boolean valueWasDefault = ( preferences.getLong(name) == preferences.getDefaultLong(name) );

            preferences.setDefaultLong(name, value);

            if ( valueWasDefault ) {
                setValue(name, value);
            }

        }

        @Override
        public void setDefault ( String name, String value ) {

            boolean valueWasDefault = ( preferences.getString(name) == preferences.getDefaultString(name) );

            preferences.setDefaultString(name, value);

            if ( valueWasDefault ) {
                setValue(name, value);
            }

        }

        @Override
        public void setToDefault ( String name ) {
            setValue(name, preferences.getDefaultString(name));
        }

        @Override
        public void setValue ( String name, boolean newValue ) {

            boolean oldValue = getBoolean(name);

            if ( oldValue != newValue ) {
                preferences.setBoolean(name, newValue);
                firePropertyChangeEvent(name, oldValue, newValue);
            }

        }

        @Override
        public void setValue ( String name, double newValue ) {

            double oldValue = getDouble(name);

            if ( oldValue != newValue ) {
                preferences.setDouble(name, newValue);
                firePropertyChangeEvent(name, oldValue, newValue);
            }

        }

        @Override
        public void setValue ( String name, float newValue ) {

            float oldValue = getFloat(name);

            if ( oldValue != newValue ) {
                preferences.setFloat(name, newValue);
                firePropertyChangeEvent(name, oldValue, newValue);
            }

        }

        @Override
        public void setValue ( String name, int newValue ) {

            int oldValue = getInt(name);

            if ( oldValue != newValue ) {
                preferences.setInteger(name, newValue);
                firePropertyChangeEvent(name, oldValue, newValue);
            }

        }

        @Override
        public void setValue ( String name, long newValue ) {

            long oldValue = getLong(name);

            if ( oldValue != newValue ) {
                preferences.setLong(name, newValue);
                firePropertyChangeEvent(name, oldValue, newValue);
            }

        }

        @Override
        public void setValue ( String name, String newValue ) {

            String oldValue = getString(name);

            if ( !oldValue.equals(newValue) ) {
                preferences.setString(name, newValue);
                firePropertyChangeEvent(name, oldValue, newValue);
            }

        }

    }

}
