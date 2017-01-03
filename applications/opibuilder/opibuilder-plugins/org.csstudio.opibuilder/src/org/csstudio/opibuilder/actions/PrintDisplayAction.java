/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

/**
 * The action to print display.
 *
 * @author Xihui Chen
 *
 */
public class PrintDisplayAction extends WorkbenchPartAction {

    public static final String ID = "org.csstudio.opibuilder.actions.print";

    /**
     * Constructor for PrintAction.
     *
     * @param part
     *            The workbench part associated with this PrintAction
     */
    public PrintDisplayAction(IWorkbenchPart part) {
        super(part);
    }

    /**
     * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
     */
    @Override
    protected boolean calculateEnabled() {
        return true;
    }

    /**
     * @see org.eclipse.gef.ui.actions.EditorPartAction#init()
     */
    @Override
    protected void init() {
        super.init();
        setText("Print...");
        setToolTipText("Print Display");
        setId(ActionFactory.PRINT.getId());
        setActionDefinitionId("org.eclipse.ui.file.print"); //$NON-NLS-1$
        ISharedImages sharedImages = getWorkbenchPart().getSite()
                .getWorkbenchWindow().getWorkbench().getSharedImages();
        setImageDescriptor(sharedImages
                .getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        final GraphicalViewer viewer = getWorkbenchPart().getAdapter(GraphicalViewer.class);

        viewer.getControl().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                final ImageLoader loader = new ImageLoader();
                ImageData[] imageData;
                try {

                    imageData = loader.load(ResourceUtil
                            .getScreenshotFile(viewer));

                    if (imageData.length > 0) {
                        PrintDialog dialog = new PrintDialog(viewer.getControl()
                                .getShell(), SWT.NULL);
                        final PrinterData data = dialog.open();
                        if (data != null) {
                            Printer printer = new Printer(data);

                            // Calculate the scale factor between the screen resolution
                            // and printer
                            // resolution in order to correctly size the image for the
                            // printer
                            Point screenDPI = viewer.getControl().getDisplay().getDPI();
                            Point printerDPI = printer.getDPI();
                            int scaleFactor = printerDPI.x / screenDPI.x;

                            // Determine the bounds of the entire area of the printer
                            Rectangle trim = printer.computeTrim(0, 0, 0, 0);
                            Image printerImage = new Image(printer, imageData[0]);
                            if (printer.startJob("Printing OPI")) {
                                if (printer.startPage()) {
                                    GC gc = new GC(printer);
                                    Rectangle printArea = printer.getClientArea();

                                    if (imageData[0].width * scaleFactor <= printArea.width) {
                                        printArea.width = imageData[0].width * scaleFactor;
                                        printArea.height = imageData[0].height
                                                * scaleFactor;
                                    } else {
                                        printArea.height = printArea.width
                                                * imageData[0].height / imageData[0].width;
                                    }
                                    gc.drawImage(printerImage, 0, 0, imageData[0].width,
                                            imageData[0].height, -trim.x, -trim.y,
                                            printArea.width, printArea.height);
                                    
                                    gc.dispose();
                                    printer.endPage();
                                } 
                            }
                            printer.endJob();
                            printer.dispose();
                            printerImage.dispose();
                        }
                    }
                } catch (Exception e) {
                    ErrorHandlerUtil.handleError("Failed to print OPI", e);
                    return;
                }
            }
        });

    }

}
