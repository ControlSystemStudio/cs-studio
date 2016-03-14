/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editparts.AbstractLayoutEditpart;
import org.eclipse.jface.action.IAction;

/**
 * An action to layout widgets in a container.
 * @author Xihui Chen
 *
 */
public class LayoutWidgetsAction extends AbstractWidgetTargetAction {

    @Override
    public void run(IAction action) {

        AbstractLayoutEditpart layoutWidget = getLayoutWidget();

        LayoutWidgetsImp.run(layoutWidget, getCommandStack());

    }

    protected AbstractLayoutEditpart getLayoutWidget(){
        return (AbstractLayoutEditpart)selection.getFirstElement();
    }

}
