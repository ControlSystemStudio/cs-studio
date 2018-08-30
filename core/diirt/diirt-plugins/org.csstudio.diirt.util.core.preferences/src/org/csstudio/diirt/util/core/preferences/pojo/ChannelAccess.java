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
    //  JCA no more available since DIIRT 3.1.7 ---------------------------
    //public static final String PREF_PURE_JAVA              = "diirt.ca.pure.java";
    //  -------------------------------------------------------------------
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
                //  JCA no more available since DIIRT 3.1.7 ---------------------------
                //preferencesSet.getBoolean(PREF_PURE_JAVA),
                //  -------------------------------------------------------------------
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
            //  JCA no more available since DIIRT 3.1.7 ---------------------------
            //preferencesSet.setDefaultBoolean(PREF_PURE_JAVA,         jcaContext.pureJava);
            //  -------------------------------------------------------------------
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
            //  JCA no more available since DIIRT 3.1.7 ---------------------------
            //preferencesSet.setBoolean(PREF_PURE_JAVA, jcaContext.pureJava);
            //  -------------------------------------------------------------------
            preferencesSet.setInteger(PREF_REPEATER_PORT, jcaContext.repeaterPort);
            preferencesSet.setInteger(PREF_SERVER_PORT, jcaContext.serverPort);
        }

    }

}
