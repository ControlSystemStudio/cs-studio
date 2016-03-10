/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.palette;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.eclipse.gef.requests.CreationFactory;


/**The CreationFactory to create the widget.
 * @author Xihui Chen
 *
 */
public class WidgetCreationFactory implements CreationFactory {

    private final WidgetDescriptor widgetDescriptor;
    private AbstractWidgetModel widgetModel = null;

    public WidgetCreationFactory(WidgetDescriptor widgetDescriptor) {
        this.widgetDescriptor = widgetDescriptor;
    }

    @Override
    public Object getNewObject() {
        widgetModel = widgetDescriptor.getWidgetModel();
        return widgetModel;
    }

    @Override
    public Object getObjectType() {
        if(widgetModel == null)
            widgetModel = widgetDescriptor.getWidgetModel();
        Object widgetClass = widgetModel.getClass();
        return widgetClass;
    }

}
