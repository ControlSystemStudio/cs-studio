/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.opiwidget;

import org.csstudio.alarm.beast.msghist.model.FilterQuery;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * Adds filtering query validation to string property.
 *
 * @author Borut Terpinc
 */
public class FilterProperty extends StringProperty {

    public FilterProperty(String propId, String description, WidgetPropertyCategory category, String defaultValue) {
        super(propId, description, category, defaultValue);
    }

    @Override
    public Object checkValue(Object value) {
        String query = (String) super.checkValue(value);
        if (query == null)
            return null;
        if (FilterQuery.validateQuery(query) == null)
            return query;
        return null;
    }
}
