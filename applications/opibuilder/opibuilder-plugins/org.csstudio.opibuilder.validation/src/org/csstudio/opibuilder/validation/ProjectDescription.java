/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * <code>ProjectDescription</code> is a JAXB root element for the .project file. It contains only those properties that
 * are requiured by the opivalidation (links and vairables).
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
@XmlRootElement(name = "projectDescription")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectDescription {

    /**
     *
     * <code>Link</code> represents the linked resource.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Link {
        private String name;
        private int type;
        private String locationURI;
        private String location;

        /**
         * @return destination path name in workspace.
         */
        public String getName() {
            return name;
        }

        /**
         * @return 1 for file and 2 for folder
         */
        public int getType() {
            return type;
        }

        /**
         * Returns the location as URI. If location URI is defined, location is not. The location URI might be
         * unresolved yet (contains variables).
         *
         * @return location as URI (if location URI is defined, location is not)
         */
        public String getLocationURI() {
            return locationURI;
        }

        /**
         * Set a new location URI.
         *
         * @param locationURI
         */
        public void setLocationURI(String locationURI) {
            this.locationURI = locationURI;
        }

        /**
         * Returns the location. If location is defined, location URI is not. The location might be unresolved yet
         * (contains variables).
         *
         * @return location as string (if location is defined, location URI is not)
         */
        public String getLocation() {
            return location;
        }

        /**
         *
         * @param location
         */
        public void setLocation(String location) {
            this.location = location;
        }

        /**
         * Transforms the location or location uri to a file.
         *
         * @return the file
         * @throws URISyntaxException if location uri is defined but is not a file
         */
        public File getFile() throws URISyntaxException {
            if (location != null) {
                return new File(location);
            } else if (locationURI != null) {
                return new File(new URI(locationURI));
            } else {
                return null;
            }
        }
    }

    /**
     *
     * <code>Variable</code> represents a name value pair
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Variable {
        private String name;
        private String value;

        /**
         * @return the name of the variable
         */
        public String getName() {
            return name;
        }

        /**
         * @return the value of the variable
         */
        public String getValue() {
            return value;
        }
    }

    @XmlElement(name = "link")
    @XmlElementWrapper(name = "linkedResources")
    private List<Link> links = new ArrayList<>();
    @XmlElement(name = "variable")
    @XmlElementWrapper(name = "variableList")
    private List<Variable> variables = new ArrayList<>();

    /**
     * @return all links that are defined in the file
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     * @return all variables defined in the file
     */
    public List<Variable> getVariables() {
        return variables;
    }
}
