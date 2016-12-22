/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences.pojo;


import java.text.MessageFormat;
import java.util.logging.Level;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.csstudio.diirt.util.core.preferences.DIIRTPreferences;
import org.csstudio.diirt.util.core.preferences.ExceptionUtilities;

/**
 * Plain Old Java Object representing the "compositeDataSource" element of a
 * {@code datasources.xml} file.
 * <p>
 * It  allows to set a default data source so that names that do not
 * match the pattern are forwarded to the datasource.</p>
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 7 Nov 2016
 * @see DataSources
 */
@XmlType( name = "CompositeDataSource" )
public class CompositeDataSource {

	@XmlAttribute( name = "defaultDataSource" )
	public DataSourceProtocol defaultDataSource = null;

	@XmlAttribute( name = "delimiter" )
	public String delimiter = "://";

	public CompositeDataSource () {
    }

	public CompositeDataSource ( DataSourceProtocol defaultDataSource, String delimiter ) {

	    this();

	    this.defaultDataSource = defaultDataSource;
	    this.delimiter = delimiter;

	}

    @Override
    public int hashCode ( ) {
        return new HashCodeBuilder(17, 37)
            .append(defaultDataSource)
            .append(delimiter)
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

        CompositeDataSource cds = (CompositeDataSource) obj;

        return new EqualsBuilder()
            .append(defaultDataSource, cds.defaultDataSource)
            .append(delimiter,         cds.delimiter)
            .isEquals();

    }

    @Override
    public String toString ( ) {
        return new ToStringBuilder(this)
            .append("defaultDataSource", defaultDataSource)
            .append("delimiter",         delimiter)
            .toString();
    }

    /**
	 * The possible values for the {@link #defaultDataSource} property.
	 */
	@XmlEnum
	public enum DataSourceProtocol {

		/**
		 * No data source protocol
		 */
		none,

		/**
		 * Channel Access
		 */
		ca,

		/**
		 * File
		 */
		file,

		/**
		 * Local
		 */
		loc,

		/**
		 * PV Access
		 */
		pva,

		/**
		 * Simulation
		 */
		sim,

		/**
		 * System
		 */
		sys;

	    public static DataSourceProtocol fromString ( String dataSourceProtocol ) {

	        DataSourceProtocol dsp = DataSourceProtocol.none;

	        try {
	            dsp = DataSourceProtocol.valueOf(dataSourceProtocol);
	        } catch ( Exception ex ){
	            DIIRTPreferences.LOGGER.log(
                    Level.WARNING,
                    MessageFormat.format(
                        "Invalid default data source [{0}]\n{1}",
                        dataSourceProtocol,
                        ExceptionUtilities.reducedStackTrace(ex, "org.csstudio")
                    )
                );
	        }

	        return dsp;

	    }

	}

}
