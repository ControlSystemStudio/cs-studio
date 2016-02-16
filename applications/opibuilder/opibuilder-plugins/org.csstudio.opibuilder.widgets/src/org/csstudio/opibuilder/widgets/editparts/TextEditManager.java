/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;


import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.IPVWidgetEditpart;
import org.csstudio.swt.widgets.figures.ITextFigure;
import org.eclipse.draw2d.AbstractBackground;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.CellEditorActionHandler;


/**The manager help to managing the text direct editing.
 * @author Xihui Chen
 *
 */
public class TextEditManager extends DirectEditManager
{

private IActionBars actionBars;
private CellEditorActionHandler actionHandler;
private IAction copy, cut, paste, undo, redo, find, selectAll, delete;
private double cachedZoom = -1.0;
private Font scaledFont;
private boolean multiLine = true;
private AbstractBaseEditPart editPart;
private ZoomListener zoomListener = new ZoomListener() {
    public void zoomChanged(double newZoom) {
        updateScaledFont(newZoom);
    }
};
public TextEditManager(AbstractBaseEditPart source, CellEditorLocator locator, boolean multiline) {
    super(source, null, locator);
    this.editPart = source;
    this.multiLine = multiline;
}

public TextEditManager(AbstractBaseEditPart source, CellEditorLocator locator) {
    this(source, locator, true);
}

/**
 * @see org.eclipse.gef.tools.DirectEditManager#bringDown()
 */
protected void bringDown() {
    ZoomManager zoomMgr = (ZoomManager)getEditPart().getViewer()
            .getProperty(ZoomManager.class.toString());
    if (zoomMgr != null)
        zoomMgr.removeZoomListener(zoomListener);

    if (actionHandler != null) {
        actionHandler.dispose();
        actionHandler = null;
    }
    if (actionBars != null) {
        restoreSavedActions(actionBars);
        actionBars.updateActionBars();
        actionBars = null;
    }

    super.bringDown();
    // dispose any scaled fonts that might have been created
    disposeScaledFont();
}

protected CellEditor createCellEditorOn(Composite composite) {
    CellEditor editor =  new TextCellEditor(composite, (multiLine ? SWT.MULTI : SWT.SINGLE) | SWT.WRAP){
        @Override
        protected void focusLost() {
            //in run mode, if the widget has a PV attached,
            //lose focus should cancel the editing except mobile.
                if (editPart.getExecutionMode() == ExecutionMode.RUN_MODE
                        && !OPIBuilderPlugin.isMobile(getControl().getDisplay())
                        && editPart instanceof IPVWidgetEditpart
                        && ((IPVWidgetEditpart) editPart).getPV() != null) {
                    if (isActivated()) {
                        fireCancelEditor();
                        deactivate();
                    }
                    editPart.getFigure().requestFocus();
                } else
                    super.focusLost();
        }

        @Override
        protected void handleDefaultSelection(SelectionEvent event) {
            //In run mode, hit ENTER should force to write the new value even it doesn't change.
            if(editPart.getExecutionMode() == ExecutionMode.RUN_MODE) {
                setDirty(true);
            }
            super.handleDefaultSelection(event);
        }

        @Override
        protected void keyReleaseOccured(KeyEvent keyEvent) {
            //In run mode, CTRL+ENTER will always perform a write if it is multiline text input
            if (keyEvent.character == '\r' &&
                    editPart.getExecutionMode() == ExecutionMode.RUN_MODE) { // Return key
                if (text != null && !text.isDisposed()
                        && (text.getStyle() & SWT.MULTI) != 0) {
                    if ((keyEvent.stateMask & SWT.CTRL) != 0) {
                      setDirty(true);
                    }
                }
            }
            super.keyReleaseOccured(keyEvent);
        }
    };
    editor.getControl().moveAbove(null);
    return editor;
}

private void disposeScaledFont() {
    if (scaledFont != null) {
        scaledFont.dispose();
        scaledFont = null;
    }
}

protected void initCellEditor() {
    // update text
    ITextFigure textFigure = getEditPart().getAdapter(ITextFigure.class);

//    AbstractWidgetModel labelModel = (AbstractWidgetModel) getEditPart().getModel();
    getCellEditor().setValue(textFigure.getText());
    if(textFigure.isOpaque() || textFigure.getBorder() instanceof AbstractBackground){
        getCellEditor().getControl().setBackground(
                textFigure.getBackgroundColor());
    }else {
        getCellEditor().getControl().setBackground(
                textFigure.getParent().getBackgroundColor());
    }
    getCellEditor().getControl().setForeground(textFigure.getForegroundColor());
    // update font
    ZoomManager zoomMgr = (ZoomManager)getEditPart().getViewer()
            .getProperty(ZoomManager.class.toString());
    if (zoomMgr != null) {
        // this will force the font to be set
        cachedZoom = -1.0;
        updateScaledFont(zoomMgr.getZoom());
        zoomMgr.addZoomListener(zoomListener);
    } else
        getCellEditor().getControl().setFont(textFigure.getFont());

    // Hook the cell editor's copy/paste actions to the actionBars so that they can
    // be invoked via keyboard shortcuts.
    IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .getActiveEditor();
    if(activeEditor != null){
        actionBars = activeEditor.getEditorSite().getActionBars();
        saveCurrentActions(actionBars);
        actionHandler = new CellEditorActionHandler(actionBars);
        actionHandler.addCellEditor(getCellEditor());
        actionBars.updateActionBars();
    }
}

private void restoreSavedActions(IActionBars actionBars){
    actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copy);
    actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), paste);
    actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), delete);
    actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), selectAll);
    actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), cut);
    actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), find);
    actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undo);
    actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redo);
}

private void saveCurrentActions(IActionBars actionBars) {
    copy = actionBars.getGlobalActionHandler(ActionFactory.COPY.getId());
    paste = actionBars.getGlobalActionHandler(ActionFactory.PASTE.getId());
    delete = actionBars.getGlobalActionHandler(ActionFactory.DELETE.getId());
    selectAll = actionBars.getGlobalActionHandler(ActionFactory.SELECT_ALL.getId());
    cut = actionBars.getGlobalActionHandler(ActionFactory.CUT.getId());
    find = actionBars.getGlobalActionHandler(ActionFactory.FIND.getId());
    undo = actionBars.getGlobalActionHandler(ActionFactory.UNDO.getId());
    redo = actionBars.getGlobalActionHandler(ActionFactory.REDO.getId());
}

private void updateScaledFont(double zoom) {
    if (cachedZoom == zoom)
        return;

    Text text = (Text)getCellEditor().getControl();
    Font font = getEditPart().getFigure().getFont();

    disposeScaledFont();
    cachedZoom = zoom;
    if (zoom == 1.0)
        text.setFont(font);
    else {
        FontData fd = font.getFontData()[0];
        fd.setHeight((int)(fd.getHeight() * zoom));
        text.setFont(scaledFont = new Font(null, fd));
    }
}

}
