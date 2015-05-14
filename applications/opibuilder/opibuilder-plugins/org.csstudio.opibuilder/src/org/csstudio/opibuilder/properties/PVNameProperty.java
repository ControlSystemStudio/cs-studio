/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**The widget property for pvname. It also accept macro string $(macro).
 * @author Xihui Chen
 *
 */
public class PVNameProperty extends StringProperty {

    private String detailDescription;

    /**PV Name Property Constructor. The property value type is {@link String}.
     * @param prop_id the property id which should be unique in a widget model.
     * @param description the description of the property,
     * which will be shown as the property name in property sheet.
     * @param category the category of the widget.
     * @param defaultValue the default value when the widget is first created.
     */
    public PVNameProperty(String prop_id, String description,
            WidgetPropertyCategory category, String defaultValue) {
        super(prop_id, description, category, defaultValue, false, false);
        setDetailedDescription(description);
    }

    /**Set detailed description to be displayed on tooltip and status line
      * @param detailedDescription the detailed description.
     */
    public void setDetailedDescription(String detailDescription) {
        this.detailDescription = detailDescription;
    }

    @Override
    protected PropertyDescriptor createPropertyDescriptor() {
        if(PropertySSHelper.getIMPL() == null)
            return null;
        return PropertySSHelper.getIMPL().getPVNamePropertyDescriptor(
                prop_id, description, detailDescription);
    }

}
