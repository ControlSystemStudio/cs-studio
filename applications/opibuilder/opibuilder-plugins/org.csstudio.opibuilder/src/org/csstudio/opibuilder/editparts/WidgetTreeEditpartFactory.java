/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editparts;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**The factory creating tree editpart from model.
 * @author Xihui Chen
 *
 */
public class WidgetTreeEditpartFactory implements EditPartFactory {

    @Override
    public EditPart createEditPart(EditPart context, Object model) {
        if(model instanceof AbstractContainerModel)
            return new ContainerTreeEditpart((AbstractContainerModel) model);
        if(model instanceof AbstractWidgetModel)
            return new WidgetTreeEditpart((AbstractWidgetModel) model);
        return null;
    }

}
