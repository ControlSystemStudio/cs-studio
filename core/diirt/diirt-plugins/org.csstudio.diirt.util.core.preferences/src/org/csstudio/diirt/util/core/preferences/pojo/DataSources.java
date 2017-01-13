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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.csstudio.diirt.util.core.preferences.DIIRTPreferences;
import org.csstudio.diirt.util.core.preferences.pojo.CompositeDataSource.DataSourceProtocol;


/**
 * Plain Old Java Object representing a {@code datasources.xml} file.
 * <p>
 * This class controls the configuration of how the different data sources
 * are combined and made accessible to DIIRT.</p>
 * <p>
 * The composite data source allows multiple data sources to be
 * made available by breaking up the channel name into three parts:</p>
 * <pre>
 * [dataSourceName][delimiter][channelName]
 * </pre>
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 7 Nov 2016
 * @see CompositeDataSource
 */
@XmlRootElement( name = "dataSources" )
@XmlType( name = "DataSources" )
public class DataSources {

    public static final String PREF_DEFAULT = "diirt.datasource.default";
    public static final String PREF_DELIMITER = "diirt.datasource.delimiter";

    public static final String DATASOURCES_DIR = "datasources";
    public static final String DATASOURCES_FILE = "datasources.xml";
    public static final String DATASOURCES_VERSION = "1";

    @XmlElement( name = "compositeDataSource", nillable = true )
    public CompositeDataSource compositeDataSource = null;

    @XmlAttribute( name = "version", required = true )
    public String version = DATASOURCES_VERSION;

    /**
     * Create and instance of this class loading it from the given folder.
     *
     * @param confDir The DIIRT configuration directory.
     * @return An instance of this class initialized from the given folder.
     * @throws IOException   In case the given file cannot be read or the version is invalid.
     * @throws JAXBException In case the given file cannot unmashalled.
     */
    public static DataSources fromFile( File confDir ) throws IOException, JAXBException {

        File datasourcesDir = new File(confDir, DATASOURCES_DIR);
        File datasourcesFile = new File(datasourcesDir, DATASOURCES_FILE);
        JAXBContext jc = JAXBContext.newInstance(DataSources.class);
        Unmarshaller u = jc.createUnmarshaller();
        DataSources ds = (DataSources) u.unmarshal(datasourcesFile);

        if ( !DATASOURCES_VERSION.equals(ds.version) ) {
            throw new IOException(MessageFormat.format("Version mismatch: expected {0}, found {1}.", DATASOURCES_VERSION, ds.version));
        }

        if ( ds.compositeDataSource == null ) {
            ds.compositeDataSource = new CompositeDataSource();
        }

        return ds;

    }

    /**
     * Create an instance of this class with default initialization.
     */
    public DataSources() {
    }

    /**
     * Create an instance of this class initialized using the given preferences
     * set.
     *
     * @param preferencesSet The preferences set used to initialize this object.
     */
    public DataSources( DIIRTPreferences preferencesSet ) {
        this(new CompositeDataSource(
            CompositeDataSource.DataSourceProtocol.fromString(preferencesSet.getString(PREF_DEFAULT)),
            preferencesSet.getString(PREF_DELIMITER)
        ));
    }

    public DataSources( CompositeDataSource compositeDataSource ) {

        this();

        this.compositeDataSource = compositeDataSource;

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 311)
            .append(compositeDataSource)
            .append(version)
            .toHashCode();
    }

    @Override
    public boolean equals( Object obj ) {

        if ( obj == null ) {
            return false;
        } else if ( obj == this ) {
            return true;
        } else if ( obj.getClass() != getClass() ) {
            return false;
        }

        DataSources ds = (DataSources) obj;

        return new EqualsBuilder()
            .append(compositeDataSource, ds.compositeDataSource)
            .append(version, ds.version)
            .isEquals();

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("compositeDataSource", compositeDataSource)
            .append("version", version)
            .toString();
    }

    /**
     * Store this instance into the given folder.
     *
     * @param confDir The current DIIRT configuration directory.
     * @throws IOException   If problems occurred saving data into file or creating the folder structure.
     * @throws JAXBException In case the given instance cannot be marshalled.
     */
    public void toFile( File confDir ) throws IOException, JAXBException {

        File dsDir = new File(confDir, DATASOURCES_DIR);

        FileUtils.forceMkdir(dsDir);

        JAXBContext context = JAXBContext.newInstance(DataSources.class);
        Marshaller marshaller = context.createMarshaller();

        try ( Writer writer = new FileWriter(new File(dsDir, DATASOURCES_FILE)) ) {

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
    public void updateDefaultsAndValues( DIIRTPreferences preferencesSet ) {

        if ( compositeDataSource != null ) {

            String cdsName = ObjectUtils.defaultIfNull(compositeDataSource.defaultDataSource, DataSourceProtocol.none).name();

            preferencesSet.setDefaultString(PREF_DEFAULT, cdsName);
            preferencesSet.setDefaultString(PREF_DELIMITER, compositeDataSource.delimiter);

        }

        updateValues(preferencesSet);

    }

    /**
     * Update values in the given preferences set.
     *
     * @param preferencesSet The preferences set.
     */
    public void updateValues( DIIRTPreferences preferencesSet ) {

        if ( compositeDataSource != null ) {

            String cdsName = ObjectUtils.defaultIfNull(compositeDataSource.defaultDataSource, DataSourceProtocol.none).name();

            preferencesSet.setString(PREF_DEFAULT, cdsName);
            preferencesSet.setString(PREF_DELIMITER, compositeDataSource.delimiter);

        }

    }

}
