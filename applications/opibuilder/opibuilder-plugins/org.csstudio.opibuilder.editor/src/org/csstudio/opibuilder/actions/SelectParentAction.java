/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.eclipse.jface.action.IAction;

/**The action will select parent container of current selected widget.
 * @author Xihui Chen
 *
 */
public class SelectParentAction extends AbstractWidgetTargetAction{

    @Override
    public void run(IAction action) {

        AbstractContainerEditpart containerEditpart = getParentContainerEditpart();
        containerEditpart.getViewer().select(containerEditpart);

    }


    protected final AbstractContainerEditpart getParentContainerEditpart() {
        return (AbstractContainerEditpart) (
                (AbstractBaseEditPart)selection.getFirstElement()).getParent();
    }

}
