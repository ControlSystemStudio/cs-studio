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

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Plain Old Java Object representing a {@code ca.xml} file.
 * <p>
 * This class controls the EPICS Channel Access parameters.
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 18 Nov 2016
 */
@XmlRootElement( name = "dataSources" )
@XmlType( name = "DataSources" )
@ToString @EqualsAndHashCode
public class ChannelAccess {

    @XmlElement( name = "jcaContext", nillable = true )
    public JCAContext jcaContext = null;

    @XmlAttribute( name = "version", required = true )
    public String version = "1";

}
