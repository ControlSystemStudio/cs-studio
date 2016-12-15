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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.csstudio.diirt.util.preferences.DIIRTPreferencesPlugin;
import org.csstudio.diirt.util.preferences.pojo.CompositeDataSource.DataSourceProtocol;
import org.eclipse.jface.preference.IPreferenceStore;


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

    public static final String PREF_DEFAULT   = "diirt.datasource.default";
    public static final String PREF_DELIMITER = "diirt.datasource.delimiter";

    public static final String DATASOURCES_DIR     = "datasources";
    public static final String DATASOURCES_FILE    = "datasources.xml";
    public static final String DATASOURCES_VERSION = "1";

    @XmlElement( name = "compositeDataSource", nillable = true )
	public CompositeDataSource compositeDataSource = null;

	@XmlAttribute( name = "version", required = true )
	public String version = DATASOURCES_VERSION;

    /**
     * Copy the {@link DataSources} parameters from a source store to a
     * destination one.
     *
     * @param source The source preference store.
     * @param destination The destination preference store.
     */
    public static void copy ( IPreferenceStore source, IPreferenceStore destination ) {

        destination.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_DEFAULT),   source.getString(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_DEFAULT)));
        destination.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_DELIMITER), source.getString(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_DELIMITER)));

        destination.setDefault(PREF_DEFAULT,   source.getDefaultString(PREF_DEFAULT));
        destination.setDefault(PREF_DELIMITER, source.getDefaultString(PREF_DELIMITER));

        destination.setValue(PREF_DEFAULT,   source.getString(PREF_DEFAULT));
        destination.setValue(PREF_DELIMITER, source.getString(PREF_DELIMITER));

    }

	/**
	 * Create and instance of this class loading it from the given folder.
	 *
	 * @param confDir The DIIRT configuration directory.
	 * @return An instance of this class initialized from the given folder.
	 * @throws IOException In case the given file cannot be read or the version is invalid.
	 * @throws JAXBException In case the given file cannot unmashalled.
	 */
	public static DataSources fromFile ( File confDir ) throws IOException, JAXBException {

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
     * Update current defaults and values from the stored ones.
     *
     * @param store The preference store.
     */
    public static void updateValues ( IPreferenceStore store ) {
        store.setDefault(PREF_DEFAULT, store.getString(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_DEFAULT)));
        store.setDefault(PREF_DELIMITER, store.getString(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_DELIMITER)));
    }

    public DataSources () {
    }

    public DataSources ( IPreferenceStore store ) {
        this(new CompositeDataSource(
            CompositeDataSource.DataSourceProtocol.fromString(store.getString(PREF_DEFAULT)),
            store.getString(PREF_DELIMITER)
        ));
    }

    public DataSources ( CompositeDataSource compositeDataSource ) {

        this();

        this.compositeDataSource = compositeDataSource;

    }

    @Override
    public int hashCode ( ) {
        return new HashCodeBuilder(19, 311)
            .append(compositeDataSource)
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

        DataSources ds = (DataSources) obj;

        return new EqualsBuilder()
            .append(compositeDataSource, ds.compositeDataSource)
            .append(version,             ds.version)
            .isEquals();

    }

    @Override
    public String toString ( ) {
        return new ToStringBuilder(this)
            .append("compositeDataSource", compositeDataSource)
            .append("version",             version)
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

        File dsDir = new File(confDir, DATASOURCES_DIR);

        FileUtils.forceMkdir(dsDir);

        JAXBContext context = JAXBContext.newInstance(DataSources.class);
        Marshaller marshaller = context.createMarshaller();

        try ( Writer writer = new FileWriter(new File(dsDir, DATASOURCES_FILE)) ) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n");

            if ( DIIRTPreferencesPlugin.get() != null ) {
                writer.write(MessageFormat.format("<!-- Original DIIRT home: {0} -->\n", DIIRTPreferencesPlugin.get().getDIIRTHome()));
            }

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

        if ( compositeDataSource != null ) {

            String cdsName = ObjectUtils.defaultIfNull(compositeDataSource.defaultDataSource, DataSourceProtocol.none).name();

            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_DEFAULT), cdsName);
            store.setValue(DIIRTPreferencesPlugin.defaultPreferenceName(PREF_DELIMITER), compositeDataSource.delimiter);

            store.setDefault(PREF_DEFAULT, cdsName);
            store.setDefault(PREF_DELIMITER, compositeDataSource.delimiter);

            store.setValue(PREF_DEFAULT, cdsName);
            store.setValue(PREF_DELIMITER, compositeDataSource.delimiter);

        }

    }

}
