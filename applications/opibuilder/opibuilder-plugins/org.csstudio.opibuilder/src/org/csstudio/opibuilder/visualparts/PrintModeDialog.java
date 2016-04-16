/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.eclipse.draw2d.PrintFigureOperation;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;



/**The print mode selection dialog.
 * @author Xihui Chen
 *
 */
public class PrintModeDialog extends Dialog {

private Button tile, fitPage, fitWidth, fitHeight;

public PrintModeDialog(Shell shell) {
    super(shell);
}

@Override
protected void cancelPressed() {
    setReturnCode(-1);
    close();
}

@Override
protected void configureShell(Shell newShell) {
    newShell.setText("Select Print Mode");
    super.configureShell(newShell);
}

@Override
protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite)super.createDialogArea(parent);

    tile = new Button(composite, SWT.RADIO);
    tile.setText("Tile");

    fitPage = new Button(composite, SWT.RADIO);
    fitPage.setText("Fit Page");
    fitPage.setSelection(true);


    fitWidth = new Button(composite, SWT.RADIO);
    fitWidth.setText("Fit Width");

    fitHeight = new Button(composite, SWT.RADIO);
    fitHeight.setText("Fit Height");

    return composite;
}

@Override
protected void okPressed() {
    int returnCode = -1;
    if (tile.getSelection())
        returnCode = PrintFigureOperation.TILE;
    else if (fitPage.getSelection())
        returnCode = PrintFigureOperation.FIT_PAGE;
    else if (fitHeight.getSelection())
        returnCode = PrintFigureOperation.FIT_HEIGHT;
    else if (fitWidth.getSelection())
        returnCode = PrintFigureOperation.FIT_WIDTH;
    setReturnCode(returnCode);
    close();
}
}
