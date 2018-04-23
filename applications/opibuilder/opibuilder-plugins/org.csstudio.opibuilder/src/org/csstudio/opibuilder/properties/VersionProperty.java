/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.jdom.Element;
import org.osgi.framework.Version;

/**Version property.
 * @author Xihui Chen
 *
 */
public class VersionProperty extends UnchangableStringProperty{

    public VersionProperty(String prop_id, String description,
            WidgetPropertyCategory category, String defaultValue) {
        super(prop_id, description, category, defaultValue);
    }

    @Override
    public void writeToXML(Element propElement) {
        final Version version = OPIBuilderPlugin.getDefault().getBundle().getVersion();
        // Omit the qualifier
        final StringBuilder buf = new StringBuilder();
        buf.append(version.getMajor())
           .append('.')
           .append(version.getMinor())
           .append('.')
           .append(version.getMicro());
        setPropertyValue(buf.toString());
        super.writeToXML(propElement);
    }

    @Override
    public boolean configurableByRule() {
        return false;
    }


}
