/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

/**
 * Custom selection tool for OPI Runtime. Copies PV name to pastebuffer on
 * middle click. Also patches behaviour on right-click.
 *
 * @author Xihui Chen
 * @author Will Rogers
 *
 */
public class RuntimePatchedSelectionTool extends SelectionTool {

    private static final String CURSOR_PATH = "icons/copy.gif";
    private static Cursor copyPvCursor;
    private Cursor oldCursor = null;
    private boolean cursorChanged = false;

    /*
     * Set up cursor icon.
     */
    static {
        Bundle bundle = Platform.getBundle(OPIBuilderPlugin.PLUGIN_ID);
        IPath path = new Path(CURSOR_PATH);
        URL url = FileLocator.find(bundle, path, null);
        try {
            InputStream inputStream = url.openConnection().getInputStream();
            copyPvCursor = new Cursor(Display.getCurrent(), new ImageData(
                    inputStream), 0, 0);
        } catch (IOException e) {
            copyPvCursor = Cursors.HELP;
        }
    }

    /**
     * Work around a bug in GEF: right click is recognized as mouse exit event
     * in LightWeightSystem, so handleButtonUp() will not be invoked for right
     * click button up. This will cause unexpected select behavior.
     */
    @Override
    protected boolean handleViewerExited() {
        getCurrentInput().setMouseButton(3, false);
        handleButtonUp(3);
        return super.handleViewerExited();
    }

    /**
     * Intercept middle clicks and copy PV name to pastebuffer if available.
     * Change cursor to copy symbol.
     */
    @Override
    protected boolean handleButtonDown(int button) {
        if (button == 2) {
            EditPart editPart = getTargetEditPart();
            if (editPart instanceof AbstractPVWidgetEditPart) {
                AbstractPVWidgetEditPart apvwep = (AbstractPVWidgetEditPart) editPart;
                String pvName = ((AbstractPVWidgetModel) editPart.getModel())
                        .getPVName();
                if (pvName != "" && pvName != null) {
                    Display display = Display.getCurrent();
                    Clipboard clipboard = new Clipboard(display);

                    // Copies to middle button paste buffer,
                    // to be pasted via another middle-button click
                    clipboard.setContents(new Object[] { pvName },
                            new Transfer[] { TextTransfer.getInstance() },
                            DND.SELECTION_CLIPBOARD);

                    // Copies to normal clipboard,
                    // to be pasted via Ctrl-V or Edit/Paste
                    clipboard.setContents(new String[] { pvName },
                            new Transfer[] { TextTransfer.getInstance() });

                    clipboard.dispose();
                    IFigure figure = apvwep.getFigure();
                    oldCursor = figure.getCursor();
                    figure.setCursor(copyPvCursor);
                    cursorChanged = true;
                }
            }
            return true;
        } else {
            return super.handleButtonDown(button);
        }
    }

    /**
     * Intercept middle clicks and restore original cursor if it has changed.
     */
    @Override
    protected boolean handleButtonUp(int button) {
        if (button == 2) {
            EditPart editPart = getTargetEditPart();
            if (editPart instanceof AbstractPVWidgetEditPart) {
                AbstractPVWidgetEditPart apvwep = (AbstractPVWidgetEditPart) editPart;
                IFigure figure = apvwep.getFigure();
                if (cursorChanged) {
                    figure.setCursor(oldCursor);
                    oldCursor = null;
                    cursorChanged = false;
                }
            }
            return true;
        } else {
            return super.handleButtonUp(button);
        }
    }
}
