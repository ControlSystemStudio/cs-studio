/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.visualparts.PrintModeDialog;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;


/**The action to print display.
 * @author Xihui Chen
 *
 */
public class PrintDisplayAction extends WorkbenchPartAction {

public static final String ID = "org.csstudio.opibuilder.actions.print";

private PrinterData[] printers;
/**
 * Constructor for PrintAction.
 * @param part The workbench part associated with this PrintAction
 */
public PrintDisplayAction(IWorkbenchPart part) {
	super(part);
	printers = Printer.getPrinterList();
}

/**
 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
 */
protected boolean calculateEnabled() {	
	return printers != null && printers.length > 0;
}

/**
 * @see org.eclipse.gef.ui.actions.EditorPartAction#init()
 */
protected void init() {
	super.init();
	setText("Print...");
	setToolTipText("Print Display");
	setId(ActionFactory.PRINT.getId());
	setActionDefinitionId("org.eclipse.ui.file.print"); //$NON-NLS-1$
	ISharedImages sharedImages = 
		getWorkbenchPart().getSite().getWorkbenchWindow().getWorkbench().getSharedImages();
	setImageDescriptor(sharedImages
    .getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));
}

/**
 * @see org.eclipse.jface.action.Action#run()
 */
public void run() {
	int printMode = new PrintModeDialog(null).open();
	if (printMode == -1)
		return;
	GraphicalViewer viewer;
	viewer = (GraphicalViewer)getWorkbenchPart().getAdapter(GraphicalViewer.class);
	
	PrintDialog dialog = new PrintDialog(viewer.getControl().getShell(), SWT.NULL);
	PrinterData data = dialog.open();
	
	if (data != null) {
		PrintGraphicalViewerOperation op = 
					new PrintGraphicalViewerOperation(new Printer(data), viewer);
		op.setPrintMode(printMode);
		op.run(getWorkbenchPart().getTitle());
	}
}

}
