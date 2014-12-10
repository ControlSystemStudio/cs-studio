package org.csstudio.trends.databrowser2.propsheet;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.rtplot.TraceType;
import org.csstudio.swt.rtplot.undo.UndoableAction;
import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.model.RequestType;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.swt.graphics.RGB;

/** Undo-able command for edit item properties.
 *  @author Takashi Nakamoto - Original implementation that 'cloned' ModelItem
 *  @author Kay Kasemir - without 'clone', since data cannot be cloned
 */
public class EditItemsCommand extends UndoableAction
{
    final private List<ModelItem> items;
    final private EditItemsDialog.Result result;
    final private List<Boolean> wasVisible = new ArrayList<>();
    final private List<String> oldName = new ArrayList<>();
    final private List<String> oldDisplayName = new ArrayList<>();
    final private List<RGB> oldColor = new ArrayList<>();
    final private List<Integer> oldLineWidth = new ArrayList<>();
    final private List<AxisConfig> oldAxis = new ArrayList<>();
    final private List<TraceType> oldTraceType = new ArrayList<>();
    final private List<Integer> oldWaveformIndex = new ArrayList<>();
    final private List<Double> oldScanPeriod = new ArrayList<>();
    final private List<Integer> oldBufferSizes = new ArrayList<>();
    final private List<RequestType> oldRequestType = new ArrayList<>();

    /** Initialize command.
     *  @param shell Shell for error message dialogs.
     *  @param operations_manager Operations manager which manages undo/redo commands.
     *  @param items Array of ModelItem instances subjected to editing.
     *  @param result Result instance returned by EditItemsDialog.
     */
    public EditItemsCommand(
    		final UndoableActionManager operations_manager,
            final List<ModelItem> items,
            final EditItemsDialog.Result result)
    {
        super(Messages.EditItems);
    	this.items = items;
    	this.result = result;

    	// Save old values so that this operation can be undone later.
    	for (ModelItem item : items)
    	{
    	    wasVisible.add(item.isVisible());
    	    oldName.add(item.getName());
    	    oldDisplayName.add(item.getDisplayName());
    	    oldColor.add(item.getColor());
    	    oldLineWidth.add(item.getLineWidth());
    	    oldAxis.add(item.getAxis());
    	    oldTraceType.add(item.getTraceType());
    	    oldWaveformIndex.add(item.getWaveformIndex());
    		if (item instanceof PVItem)
    		{
    		    oldScanPeriod.add(((PVItem) item).getScanPeriod());
    			oldBufferSizes.add(((PVItem)item).getLiveCapacity());
    			oldRequestType.add(((PVItem) item).getRequestType());
    		}
    		else
    		{
                oldScanPeriod.add(0.0);
                oldBufferSizes.add(0);
                oldRequestType.add(RequestType.RAW);
    		}
    	}
    	operations_manager.execute(this);
    }

    @Override
    public void run()
    {
        for (ModelItem item : items)
        {
            try
            {
                if (result.appliedVisible())
                    item.setVisible(result.isVisible());
                if (result.appliedItem())
                    item.setName(result.getItem());
                if (result.appliedDisplayName())
                    item.setDisplayName(result.getDisplayName());
                if (result.appliedColor())
                    item.setColor(result.getColor());
                if (result.appliedWidth())
                    item.setLineWidth(result.getWidth());
                if (result.appliedAxis())
                    item.setAxis(result.getAxis());
                if (result.appliedTraceType())
                    item.setTraceType(result.getTraceType());
                if (result.appliedIndex())
                    item.setWaveformIndex(result.getIndex());
                if (item instanceof PVItem)
                {
                    if (result.appliedScan())
                        ((PVItem)item).setScanPeriod(result.getScan());
                    if (result.appliedBufferSize())
                        ((PVItem)item).setLiveCapacity(result.getBufferSize());
                    if (result.appliedRequest())
                        ((PVItem)item).setRequestType(result.getRequest());
                }
            }
            catch (Exception ex)
            {
                ExceptionDetailsErrorDialog.openError(null, Messages.Error, ex);
            }
        }
    }

	@Override
	public void undo()
	{
		for (int i=0; i<items.size(); ++i)
		{
			final ModelItem item = items.get(i);
			try
			{
    			item.setVisible(wasVisible.get(i));
    			item.setName(oldName.get(i));
    			item.setDisplayName(oldDisplayName.get(i));
    			item.setColor(oldColor.get(i));
    			item.setLineWidth(oldLineWidth.get(i));
    			item.setAxis(oldAxis.get(i));
    			item.setTraceType(oldTraceType.get(i));
    			item.setWaveformIndex(oldWaveformIndex.get(i));
    			if (item instanceof PVItem)
    			{
    			    ((PVItem)item).setScanPeriod(oldScanPeriod.get(i));
    			    ((PVItem)item).setLiveCapacity(oldBufferSizes.get(i));
    				((PVItem)item).setRequestType(oldRequestType.get(i));
    			}
            }
            catch (Exception ex)
            {
                ExceptionDetailsErrorDialog.openError(null, Messages.Error, ex);
            }
		}
	}
}
