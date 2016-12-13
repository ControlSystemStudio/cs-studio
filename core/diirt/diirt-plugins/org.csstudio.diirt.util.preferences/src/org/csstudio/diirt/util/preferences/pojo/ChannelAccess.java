/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences.pojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.csstudio.diirt.util.preferences.DIIRTPreferencesPlugin;
import org.csstudio.diirt.util.preferences.pojo.DataSourceOptions.MonitorMask;
import org.csstudio.diirt.util.preferences.pojo.DataSourceOptions.VariableArraySupport;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Plain Old Java Object representing a {@code ca.xml} file.
 * <p>
 * This class controls the EPICS Channel Access parameters.
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 18 Nov 2016
 */
@XmlRootElement( name = "ca" )
@XmlType( name = "ChannelAccess" )
public class ChannelAccess {

    public static final String PREF_ADDR_LIST              = "diirt.ca.addr.list";
    public static final String PREF_AUTO_ADDR_LIST         = "diirt.ca.auto.addr.list";
    public static final String PREF_BEACON_PERIOD          = "diirt.ca.beacon.period";
    public static final String PREF_CONNECTION_TIMEOUT     = "diirt.ca.connection.timeout";
    public static final String PREF_CUSTOM_MASK            = "diirt.ca.custom.mask";
    public static final String PREF_DBE_PROPERTY_SUPPORTED = "diirt.ca.dbe.property.supported";
    public static final String PREF_HONOR_ZERO_PRECISION   = "diirt.ca.honor.zero.precision";
    public static final String PREF_MAX_ARRAY_SIZE         = "diirt.ca.max.array.size";
    public static final String PREF_MONITOR_MASK           = "diirt.ca.monitor.mask";
    public static final String PREF_PURE_JAVA              = "diirt.ca.pure.java";
    public static final String PREF_REPEATER_PORT          = "diirt.ca.repeater.port";
    public static final String PREF_SERVER_PORT            = "diirt.ca.server.port";
    public static final String PREF_VALUE_RTYP_MONITOR     = "diirt.ca.value.rtyp.monitor";
    public static final String PREF_VARIABLE_LENGTH_ARRAY  = "diirt.ca.variable.length.array";

    public static final String CA_DIR     = "ca";
    public static final String CA_FILE    = "ca.xml";
    public static final String CA_VERSION = "1";

    @XmlElement( name = "dataSourceOptions", nillable = true )
    public DataSourceOptions dataSourceOptions = null;

    @XmlElement( name = "jcaContext", nillable = true )
    public JCAContext jcaContext = null;

    @XmlAttribute( name = "version", required = true )
    public String version = "1";

    /**
     * Create and instance of this class loading it from the given folder.
     *
     * @param confDir The DIIRT configuration directory.
     * @return An instance of this class initialized from the given folder.
     * @throws IOException In case the given file cannot be read or the version is invalid.
     * @throws JAXBException In case the given file cannot unmashalled.
     */
    public static ChannelAccess fromFile ( File confDir ) throws IOException, JAXBException {

        File datasourcesDir = new File(confDir, DataSources.DATASOURCES_DIR);
        File channelAccessDir = new File(datasourcesDir, CA_DIR);
        File channelAccessFile = new File(channelAccessDir, CA_FILE);
        JAXBContext jc = JAXBContext.newInstance(ChannelAccess.class);
        Unmarshaller u = jc.createUnmarshaller();
        ChannelAccess ca = (ChannelAccess) u.unmarshal(channelAccessFile);

        if ( !CA_VERSION.equals(ca.version) ) {
            throw new IOException(MessageFormat.format("Version mismatch: expected {0}, found {1}.", CA_VERSION, ca.version));
        }

        if ( ca.dataSourceOptions == null ) {
           ca.dataSourceOptions = new DataSourceOptions();
        }

        if ( ca.jcaContext == null ) {
            ca.jcaContext = new JCAContext();
        }

        return ca;

    }

