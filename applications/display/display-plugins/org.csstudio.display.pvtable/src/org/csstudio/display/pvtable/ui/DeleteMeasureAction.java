package org.csstudio.display.pvtable.ui;

import java.util.List;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.model.Configuration;
import org.csstudio.display.pvtable.model.Measure;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
/**
 * 
 * @author A.PHILIPPE, L.PHILIPPE GANIL/FRANCE
 */
public class DeleteMeasureAction extends PVTableAction {
	public DeleteMeasureAction(final TableViewer viewer) {
        super(Messages.DeleteMeasure, "icons/deleteMeasure.png", viewer); //$NON-NLS-1$
        setToolTipText(Messages.Delete_TT);
    }

    public void run()
    {
        final PVTableModel model = (PVTableModel) viewer.getInput();
        if (model == null) {
            return;
        }
        final IStructuredSelection select = (IStructuredSelection) viewer.getSelection();
        if (select == null) {
        	MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.ImpossibleToDelete, Messages.MeasureSelected);
            return;
        }
        
        for(int i = 0 ; i < select.size() ; i++) {
        	PVTableItem itemMeasure = (PVTableItem) select.toList().get(i);
        	Measure measure = itemMeasure.getMeasure();
        	if(measure == null) {
        		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.ImpossibleToDelete, Messages.MeasureSelected);
        		return;
        	}
        	Configuration conf = model.getConfig();
        	List<PVTableItem> itemsMeasure = measure.getItems();
        	
	        for(PVTableItem itemMes : itemsMeasure) {
	        	model.removeItem(itemMes);
	        }
	        conf.removeMeasure(measure);
        }
        viewer.setSelection(null);
        viewer.setItemCount(model.getItemCount());
        viewer.refresh();
    }
}
