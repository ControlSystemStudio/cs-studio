/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences.pojo;

import java.text.MessageFormat;
import java.util.logging.Level;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.csstudio.diirt.util.preferences.DIIRTPreferencesPlugin;

/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 22 Nov 2016
 */
@XmlType( name = "DataSourceOptions" )
public class DataSourceOptions {

    @XmlAttribute( name = "dbePropertySupported" )
    public boolean dbePropertySupported = false;

    @XmlAttribute( name = "honorZeroPrecision" )
    public boolean honorZeroPrecision = true;

    @XmlAttribute( name = "monitorMask" )
    public String monitorMaskValue = MonitorMask.VALUE.name();

    @XmlAttribute( name = "rtypeValueOnly" )
    public boolean rtypeValueOnly = false;

    @XmlAttribute( name = "varArraySupported" )
    public VariableArraySupport varArraySupported = VariableArraySupport.AUTO;

    public DataSourceOptions () {
    }

    public DataSourceOptions (
        boolean dbePropertySupported,
        boolean honorZeroPrecision,
        MonitorMask monitorMask,
        int monitorMaskCustomValue,
        boolean rtypeValueOnly,
        VariableArraySupport varArraySupported
    ) {

        this();

        this.dbePropertySupported = dbePropertySupported;
        this.honorZeroPrecision = honorZeroPrecision;
        this.rtypeValueOnly = rtypeValueOnly;
        this.varArraySupported = varArraySupported;

        if ( monitorMask == MonitorMask.CUSTOM ) {
            monitorMaskValue = String.valueOf(monitorMaskCustomValue);
        } else {
            monitorMaskValue = monitorMask.name();
        }

    }

    public MonitorMask monitorMask() {
        try {
            return MonitorMask.valueOf(monitorMaskValue);
        } catch ( Exception ex ) {
            return MonitorMask.CUSTOM;
        }
    }

    public int monitorMaskCustomValue() {
        try {
            return Integer.parseInt(monitorMaskValue);
        } catch ( Exception ex ) {
            return 5;
        }
    }

    /**
     * The possible values for the {@link #monitorMask} property.
     */
    @XmlEnum
    public enum MonitorMask {

        /**
         * Corresponds to a monitor mask on both VALUE and ALARM.
         */
        VALUE,

        /**
         * Corresponds to a monitor mask on LOG.
         */
        ARCHIVE,

        /**
         * Corresponds to a monitor mask on ALARM.
         */
        ALARM,

        /**
         * A number corresponding to the mask itself.
         */
        CUSTOM;

        public static MonitorMask fromString ( String monitorMask ) {

            MonitorMask mm = MonitorMask.VALUE;

            try {
                mm = MonitorMask.valueOf(monitorMask);
            } catch ( Exception ex ){
                DIIRTPreferencesPlugin.LOGGER.log(Level.WARNING, MessageFormat.format("Invalid monitor mask [{0}].", monitorMask), ex);
            }

            return mm;

        }

    }

    /**
     * The possible values for the {@link #varArraySupported} property.
     */
    @XmlEnum
    public enum VariableArraySupport {

        /**
         * Tries to detect the version of the client and act accordingly.
         */
        AUTO("auto"),

        /**
         * Variable array supported.
         */
        TRUE(Boolean.TRUE.toString()),

        /**
         * Variable array not supported.
         */
        FALSE(Boolean.FALSE.toString());

        public static VariableArraySupport fromString ( String variableArraySupportRepresentation ) {

            VariableArraySupport vas = VariableArraySupport.AUTO;

            try {
                vas = VariableArraySupport.representationOf(variableArraySupportRepresentation);
            } catch ( Exception ex ){
                DIIRTPreferencesPlugin.LOGGER.log(Level.WARNING, MessageFormat.format("Invalid cariable array support [{0}].", variableArraySupportRepresentation), ex);
            }

            return vas;

        }

        public static VariableArraySupport representationOf ( String repr ) {

            for ( VariableArraySupport v : values() ) {
                if ( v.representation.equals(repr) ) {
                    return v;
                }
            }

            throw new IllegalArgumentException(MessageFormat.format("Illegal representation: {0}", repr));

        }

        private final String representation;

        private VariableArraySupport ( String representation ) {
            this.representation = representation;
        }

        public String representation() {
            return representation;
        }

    }

}
