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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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

	@XmlElement( name = "compositeDataSource", nillable = true )
	private CompositeDataSource compositeDataSource = null;

	@XmlAttribute( name = "version", required = true )
	private final String version = "1";

}
