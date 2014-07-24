package org.csstudio.trends.databrowser2.propsheet;

import java.util.ArrayList;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.PVItem;

/**
 * Undo-able command for edit item properties. 
 * @author Takashi Nakamoto
 */
public class EditItemsCommand implements IUndoableCommand {
    final private ModelItem[] items;
    final private ArrayList<ModelItem> oldItems;
    final private ArrayList<Integer> oldBufferSizes;
    final private EditItemsDialog.Result result;
   
    /**
     * Initialize command.
     * @param shell Shell for error message dialogs.
     * @param operations_manager Operations manager which manages undo/redo commands.
     * @param items Array of ModelItem instances subjected to editing.
     * @param result Result instance returned by EditItemsDialog.
     */
    public EditItemsCommand(
    		final OperationsManager operations_manager,
            final ModelItem[] items,
            final EditItemsDialog.Result result) {
    	this.items = items;
    	this.result = result;

    	// Save old values so that this operation can be undone later.
    	oldItems = new ArrayList<ModelItem>();
    	oldBufferSizes = new ArrayList<Integer>();
    	for (ModelItem item : items) {
    		oldItems.add(item.clone());
    		if (item instanceof PVItem) {
    			oldBufferSizes.add(((PVItem)item).getLiveCapacity());
    		} else {
    			oldBufferSizes.add(0);
    		}
    	}
    	
    	// Edit items
    	applyChanges();
    	
    	operations_manager.addCommand(this);
    }

	@Override
	public void undo() {
		for (int i=0; i<items.length; i++) {
			ModelItem item = items[i];
			ModelItem oldItem = oldItems.get(i);
			
			item.setVisible(oldItem.isVisible());
			try {
				item.setName(oldItem.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			item.setDisplayName(oldItem.getDisplayName());
			item.setColor(oldItem.getColor());
			item.setLineWidth(oldItem.getLineWidth());
			item.setAxis(oldItem.getAxis());
			item.setTraceType(oldItem.getTraceType());
			item.setWaveformIndex(oldItem.getWaveformIndex());
			if (item instanceof PVItem) {
				try {
					((PVItem)item).setScanPeriod(((PVItem)oldItem).getScanPeriod());
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					((PVItem)item).setLiveCapacity(oldBufferSizes.get(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
				((PVItem)item).setRequestType(((PVItem)oldItem).getRequestType());
			}
		}
	}

	@Override
	public void redo() {
		applyChanges();
	}
	
	public void applyChanges() {
		for (ModelItem item : items) {
			if (result.appliedVisible())
				item.setVisible(result.isVisible());
			if (result.appliedItem()) {
				try {
					item.setName(result.getItem());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (result.appliedDisplayName())
				item.setDisplayName(result.getDisplayName());
			if (result.appliedColor())
				item.setColor(result.getColor());
			if (result.appliedScan() && item instanceof PVItem) {
				try {
					((PVItem)item).setScanPeriod(result.getScan());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (result.appliedBufferSize() && item instanceof PVItem) {
				try {
					((PVItem)item).setLiveCapacity(result.getBufferSize());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (result.appliedWidth())
				item.setLineWidth(result.getWidth());
			if (result.appliedAxis())
				item.setAxis(result.getAxis());
			if (result.appliedTraceType())
				item.setTraceType(result.getTraceType());
			if (result.appliedRequest() && item instanceof PVItem)
				((PVItem)item).setRequestType(result.getRequest());
			if (result.appliedIndex())
				item.setWaveformIndex(result.getIndex());
			if (result.appliedErrorType() && item instanceof PVItem)
				((PVItem)item).setErrorType(result.getErrorType());
		}
	}
}
