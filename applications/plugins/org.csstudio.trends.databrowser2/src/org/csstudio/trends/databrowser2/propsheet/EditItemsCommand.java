package org.csstudio.trends.databrowser2.propsheet;

import java.util.ArrayList;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.eclipse.swt.widgets.Shell;

/**
 * Undo-able command for edit item properties. 
 * @author Takashi Nakamoto
 */
public class EditItemsCommand implements IUndoableCommand {
    final private Shell shell;
    final private ModelItem[] items;
    final private ArrayList<ModelItem> oldItems;
    final private EditItemsDialog.Result result;
   
    /**
     * Initialize command.
     * @param shell Shell for error message dialogs.
     * @param operations_manager Operations manager which manages undo/redo commands.
     * @param items Array of ModelItem instances subjected to editing.
     * @param result Result instance returned by EditItemsDialog.
     */
    public EditItemsCommand(final Shell shell,
            final OperationsManager operations_manager,
            final ModelItem[] items,
            final EditItemsDialog.Result result) {
    	this.shell = shell;
    	this.items = items;
    	this.result = result;

    	// Save old values so that this operation can be undone later.
    	oldItems = new ArrayList<ModelItem>();
    	for (ModelItem item : items)
    		oldItems.add(item.clone());
    	
    	// Edit items
    	applyChanges();
    	
    	operations_manager.addCommand(this);
    }

	@Override
	public void undo() {
		for (int i=0; i<items.length; i++) {
			items[i].setDisplayName(oldItems.get(i).getDisplayName());
			
			// TODO: revert other values
		}
	}

	@Override
	public void redo() {
		applyChanges();
	}
	
	public void applyChanges() {
		for (ModelItem item : items) {
			String strDisplayName = result.getDisplayName();
			if (strDisplayName != null) {
				// Do not apply changes if the result is null.
				item.setDisplayName(strDisplayName);
			}
			
			// TODO: change other values
		}
	}
}
