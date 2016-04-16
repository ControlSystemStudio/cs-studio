/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

/**
 * The Editpolicy to handle direct text edit in index spinner of an array widget.
 * @author Xihui Chen
 *
 */
public class ArraySpinnerDirectEditPolicy
    extends DirectEditPolicy {

    /**
     * @see DirectEditPolicy#getDirectEditCommand(DirectEditRequest)
     */
    protected Command getDirectEditCommand(DirectEditRequest edit) {
        String text = (String)edit.getCellEditor().getValue();
        text = text.replace("e", "E"); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            int value = new DecimalFormat().parse(text).intValue();
            ArrayEditPart array = (ArrayEditPart) getHost();
            ArraySpinnerEditCommand command = new ArraySpinnerEditCommand(array, value);
            return command;
        } catch (ParseException e) {
            return null;
        }

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


static class ArraySpinnerEditCommand extends Command    {

    private int newIndex, oldIndex;
    private ArrayEditPart arrayEditpart;

    public ArraySpinnerEditCommand(ArrayEditPart arrayEditpart, int newIndex) {
    this.arrayEditpart = arrayEditpart;
    this.newIndex = newIndex;
    }

    @Override
    public void execute() {
        oldIndex = arrayEditpart.getArrayFigure().getIndex();
        if(newIndex>=arrayEditpart.getArrayFigure().getArrayLength())
            newIndex = arrayEditpart.getArrayFigure().getArrayLength()-1;
        arrayEditpart.getArrayFigure().setIndex(newIndex);
    }

    @Override
    public void undo() {
        arrayEditpart.getArrayFigure().setIndex(oldIndex);
    }

}


}
