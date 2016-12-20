/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences.pojo;

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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.csstudio.diirt.util.core.preferences.DIIRTPreferences;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.MonitorMask;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.VariableArraySupport;

/**
 * Plain Old Java Object representing a {@code ca.xml} file.
 * <p>
 * This class controls the EPICS Channel Access parameters.
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 18 Nov 2016
 * @see DataSourceOptions
 * @see JCAContext
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
     * Copy the {@link ChannelAccess} parameters from a source set to a
     * destination one.
     *
     * @param source The source preference store.
     * @param destination The destination preference store.
     */
    public static void copy ( DIIRTPreferences source, DIIRTPreferences destination ) {

        destination.setDefaultBoolean(PREF_DBE_PROPERTY_SUPPORTED, source.getDefaultBoolean(PREF_DBE_PROPERTY_SUPPORTED));
        destination.setDefaultBoolean(PREF_HONOR_ZERO_PRECISION,   source.getDefaultBoolean(PREF_HONOR_ZERO_PRECISION));
        destination.setDefaultString(PREF_MONITOR_MASK,            source.getDefaultString(PREF_MONITOR_MASK));
        destination.setDefaultInteger(PREF_CUSTOM_MASK,            source.getDefaultInteger(PREF_CUSTOM_MASK));
        destination.setDefaultBoolean(PREF_VALUE_RTYP_MONITOR,     source.getDefaultBoolean(PREF_VALUE_RTYP_MONITOR));
        destination.setDefaultString(PREF_VARIABLE_LENGTH_ARRAY,   source.getDefaultString(PREF_VARIABLE_LENGTH_ARRAY));
        destination.setDefaultString(PREF_ADDR_LIST,               source.getDefaultString(PREF_ADDR_LIST));
        destination.setDefaultBoolean(PREF_AUTO_ADDR_LIST,         source.getDefaultBoolean(PREF_AUTO_ADDR_LIST));
        destination.setDefaultDouble(PREF_BEACON_PERIOD,           source.getDefaultDouble(PREF_BEACON_PERIOD));
        destination.setDefaultDouble(PREF_CONNECTION_TIMEOUT,      source.getDefaultDouble(PREF_CONNECTION_TIMEOUT));
        destination.setDefaultInteger(PREF_MAX_ARRAY_SIZE,         source.getDefaultInteger(PREF_MAX_ARRAY_SIZE));
        destination.setDefaultBoolean(PREF_PURE_JAVA,              source.getDefaultBoolean(PREF_PURE_JAVA));
        destination.setDefaultInteger(PREF_REPEATER_PORT,          source.getDefaultInteger(PREF_REPEATER_PORT));
        destination.setDefaultInteger(PREF_SERVER_PORT,            source.getDefaultInteger(PREF_SERVER_PORT));

        destination.setBoolean(PREF_DBE_PROPERTY_SUPPORTED, source.getBoolean(PREF_DBE_PROPERTY_SUPPORTED));
        destination.setBoolean(PREF_HONOR_ZERO_PRECISION,   source.getBoolean(PREF_HONOR_ZERO_PRECISION));
        destination.setString(PREF_MONITOR_MASK,            source.getString(PREF_MONITOR_MASK));
        destination.setInteger(PREF_CUSTOM_MASK,            source.getInteger(PREF_CUSTOM_MASK));
        destination.setBoolean(PREF_VALUE_RTYP_MONITOR,     source.getBoolean(PREF_VALUE_RTYP_MONITOR));
        destination.setString(PREF_VARIABLE_LENGTH_ARRAY,   source.getString(PREF_VARIABLE_LENGTH_ARRAY));
        destination.setString(PREF_ADDR_LIST,               source.getString(PREF_ADDR_LIST));
        destination.setBoolean(PREF_AUTO_ADDR_LIST,         source.getBoolean(PREF_AUTO_ADDR_LIST));
        destination.setDouble(PREF_BEACON_PERIOD,           source.getDouble(PREF_BEACON_PERIOD));
        destination.setDouble(PREF_CONNECTION_TIMEOUT,      source.getDouble(PREF_CONNECTION_TIMEOUT));
        destination.setInteger(PREF_MAX_ARRAY_SIZE,         source.getInteger(PREF_MAX_ARRAY_SIZE));
        destination.setBoolean(PREF_PURE_JAVA,              source.getBoolean(PREF_PURE_JAVA));
        destination.setInteger(PREF_REPEATER_PORT,          source.getInteger(PREF_REPEATER_PORT));
        destination.setInteger(PREF_SERVER_PORT,            source.getInteger(PREF_SERVER_PORT));

    }

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
     * Create an instance of this class with default initialization.
     */
    public ChannelAccess () {
    }

    /**
     * Create an instance of this class initialized using the given preferences
     * set.
     *
     * @param preferencesSet The preferences set used to initialize this object.
     */
    public ChannelAccess ( DIIRTPreferences preferencesSet ) {
        this(
            new DataSourceOptions(
                preferencesSet.getBoolean(PREF_DBE_PROPERTY_SUPPORTED),
                preferencesSet.getBoolean(PREF_HONOR_ZERO_PRECISION),
                MonitorMask.fromString(preferencesSet.getString(PREF_MONITOR_MASK)),
                preferencesSet.getInteger(PREF_CUSTOM_MASK),
                preferencesSet.getBoolean(PREF_VALUE_RTYP_MONITOR),
                VariableArraySupport.fromString(preferencesSet.getString(PREF_VARIABLE_LENGTH_ARRAY))
            ),
            new JCAContext(
                preferencesSet.getString(PREF_ADDR_LIST),
                preferencesSet.getBoolean(PREF_AUTO_ADDR_LIST),
                preferencesSet.getDouble(PREF_BEACON_PERIOD),
                preferencesSet.getDouble(PREF_CONNECTION_TIMEOUT),
                preferencesSet.getInteger(PREF_MAX_ARRAY_SIZE),
                preferencesSet.getBoolean(PREF_PURE_JAVA),
                preferencesSet.getInteger(PREF_REPEATER_PORT),
                preferencesSet.getInteger(PREF_SERVER_PORT)
            )
        );
    }

    public ChannelAccess ( DataSourceOptions dataSourceOptions, JCAContext jcaContext ) {

        this();

        this.dataSourceOptions = dataSourceOptions;
        this.jcaContext = jcaContext;

    }

    @Override
    public int hashCode ( ) {
        return new HashCodeBuilder(29, 821)
            .append(dataSourceOptions)
            .append(jcaContext)
            .append(version)
            .toHashCode();
    }

    @Override
    public boolean equals ( Object obj ) {

        if ( obj == null ) {
            return false;
        } else if ( obj == this ) {
            return true;
        } else if ( obj.getClass() != getClass() ) {
            return false;
        }

        ChannelAccess ca = (ChannelAccess) obj;

        return new EqualsBuilder()
            .append(dataSourceOptions, ca.dataSourceOptions)
            .append(jcaContext,        ca.jcaContext)
            .append(version,           ca.version)
            .isEquals();

    }

    @Override
    public String toString ( ) {
        return new ToStringBuilder(this)
            .append("dataSourceOptions", dataSourceOptions)
            .append("jcaContext",        jcaContext)
            .append("version",           version)
            .toString();
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

//  TODO: CR: uncomment when understood how to run IT tests.
//            String diirtHome = DIIRTPreferences.get().getDIIRTHome();
//
//            if ( StringUtils.isNoneBlank(diirtHome) ) {
//                writer.write(MessageFormat.format("<!-- Original DIIRT home: {0} -->\n", diirtHome));
//            }

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(this, writer);

        }

    }

    /**
     * Update defaults and values in the given preferences set.
     *
     * @param preferencesSet The preferences set.
     */
    public void updateDefaultsAndValues ( DIIRTPreferences preferencesSet ) {

        if ( dataSourceOptions != null ) {

            String vlaName = dataSourceOptions.variableArraySupport().representation();
            String mmName = dataSourceOptions.monitorMask().name();
            int mmcValue = dataSourceOptions.monitorMaskCustomValue();

            preferencesSet.setDefaultBoolean(PREF_DBE_PROPERTY_SUPPORTED, dataSourceOptions.dbePropertySupported);
            preferencesSet.setDefaultBoolean(PREF_HONOR_ZERO_PRECISION,   dataSourceOptions.honorZeroPrecision);
            preferencesSet.setDefaultString(PREF_MONITOR_MASK,            mmName);
            preferencesSet.setDefaultInteger(PREF_CUSTOM_MASK,            mmcValue);
            preferencesSet.setDefaultBoolean(PREF_VALUE_RTYP_MONITOR,     dataSourceOptions.rtypeValueOnly);
            preferencesSet.setDefaultString(PREF_VARIABLE_LENGTH_ARRAY,   vlaName);

        }

        if ( jcaContext != null ) {

            preferencesSet.setDefaultString(PREF_ADDR_LIST,          StringUtils.defaultString(jcaContext.addrList));
            preferencesSet.setDefaultBoolean(PREF_AUTO_ADDR_LIST,    jcaContext.autoAddrList);
            preferencesSet.setDefaultDouble(PREF_BEACON_PERIOD,      jcaContext.beaconPeriod);
            preferencesSet.setDefaultDouble(PREF_CONNECTION_TIMEOUT, jcaContext.connectionTimeout);
            preferencesSet.setDefaultInteger(PREF_MAX_ARRAY_SIZE,    jcaContext.maxArrayBytes);
            preferencesSet.setDefaultBoolean(PREF_PURE_JAVA,         jcaContext.pureJava);
            preferencesSet.setDefaultInteger(PREF_REPEATER_PORT,     jcaContext.repeaterPort);
            preferencesSet.setDefaultInteger(PREF_SERVER_PORT,       jcaContext.serverPort);

        }

        updateValues(preferencesSet);

    }

    /**
     * Update values in the given preferences set.
     *
     * @param preferencesSet The preferences set.
     */
    public void updateValues ( DIIRTPreferences preferencesSet ) {

        if ( dataSourceOptions != null ) {

            String vlaName = dataSourceOptions.variableArraySupport().representation();
            String mmName = dataSourceOptions.monitorMask().name();
            int mmcValue = dataSourceOptions.monitorMaskCustomValue();

            preferencesSet.setBoolean(PREF_DBE_PROPERTY_SUPPORTED, dataSourceOptions.dbePropertySupported);
            preferencesSet.setBoolean(PREF_HONOR_ZERO_PRECISION, dataSourceOptions.honorZeroPrecision);
            preferencesSet.setString(PREF_MONITOR_MASK, mmName);
            preferencesSet.setInteger(PREF_CUSTOM_MASK, mmcValue);
            preferencesSet.setBoolean(PREF_VALUE_RTYP_MONITOR, dataSourceOptions.rtypeValueOnly);
            preferencesSet.setString(PREF_VARIABLE_LENGTH_ARRAY, vlaName);

        }

        if ( jcaContext != null ) {
            preferencesSet.setString(PREF_ADDR_LIST, StringUtils.defaultString(jcaContext.addrList));
            preferencesSet.setBoolean(PREF_AUTO_ADDR_LIST, jcaContext.autoAddrList);
            preferencesSet.setDouble(PREF_BEACON_PERIOD, jcaContext.beaconPeriod);
            preferencesSet.setDouble(PREF_CONNECTION_TIMEOUT, jcaContext.connectionTimeout);
            preferencesSet.setInteger(PREF_MAX_ARRAY_SIZE, jcaContext.maxArrayBytes);
            preferencesSet.setBoolean(PREF_PURE_JAVA, jcaContext.pureJava);
            preferencesSet.setInteger(PREF_REPEATER_PORT, jcaContext.repeaterPort);
            preferencesSet.setInteger(PREF_SERVER_PORT, jcaContext.serverPort);
        }

    }

}