    /**
     * Update current defaults and values from the stored ones.
     *
     * @param store The preference store.
     */
    public static void updateValues ( IPreferenceStore store ) {
        store.setDefault(PREF_DBE_PROPERTY_SUPPORTED, store.getBoolean(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_DBE_PROPERTY_SUPPORTED)));
        store.setDefault(PREF_HONOR_ZERO_PRECISION, store.getBoolean(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_HONOR_ZERO_PRECISION)));
        store.setDefault(PREF_MONITOR_MASK, store.getString(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_MONITOR_MASK)));
        store.setDefault(PREF_CUSTOM_MASK, store.getInt(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_CUSTOM_MASK)));
        store.setDefault(PREF_VALUE_RTYP_MONITOR, store.getBoolean(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_VALUE_RTYP_MONITOR)));
        store.setDefault(PREF_VARIABLE_LENGTH_ARRAY, store.getString(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_VARIABLE_LENGTH_ARRAY)));
        store.setDefault(PREF_ADDR_LIST, store.getString(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_ADDR_LIST)));
        store.setDefault(PREF_AUTO_ADDR_LIST, store.getBoolean(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_AUTO_ADDR_LIST)));
        store.setDefault(PREF_BEACON_PERIOD, store.getDouble(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_BEACON_PERIOD)));
        store.setDefault(PREF_CONNECTION_TIMEOUT, store.getDouble(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_CONNECTION_TIMEOUT)));
        store.setDefault(PREF_MAX_ARRAY_SIZE, store.getInt(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_MAX_ARRAY_SIZE)));
        store.setDefault(PREF_PURE_JAVA, store.getBoolean(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_PURE_JAVA)));
        store.setDefault(PREF_REPEATER_PORT, store.getInt(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_REPEATER_PORT)));
        store.setDefault(PREF_SERVER_PORT, store.getInt(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_SERVER_PORT)));
    }

    public ChannelAccess () {
    }

    public ChannelAccess ( IPreferenceStore store ) {
        this(
            new DataSourceOptions(
                store.getBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED),
                store.getBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION),
                MonitorMask.fromString(store.getString(ChannelAccess.PREF_MONITOR_MASK)),
                store.getInt(ChannelAccess.PREF_CUSTOM_MASK),
                store.getBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR),
                VariableArraySupport.fromString(store.getString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY))
            ),
            new JCAContext(
                store.getString(ChannelAccess.PREF_ADDR_LIST),
                store.getBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST),
                store.getDouble(ChannelAccess.PREF_BEACON_PERIOD),
                store.getDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT),
                store.getInt(ChannelAccess.PREF_MAX_ARRAY_SIZE),
                store.getBoolean(ChannelAccess.PREF_PURE_JAVA),
                store.getInt(ChannelAccess.PREF_REPEATER_PORT),
                store.getInt(ChannelAccess.PREF_SERVER_PORT)
            )
        );
    }

    public ChannelAccess ( DataSourceOptions dataSourceOptions, JCAContext jcaContext ) {

        this();

        this.dataSourceOptions = dataSourceOptions;
        this.jcaContext = jcaContext;

    }

    /**
     * Store this instance into the given folder.
     *
     * @param confDir The current DIIRT configuration directory.
     * @throws IOException If problems occurred saving data into file or creating the folder structure.
     * @throws JAXBException In case the given instance cannot be marshalled.
     */
    public void toFile ( File confDir ) throws IOException, JAXBException {

        File dsDir = new File(confDir, DataSources.DATASOURCES_DIR);
        File caDir = new File(dsDir, CA_DIR);

        FileUtils.forceMkdir(caDir);

        JAXBContext context = JAXBContext.newInstance(ChannelAccess.class);
        Marshaller marshaller = context.createMarshaller();

        try ( Writer writer = new FileWriter(new File(caDir, CA_FILE)) ) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n");
            writer.write(MessageFormat.format("<!-- Original DIIRT home: {0} -->\n", DIIRTPreferencesPlugin.get().getDIIRTHome()));

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(this, writer);

        }

    }

    /**
     * Update all stored defaults, current defaults and values in the given store.
     *
     * @param store The preference store.
     */
    public void updateDefaultsAndValues ( IPreferenceStore store ) {

        if ( dataSourceOptions != null ) {

            String vlaName = ObjectUtils.defaultIfNull(dataSourceOptions.varArraySupported, VariableArraySupport.AUTO).representation();
            String mmName = dataSourceOptions.monitorMask().name();
            int mmcValue = dataSourceOptions.monitorMaskCustomValue();

            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_DBE_PROPERTY_SUPPORTED), dataSourceOptions.dbePropertySupported);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_HONOR_ZERO_PRECISION), dataSourceOptions.honorZeroPrecision);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_MONITOR_MASK), mmName);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_CUSTOM_MASK), mmcValue);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_VALUE_RTYP_MONITOR), dataSourceOptions.rtypeValueOnly);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_VARIABLE_LENGTH_ARRAY), vlaName);

            store.setDefault(PREF_DBE_PROPERTY_SUPPORTED, dataSourceOptions.dbePropertySupported);
            store.setDefault(PREF_HONOR_ZERO_PRECISION, dataSourceOptions.honorZeroPrecision);
            store.setDefault(PREF_MONITOR_MASK, mmName);
            store.setDefault(PREF_CUSTOM_MASK, mmcValue);
            store.setDefault(PREF_VALUE_RTYP_MONITOR, dataSourceOptions.rtypeValueOnly);
            store.setDefault(PREF_VARIABLE_LENGTH_ARRAY, vlaName);

            store.setValue(PREF_DBE_PROPERTY_SUPPORTED, dataSourceOptions.dbePropertySupported);
            store.setValue(PREF_HONOR_ZERO_PRECISION, dataSourceOptions.honorZeroPrecision);
            store.setValue(PREF_MONITOR_MASK, mmName);
            store.setValue(PREF_CUSTOM_MASK, mmcValue);
            store.setValue(PREF_VALUE_RTYP_MONITOR, dataSourceOptions.rtypeValueOnly);
            store.setValue(PREF_VARIABLE_LENGTH_ARRAY, vlaName);

        }

        if ( jcaContext != null ) {

            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_ADDR_LIST), StringUtils.defaultString(jcaContext.addrList));
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_AUTO_ADDR_LIST), jcaContext.autoAddrList);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_BEACON_PERIOD), jcaContext.beaconPeriod);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_CONNECTION_TIMEOUT), jcaContext.connectionTimeout);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_MAX_ARRAY_SIZE), jcaContext.maxArrayBytes);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_PURE_JAVA), jcaContext.pureJava);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_REPEATER_PORT), jcaContext.repeaterPort);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_SERVER_PORT), jcaContext.serverPort);

            store.setDefault(PREF_ADDR_LIST, StringUtils.defaultString(jcaContext.addrList));
            store.setDefault(PREF_AUTO_ADDR_LIST, jcaContext.autoAddrList);
            store.setDefault(PREF_BEACON_PERIOD, jcaContext.beaconPeriod);
            store.setDefault(PREF_CONNECTION_TIMEOUT, jcaContext.connectionTimeout);
            store.setDefault(PREF_MAX_ARRAY_SIZE, jcaContext.maxArrayBytes);
            store.setDefault(PREF_PURE_JAVA, jcaContext.pureJava);
            store.setDefault(PREF_REPEATER_PORT, jcaContext.repeaterPort);
            store.setDefault(PREF_SERVER_PORT, jcaContext.serverPort);

            store.setValue(PREF_ADDR_LIST, StringUtils.defaultString(jcaContext.addrList));
            store.setValue(PREF_AUTO_ADDR_LIST, jcaContext.autoAddrList);
            store.setValue(PREF_BEACON_PERIOD, jcaContext.beaconPeriod);
            store.setValue(PREF_CONNECTION_TIMEOUT, jcaContext.connectionTimeout);
            store.setValue(PREF_MAX_ARRAY_SIZE, jcaContext.maxArrayBytes);
            store.setValue(PREF_PURE_JAVA, jcaContext.pureJava);
            store.setValue(PREF_REPEATER_PORT, jcaContext.repeaterPort);
            store.setValue(PREF_SERVER_PORT, jcaContext.serverPort);

        }

    }

}
