/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.csstudio.diirt.util.preferences.pojo.ChannelAccess;
import org.csstudio.diirt.util.preferences.pojo.CompositeDataSource;
import org.csstudio.diirt.util.preferences.pojo.CompositeDataSource.DataSourceProtocol;
import org.csstudio.diirt.util.preferences.pojo.DataSourceOptions;
import org.csstudio.diirt.util.preferences.pojo.DataSourceOptions.MonitorMask;
import org.csstudio.diirt.util.preferences.pojo.DataSourceOptions.VariableArraySupport;
import org.csstudio.diirt.util.preferences.pojo.DataSources;
import org.csstudio.diirt.util.preferences.pojo.JCAContext;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
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

    public static final String[][] AVAILABLE_DATA_SOURCES         = {
        { "None",           DataSourceProtocol.none.name() },
        { "Channel Access", DataSourceProtocol.ca.name()   },
        { "File",           DataSourceProtocol.file.name() },
        { "Local",          DataSourceProtocol.loc.name()  },
        { "PV Access",      DataSourceProtocol.pva.name()  },
        { "Simulation",     DataSourceProtocol.sim.name()  },
        { "System",         DataSourceProtocol.sys.name()  },
    };
    public static final String[][] AVAILABLE_MODES                = {
        { Messages.CAPP_pureJavaRadioButton_text, Boolean.TRUE.toString()  },
        { Messages.CAPP_jcaRadioButton_text,      Boolean.FALSE.toString() },
    };
    public static final String[][] AVAILABLE_MONITOR_MASKS        = {
        { Messages.CAPP_valueRadioButton_text,   MonitorMask.VALUE.name()   },
        { Messages.CAPP_archiveRadioButton_text, MonitorMask.ARCHIVE.name() },
        { Messages.CAPP_alarmRadioButton_text,   MonitorMask.ALARM.name()   },
        { Messages.CAPP_customRadioButton_text,  MonitorMask.CUSTOM.name()  },
    };
    public static final String[][] AVAILABLE_VAR_ARRAY_SUPPORTS   = {
        { Messages.CAPP_autoRadioButton_text,  VariableArraySupport.AUTO.representation()  },
        { Messages.CAPP_trueRadioButton_text,  VariableArraySupport.TRUE.representation()  },
        { Messages.CAPP_falseRadioButton_text, VariableArraySupport.FALSE.representation() },
    };
    public static final Logger     LOGGER                         = Logger.getLogger(DIIRTPreferencesPlugin.class.getName());
    public static final String     PLATFORM_URI_PREFIX            = "platform:";
    public static final String     PREF_CONFIGURATION_DIRECTORY   = "diirt.home";
    public static final String     PREF_CA_ADDR_LIST              = "diirt.ca.addr.list";
    public static final String     PREF_CA_AUTO_ADDR_LIST         = "diirt.ca.auto.addr.list";
    public static final String     PREF_CA_BEACON_PERIOD          = "diirt.ca.beacon.period";
    public static final String     PREF_CA_CONNECTION_TIMEOUT     = "diirt.ca.connection.timeout";
    public static final String     PREF_CA_CUSTOM_MASK            = "diirt.ca.custom.mask";
    public static final String     PREF_CA_DBE_PROPERTY_SUPPORTED = "diirt.ca.dbe.property.supported";
    public static final String     PREF_CA_HONOR_ZERO_PRECISION   = "diirt.ca.honor.zero.precision";
    public static final String     PREF_CA_MAX_ARRAY_SIZE         = "diirt.ca.max.array.size";
    public static final String     PREF_CA_MONITOR_MASK           = "diirt.ca.monitor.mask";
    public static final String     PREF_CA_PURE_JAVA              = "diirt.ca.pure.java";
    public static final String     PREF_CA_REPEATER_PORT          = "diirt.ca.repeater.port";
    public static final String     PREF_CA_SERVER_PORT            = "diirt.ca.server.port";
    public static final String     PREF_CA_VALUE_RTYP_MONITOR     = "diirt.ca.value.rtyp.monitor";
    public static final String     PREF_CA_VARIABLE_LENGTH_ARRAY  = "diirt.ca.variable.length.array";
    public static final String     PREF_DS_DEFAULT                = "diirt.datasource.default";
    public static final String     PREF_DS_DELIMITER              = "diirt.datasource.delimiter";
    public static final String     PREF_FIRST_ACCESS              = "diirt.firstAccess";
    public static final String     USER_HOME_PARAMETER            = "@user.home";

    public static final String CA_DIR              = "ca";
    public static final String CA_FILE             = "ca.xml";
    public static final String CA_VERSION          = "1";
    public static final String DATASOURCES_DIR     = "datasources";
    public static final String DATASOURCES_FILE    = "datasources.xml";
    public static final String DATASOURCES_VERSION = "1";

    private static DIIRTPreferencesPlugin instance = null;

    public static DIIRTPreferencesPlugin get ( ) {
        return instance;
    }

    /**
     * Verify if the selected path is a valid location for DIIRT configuration:
     * <ul>
     * <li>path exists.</li>
     * <li><path contains the {@code datasources/datasources.xml} file.</li>
     * </ul>
     *
     * @param path The path to be verified.
     * @return {@code null} if the {@code path} is valid, otherwise the error
     *         message explaining why the given {@code path} is not valid.
     */
    public static String verifyDIIRTPath ( String path ) {

        if ( path == null ) {
            return Messages.DPH_verifyDIIRTPath_nullPath_message;
        } else if ( StringUtils.isBlank(path) ) {
            return Messages.DPH_verifyDIIRTPath_blankPath_message;
        } else if ( !Files.exists(Paths.get(path)) ) {
            return NLS.bind(Messages.DPH_verifyDIIRTPath_pathNotExists_message, path);
        } else if ( !Files.exists(Paths.get(path, DATASOURCES_DIR + File.separator + DATASOURCES_FILE)) ) {
            return NLS.bind(Messages.DPH_verifyDIIRTPath_pathNotValid_message, path);
        }

        return null;

    }

    /**
     * Return a valid path string (to be used with {@link File}'s methods) resolving
     * the given {@code path} against the {@link #PLATFORM_URI_PREFIX} protocol.
     *
     * @param path The path to be resolved.
     * @return A filename valid for {@link File} operations.
     * @throws MalformedURLException If {@code path} starts with
     *             {@link #PLATFORM_URI_PREFIX} but is not a valid URL.
     * @throws IOException If {@code path} cannot be resolved.
     * @throws NullPointerException If {@code path} is {@code null}.
     * @throws IllegalArgumentException If {@code path} is empty or blank.
     */
    public static String resolvePlatformPath ( String path )
            throws MalformedURLException, IOException, NullPointerException, IllegalArgumentException
    {

        if ( path == null ) {
            throw new NullPointerException("Null path'");
        } else if ( StringUtils.isBlank(path) ) {
            throw new IllegalArgumentException("Empty path.");
        }

        if ( path.contains(USER_HOME_PARAMETER) ) {
            path = path.replace(USER_HOME_PARAMETER, System.getProperty("user.home"));
        }

        if ( path.startsWith(PLATFORM_URI_PREFIX) ) {
            return FileLocator.resolve(new URL(path)).getPath().toString();
        } else {
            return new File(path).getCanonicalPath();
        }

    }

    public DIIRTPreferencesPlugin ( ) {
        instance = this;
    }

    /**
     * Export the current DIIRT configuration creating the relative files.
     *
     * @param parent The parent directory of the exported configuration.
     * @throws JAXBException If there were some marshalling problems.
     * @throws IOException  If an error occurred writing the configuration.
     * @throws IllegalArgumentException  If an error occurred evaluating enumerations.
     */
    public void exportConfiguration ( File parent ) throws JAXBException, IOException, IllegalArgumentException {

        if ( parent == null || !parent.exists() || !parent.isDirectory() ) {
            return;
        }

        IPreferenceStore store = getPreferenceStore();
        DataSourceProtocol dsp = DataSourceProtocol.none;

        try {
            dsp = DataSourceProtocol.valueOf(store.getString(PREF_DS_DEFAULT));
        } catch ( Exception ex ){
            LOGGER.log(Level.WARNING, MessageFormat.format("Invalid default data source [{0}].", store.getString(PREF_DS_DEFAULT)), ex);
        }

        File dsDir = new File(parent, DATASOURCES_DIR);

        FileUtils.forceMkdir(dsDir);

        DataSources ds = new DataSources(new CompositeDataSource(
            dsp,
            store.getString(PREF_DS_DELIMITER)
        ));
        JAXBContext context = JAXBContext.newInstance(DataSources.class);
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ds, new File(dsDir, DATASOURCES_FILE));

        File caDir = new File(dsDir, CA_DIR);

        FileUtils.forceMkdir(caDir);

        ChannelAccess ca = new ChannelAccess(
            new DataSourceOptions(
                store.getBoolean(DIIRTPreferencesPlugin.PREF_CA_DBE_PROPERTY_SUPPORTED),
                store.getBoolean(DIIRTPreferencesPlugin.PREF_CA_HONOR_ZERO_PRECISION),
                MonitorMask.valueOf(store.getString(DIIRTPreferencesPlugin.PREF_CA_MONITOR_MASK)),
                store.getInt(DIIRTPreferencesPlugin.PREF_CA_CUSTOM_MASK),
                store.getBoolean(DIIRTPreferencesPlugin.PREF_CA_VALUE_RTYP_MONITOR),
                VariableArraySupport.representationOf(store.getString(DIIRTPreferencesPlugin.PREF_CA_VARIABLE_LENGTH_ARRAY))
            ),
            new JCAContext(
                store.getString(DIIRTPreferencesPlugin.PREF_CA_ADDR_LIST),
                store.getBoolean(DIIRTPreferencesPlugin.PREF_CA_AUTO_ADDR_LIST),
                store.getDouble(DIIRTPreferencesPlugin.PREF_CA_BEACON_PERIOD),
                store.getDouble(DIIRTPreferencesPlugin.PREF_CA_CONNECTION_TIMEOUT),
                store.getInt(DIIRTPreferencesPlugin.PREF_CA_MAX_ARRAY_SIZE),
                store.getBoolean(DIIRTPreferencesPlugin.PREF_CA_PURE_JAVA),
                store.getInt(DIIRTPreferencesPlugin.PREF_CA_REPEATER_PORT),
                store.getInt(DIIRTPreferencesPlugin.PREF_CA_SERVER_PORT)
            )
        );

        context = JAXBContext.newInstance(ChannelAccess.class);
        marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ca, new File(caDir, CA_FILE));

    }

    @Override
    public IPreferenceStore getPreferenceStore ( ) {

        IPreferenceStore store = super.getPreferenceStore();

        synchronized ( this ) {
            if ( store.getBoolean(PREF_FIRST_ACCESS) ) {

                String diirtHome = store.getString(PREF_CONFIGURATION_DIRECTORY);

                if ( verifyDIIRTPath(diirtHome) == null ) {
                    updateDefaults(diirtHome, store);
                    updateValues(diirtHome, store);
                }

                store.setValue(PREF_FIRST_ACCESS, false);

                try {
                    InstanceScope.INSTANCE.getNode("org.csstudio.diirt.util.preferences").flush();
                } catch ( BackingStoreException ex ) {
                    LOGGER.log(Level.WARNING, "Unable to flush preference store.", ex);
                }

            }
        }

        return store;

    }

    /**
     * Updates all default values reading them from the files in the
     * given DIIRT configuration directory.
     *
     * @param confDir The DIIRT configuration directory.
     * @param store   The preference store.
     */
    public void updateDefaults ( String confDir, IPreferenceStore store ) {

        if ( StringUtils.isBlank(confDir) ) {
            LOGGER.warning("Null, empty or blank 'confDir'");
            return;
        } else {
            try {
                confDir = resolvePlatformPath(confDir);
            } catch ( NullPointerException | IllegalArgumentException | IOException ex ) {
                LOGGER.log(Level.WARNING, MessageFormat.format("Path cannot be resolved [{0}].", confDir), ex);
                return;
            }
        }

        File datasourcesDir = new File(confDir, DATASOURCES_DIR);
        File datasourcesFile = new File(datasourcesDir, DATASOURCES_FILE);

        try {
            updateDataSourcesDefaults(datasourcesFile, store);
        } catch ( IOException | JAXBException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Problems opening and/or reading file [{0}].", datasourcesFile.toString()), ex);
        }

        File caDir = new File(datasourcesDir, CA_DIR);
        File caFile = new File(caDir, CA_FILE);

        try {
            updateChannelAccessDefaults(caFile, store);
        } catch ( IOException | JAXBException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Problems opening and/or reading file [{0}].", caFile.toString()), ex);
        }

    }

    /**
     * Updates all values reading them from the files in the
     * given DIIRT configuration directory.
     *
     * @param confDir The DIIRT configuration directory.
     * @param store   The preference store.
     */
    public void updateValues ( String confDir, IPreferenceStore store ) {

        if ( StringUtils.isBlank(confDir) ) {
            LOGGER.warning("Null, empty or blank 'confDir'");
            return;
        } else {
            try {
                confDir = resolvePlatformPath(confDir);
            } catch ( NullPointerException | IllegalArgumentException | IOException ex ) {
                LOGGER.log(Level.WARNING, MessageFormat.format("Path cannot be resolved [{0}].", confDir), ex);
                return;
            }
        }

        File datasourcesDir = new File(confDir, DATASOURCES_DIR);
        File datasourcesFile = new File(datasourcesDir, DATASOURCES_FILE);

        try {
            updateDataSourcesValues(datasourcesFile, store);
        } catch ( IOException | JAXBException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Problems opening and/or reading file [{0}].", datasourcesFile.toString()), ex);
        }

        File caDir = new File(datasourcesDir, CA_DIR);
        File caFile = new File(caDir, CA_FILE);

        try {
            updateChannelAccessValues(caFile, store);
        } catch ( IOException | JAXBException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Problems opening and/or reading file [{0}].", caFile.toString()), ex);
        }

    }

    private void updateChannelAccessDefaults ( File caFile, IPreferenceStore store ) throws IOException, JAXBException {

        JAXBContext jc = JAXBContext.newInstance(ChannelAccess.class);
        Unmarshaller u = jc.createUnmarshaller();
        ChannelAccess ca = (ChannelAccess) u.unmarshal(caFile);

        if ( !CA_VERSION.equals(ca.version) ) {
            throw new IOException(MessageFormat.format("Version mismatch: expected {0}, found {1}.", CA_VERSION, ca.version));
        }

        DataSourceOptions dso = ca.dataSourceOptions;

        if ( dso != null ) {
            store.setDefault(PREF_CA_DBE_PROPERTY_SUPPORTED, dso.dbePropertySupported);
            store.setDefault(PREF_CA_HONOR_ZERO_PRECISION, dso.honorZeroPrecision);
            store.setDefault(PREF_CA_MONITOR_MASK, dso.monitorMask().name());
            store.setDefault(PREF_CA_CUSTOM_MASK, dso.monitorMaskCustomValue());
            store.setDefault(PREF_CA_VALUE_RTYP_MONITOR, dso.rtypeValueOnly);
            store.setDefault(PREF_CA_VARIABLE_LENGTH_ARRAY, ObjectUtils.defaultIfNull(dso.varArraySupported, VariableArraySupport.AUTO).name());
        }

        JCAContext jcac = ca.jcaContext;

        if ( jcac != null ) {
            store.setDefault(PREF_CA_ADDR_LIST, StringUtils.defaultString(jcac.addrList));
            store.setDefault(PREF_CA_AUTO_ADDR_LIST, jcac.autoAddrList);
            store.setDefault(PREF_CA_BEACON_PERIOD, jcac.beaconPeriod);
            store.setDefault(PREF_CA_CONNECTION_TIMEOUT, jcac.connectionTimeout);
            store.setDefault(PREF_CA_MAX_ARRAY_SIZE, jcac.maxArrayBytes);
            store.setDefault(PREF_CA_PURE_JAVA, jcac.pureJava);
            store.setDefault(PREF_CA_REPEATER_PORT, jcac.repeaterPort);
            store.setDefault(PREF_CA_SERVER_PORT, jcac.serverPort);
        }

    }

    private void updateChannelAccessValues ( File caFile, IPreferenceStore store ) throws IOException, JAXBException {

        JAXBContext jc = JAXBContext.newInstance(ChannelAccess.class);
        Unmarshaller u = jc.createUnmarshaller();
        ChannelAccess ca = (ChannelAccess) u.unmarshal(caFile);

        if ( !CA_VERSION.equals(ca.version) ) {
            throw new IOException(MessageFormat.format("Version mismatch: expected {0}, found {1}.", CA_VERSION, ca.version));
        }

        DataSourceOptions dso = ca.dataSourceOptions;

        if ( dso != null ) {
            store.setValue(PREF_CA_DBE_PROPERTY_SUPPORTED, dso.dbePropertySupported);
            store.setValue(PREF_CA_HONOR_ZERO_PRECISION, dso.honorZeroPrecision);
            store.setValue(PREF_CA_MONITOR_MASK, dso.monitorMask().name());
            store.setValue(PREF_CA_CUSTOM_MASK, dso.monitorMaskCustomValue());
            store.setValue(PREF_CA_VALUE_RTYP_MONITOR, dso.rtypeValueOnly);
            store.setValue(PREF_CA_VARIABLE_LENGTH_ARRAY, ObjectUtils.defaultIfNull(dso.varArraySupported, VariableArraySupport.AUTO).name());
        }

        JCAContext jcac = ca.jcaContext;

        if ( jcac != null ) {
            store.setValue(PREF_CA_ADDR_LIST, StringUtils.defaultString(jcac.addrList));
            store.setValue(PREF_CA_AUTO_ADDR_LIST, jcac.autoAddrList);
            store.setValue(PREF_CA_BEACON_PERIOD, jcac.beaconPeriod);
            store.setValue(PREF_CA_CONNECTION_TIMEOUT, jcac.connectionTimeout);
            store.setValue(PREF_CA_MAX_ARRAY_SIZE, jcac.maxArrayBytes);
            store.setValue(PREF_CA_PURE_JAVA, jcac.pureJava);
            store.setValue(PREF_CA_REPEATER_PORT, jcac.repeaterPort);
            store.setValue(PREF_CA_SERVER_PORT, jcac.serverPort);
        }

    }

    private void updateDataSourcesDefaults ( File datasourcesFile, IPreferenceStore store ) throws IOException, JAXBException {

        JAXBContext jc = JAXBContext.newInstance(DataSources.class);
        Unmarshaller u = jc.createUnmarshaller();
        DataSources ds = (DataSources) u.unmarshal(datasourcesFile);

        if ( !DATASOURCES_VERSION.equals(ds.version) ) {
            throw new IOException(MessageFormat.format("Version mismatch: expected {0}, found {1}.", DATASOURCES_VERSION, ds.version));
        }

        CompositeDataSource cds = ds.compositeDataSource;

        if ( cds != null ) {
            store.setDefault(PREF_DS_DEFAULT, ObjectUtils.defaultIfNull(cds.defaultDataSource, DataSourceProtocol.none).name());
            store.setDefault(PREF_DS_DELIMITER, cds.delimiter);
        }

    }

    private void updateDataSourcesValues ( File datasourcesFile, IPreferenceStore store ) throws IOException, JAXBException {

        JAXBContext jc = JAXBContext.newInstance(DataSources.class);
        Unmarshaller u = jc.createUnmarshaller();
        DataSources ds = (DataSources) u.unmarshal(datasourcesFile);

        if ( !DATASOURCES_VERSION.equals(ds.version) ) {
            throw new IOException(MessageFormat.format("Version mismatch: expected {0}, found {1}.", DATASOURCES_VERSION, ds.version));
        }

        CompositeDataSource cds = ds.compositeDataSource;

        if ( cds != null ) {
            store.setValue(PREF_DS_DEFAULT, ObjectUtils.defaultIfNull(cds.defaultDataSource, DataSourceProtocol.none).name());
            store.setValue(PREF_DS_DELIMITER, cds.delimiter);
        }

    }

}
