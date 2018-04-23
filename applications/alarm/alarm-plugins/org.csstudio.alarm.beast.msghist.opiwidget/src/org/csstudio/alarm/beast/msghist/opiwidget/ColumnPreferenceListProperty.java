/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.opiwidget;

import java.util.List;

import org.csstudio.alarm.beast.msghist.PropertyColumnPreference;
import org.csstudio.opibuilder.properties.StringListProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * Wrapper around string list property that performs validation for proper {@link PropertyColumnPreference} string representation.
 *
 * @author Borut Terpinc
 */
public class ColumnPreferenceListProperty extends StringListProperty {

    public ColumnPreferenceListProperty(String prop_id, String description, WidgetPropertyCategory category,
            List<String> default_value) {
        super(prop_id, description, category, default_value);
    }

    /**
     * Checks validity of a property value.
     *
     * @return null if value is invalid and casted value otherwise.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object checkValue(Object value) {
        List<String> strings = (List<String>) super.checkValue(value);
        if (strings == null)
            return null;

        for (String s : strings) {
            try {
                PropertyColumnPreference.fromString(s);
            } catch (Exception e) {
                return null;
            }
        }

        return strings;
    }

}
