/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Plain Old Java Object representing the "jcaContext" element of a
 * {@code ca.xml} file.
 * <p>
 * The core parameter for Channel Access are configured through
 * the JCA Context. This includes what implementation of JCA is used
 * (JCA-JNI or CAJ-PureJava) and the configuration parameters for those
 * implementations.</p>
 * <p>
 * By default, CAJ is used with the default CAJ configuration.
 * CAJ should, by default, honor the standard EPICS environment variables
 * to configure the client. One can still override that configuration
 * by specifying the configuration properties here. Please, refer
 * to the JCA/CAJ instructions for details on these properties.</p>
 * <p>
 * We recommend to use the CAJ (pure java) implementation, as the JCA (JNI)
 * implementation currently lacks an official maintainer.</p>
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 18 Nov 2016
 */
@XmlType( name = "JCAContext" )
public class JCAContext {

    @XmlAttribute( name = "addr_list" )
    public String addrList = "localhost";

    @XmlAttribute( name = "auto_addr_list" )
    public boolean autoAddrList= true;

    @XmlAttribute( name = "beacon_period" )
    public double beaconPeriod = 15;

    @XmlAttribute( name = "connection_timeout" )
    public double connectionTimeout = 30;

    @XmlAttribute( name = "max_array_bytes" )
    public int maxArrayBytes = 16384;

    @XmlAttribute( name = "repeater_port" )
    public int repeaterPort = 5065;

    @XmlAttribute( name = "server_port" )
    public int serverPort = 5064;

    //  JCA no more available since DIIRT 3.1.7 ---------------------------
    @XmlAttribute( name = "pureJava" )
    private boolean pureJava = true;
    //public boolean pureJava = true;
    //  -------------------------------------------------------------------

    public JCAContext () {
    }

    public JCAContext (
        String addrList,
        boolean autoAddrList,
        double beaconPeriod,
        double connectionTimeout,
        int maxArrayBytes,
        int repeaterPort,
        int serverPort
    ) {

        this();

        this.addrList = addrList;
        this.autoAddrList = autoAddrList;
        this.beaconPeriod = beaconPeriod;
        this.connectionTimeout = connectionTimeout;
        this.maxArrayBytes = maxArrayBytes;
        this.repeaterPort = repeaterPort;
        this.serverPort = serverPort;

    }

    @Override
    public int hashCode ( ) {
        return new HashCodeBuilder(23, 1117)
            .append(addrList)
            .append(autoAddrList)
            .append(beaconPeriod)
            .append(connectionTimeout)
            .append(maxArrayBytes)
            .append(pureJava)
            .append(repeaterPort)
            .append(serverPort)
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

        JCAContext jcc = (JCAContext) obj;

        return new EqualsBuilder()
            .append(addrList,          jcc.addrList)
            .append(autoAddrList,      jcc.autoAddrList)
            .append(beaconPeriod,      jcc.beaconPeriod)
            .append(connectionTimeout, jcc.connectionTimeout)
            .append(maxArrayBytes,     jcc.maxArrayBytes)
            .append(pureJava,          jcc.pureJava)
            .append(repeaterPort,      jcc.repeaterPort)
            .append(serverPort,        jcc.serverPort)
            .isEquals();

    }

    @Override
    public String toString ( ) {
        return new ToStringBuilder(this)
            .append("addrList",          addrList)
            .append("autoAddrList",      autoAddrList)
            .append("beaconPeriod",      beaconPeriod)
            .append("connectionTimeout", connectionTimeout)
            .append("maxArrayBytes",     maxArrayBytes)
            .append("pureJava",          pureJava)
            .append("repeaterPort",      repeaterPort)
            .append("serverPort",        serverPort)
            .toString();
    }

}
