/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.widgets.model.TextUpdateModel;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

/**
 * The Editpolicy to handle direct text edit.
 * @author Xihui Chen
 *
 */
public class TextUpdateDirectEditPolicy
    extends DirectEditPolicy {

    /**
     * @see DirectEditPolicy#getDirectEditCommand(DirectEditRequest)
     */
    @Override
    protected Command getDirectEditCommand(DirectEditRequest edit) {
        String labelText = (String)edit.getCellEditor().getValue();
        TextUpdateEditPart label = (TextUpdateEditPart)getHost();
        TextIndicatorEditCommand command =
            new TextIndicatorEditCommand((TextUpdateModel)label.getModel(),labelText);
        return command;
    }

    /**
     * @see DirectEditPolicy#showCurrentEditValue(DirectEditRequest)
     */
    @Override
    protected void showCurrentEditValue(DirectEditRequest request) {
        //String value = (String)request.getCellEditor().getValue();
        //((LabelFigure)getHostFigure()).setText(value);
        //hack to prevent async layout from placing the cell editor twice.
        //getHostFigure().getUpdateManager().performUpdate();


    }


static class TextIndicatorEditCommand extends Command    {

    private String newText, oldText;
    private TextUpdateModel label;

    public TextIndicatorEditCommand(TextUpdateModel l, String s) {
    label = l;
    if (s != null)
        newText = s;
    else
        newText = "";  //$NON-NLS-1$
    }

    @Override
    public void execute() {
        oldText = label.getText();
        label.setPropertyValue(TextUpdateModel.PROP_TEXT, newText, true);//setText(newText);
    }

    @Override
    public void undo() {
        label.setText(oldText);
    }

}


}
