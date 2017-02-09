package org.csstudio.opibuilder.widgets.detailpanel;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.CellEditorActionHandler;

// An instance of this class is created when the edit in place (DIRECT_EDIT_ROLE) edit
// policy is started.  It handles the creation of the in place edit box (a TextCellEditor object),
// initialising it with the current contents of the name property.  When the edit is complete,
// the DetailPanelEditPolicyRow object is called to make the change.

public class DetailPanelRowEditManager extends DirectEditManager {

    private CellEditorActionHandler actionHandler;
    private IActionBars actionBars;
    private IAction copy, cut, paste, undo, redo, find, selectAll, delete;
    private DetailPanelEditpart editpart;
    private int rowNumber;

    // Constructor.  Save information.
    public DetailPanelRowEditManager(AbstractBaseEditPart source, int rowNumber, CellEditorLocator locator) {
        super(source, TextCellEditor.class, locator);
        editpart = (DetailPanelEditpart)source;
        this.rowNumber = rowNumber;
    }

    // Initialise the pop up edit box.
    @Override
    protected void initCellEditor() {
        // Initialise the text in the pop up
        if(rowNumber >= 0) {
            getCellEditor().setValue(editpart.getWidgetModel().getRow(rowNumber).getName());
        }
        // Hook the cell editor's copy/paste actions to the actionBars so that they can
        // be invoked via keyboard shortcuts in place of the global ones.  If we don't do
        // this, a keyboard paste replaces the whole widget rather than pasting into the
        // pop up edit box.
        IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if(activeEditor != null){
            actionBars = activeEditor.getEditorSite().getActionBars();
            saveCurrentActions(actionBars);
            actionHandler = new CellEditorActionHandler(actionBars);
            actionHandler.addCellEditor(getCellEditor());
            actionBars.updateActionBars();
        }
    }

    // Clean up after the pop up box is removed.
    @Override
    protected void bringDown() {
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
    }

    // Restore save actions.
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

    // Store actions for replacement later
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
}
