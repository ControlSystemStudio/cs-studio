/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences.pojo;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 22 Nov 2016
 */
@XmlType( name = "DataSourceOptions" )
public class DataSourceOptions {

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

        private final String representation;

        private VariableArraySupport ( String representation ) {
            this.representation = representation;
        }

        public String representation() {
            return representation;
        }

    }

}
