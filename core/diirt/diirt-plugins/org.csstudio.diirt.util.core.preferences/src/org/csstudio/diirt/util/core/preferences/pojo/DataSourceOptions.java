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

import gov.aps.jca.Monitor;

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
    public String varArraySupported = VariableArraySupport.AUTO.representation();

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
        this.varArraySupported = varArraySupported.representation();

        if ( monitorMask == MonitorMask.CUSTOM ) {
            monitorMaskValue = String.valueOf(monitorMaskCustomValue);
        } else {
            monitorMaskValue = monitorMask.name();
        }

    }

    @Override
    public int hashCode ( ) {
        return new HashCodeBuilder(29, 661)
            .append(dbePropertySupported)
            .append(honorZeroPrecision)
            .append(monitorMaskValue)
            .append(rtypeValueOnly)
            .append(varArraySupported)
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

        DataSourceOptions dso = (DataSourceOptions) obj;

        return new EqualsBuilder()
            .append(dbePropertySupported, dso.dbePropertySupported)
            .append(honorZeroPrecision,   dso.honorZeroPrecision)
            .append(monitorMaskValue,     dso.monitorMaskValue)
            .append(rtypeValueOnly,       dso.rtypeValueOnly)
            .append(varArraySupported,    dso.varArraySupported)
            .isEquals();

    }

    public MonitorMask monitorMask ( ) {
        return MonitorMask.fromString(monitorMaskValue);
    }

    public int monitorMaskCustomValue ( ) {
        try {
            return Integer.parseInt(monitorMaskValue);
        } catch ( Exception ex ) {
            try {
                return MonitorMask.fromString(monitorMaskValue).mask();
            } catch ( Exception eexx ) {
                return MonitorMask.VALUE.mask();
            }
        }
    }

    public VariableArraySupport variableArraySupport ( ) {
        return VariableArraySupport.fromString(varArraySupported);
    }

    @Override
    public String toString ( ) {
        return new ToStringBuilder(this)
            .append("dbePropertySupported", dbePropertySupported)
            .append("honorZeroPrecision",   honorZeroPrecision)
            .append("monitorMaskValue",     monitorMaskValue)
            .append("rtypeValueOnly",       rtypeValueOnly)
            .append("varArraySupported",    varArraySupported)
            .toString();
    }

    /**
     * The possible values for the {@link #monitorMask} property.
     */
    @XmlEnum
    public enum MonitorMask {

        /**
         * Corresponds to a monitor mask on both VALUE and ALARM.
         */
        VALUE(Monitor.VALUE | Monitor.ALARM),

        /**
         * Corresponds to a monitor mask on LOG.
         */
        ARCHIVE(Monitor.LOG),

        /**
         * Corresponds to a monitor mask on ALARM.
         */
        ALARM(Monitor.ALARM),

        /**
         * A number corresponding to the mask itself.
         */
        CUSTOM(-1);

        final private int mask;

        public static MonitorMask fromString ( String monitorMask ) {

            MonitorMask mm = MonitorMask.CUSTOM;

            try {
                mm = MonitorMask.valueOf(monitorMask);
            } catch ( Exception ex ){
                DIIRTPreferences.LOGGER.log(
                    Level.WARNING,
                    MessageFormat.format(
                        "Invalid monitor mask [{0}]\n{1}",
                        monitorMask,
                        ExceptionUtilities.reducedStackTrace(ex, "org.csstudio")
                    )
                );
            }

            return mm;

        }

        public int mask ( ) {
            return mask;
        }

        private MonitorMask ( int mask ) {
            this.mask = mask;
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
                DIIRTPreferences.LOGGER.log(
                    Level.WARNING,
                    MessageFormat.format(
                        "Invalid variable array support representation [{0}]\n{1}",
                        variableArraySupportRepresentation,
                        ExceptionUtilities.reducedStackTrace(ex, "org.csstudio")
                    )
                );
            }

            return vas;

        }

        public static VariableArraySupport representationOf ( String repr ) {

            for ( VariableArraySupport v : values() ) {
                if ( v.representation.equals(repr) ) {
                    return v;
                }
            }

            throw new IllegalArgumentException(MessageFormat.format("Illegal variable array support representation: {0}", repr));

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
