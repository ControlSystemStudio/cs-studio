/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.commands.SetBoundsCommand;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.editparts.DisplayEditpart;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;

/**The action will auto size the selected widget to fill its parent container.
 * @author Xihui Chen
 *
 */
public class FillParentContainerAction extends AbstractWidgetTargetAction{


    @Override
    public void run(IAction action) {

        AbstractBaseEditPart widget = (AbstractBaseEditPart)selection.getFirstElement();

        AbstractContainerEditpart containerEditpart = getParentContainerEditpart();

        Dimension size = null;
        if(containerEditpart instanceof DisplayEditpart)
            size = ((DisplayEditpart)containerEditpart).getWidgetModel().getSize();
        else
            size= containerEditpart.getFigure().getClientArea().getSize();


        Command cmd = new SetBoundsCommand(widget.getWidgetModel(),
                new Rectangle(0, 0, size.width, size.height));

        execute(cmd);

    }


    protected final AbstractContainerEditpart getParentContainerEditpart() {
        return (AbstractContainerEditpart) (
                (AbstractBaseEditPart)selection.getFirstElement()).getParent();
    }

}
