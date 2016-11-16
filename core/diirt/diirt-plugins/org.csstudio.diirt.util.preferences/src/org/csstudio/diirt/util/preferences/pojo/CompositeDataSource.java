/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences.pojo;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * Plain Old Java Object representing the "compositeDataSource element of a
 * {@code datasources.xml} file.
 * <p>
 * It  allows to set a default data source so that names that do not
 * match the pattern are forwarded to the datasource.</p>
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 7 Nov 2016
 * @see DataSources
 */
@XmlRootElement( name = "compositeDataSource" )
@XmlType( name = "CompositeDataSource" )
@ToString @EqualsAndHashCode
public class CompositeDataSource {

	@Getter @Setter @XmlAttribute( name = "defaultDataSource" )
	private DataSourceProtocol defaultDataSource = null;

	@Getter @Setter @NonNull @XmlAttribute( name = "delimiter" )
	private String delimiter = "://";

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
		ca

	}

}
