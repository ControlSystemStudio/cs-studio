package org.csstudio.display.pvtable.ui;

import java.util.List;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.model.Configuration;
import org.csstudio.display.pvtable.model.Measure;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
/**
 * 
 * @author A.PHILIPPE, L.PHILIPPE GANIL/FRANCE
 */
public class DeleteLastMeasureAction extends PVTableAction {
	public DeleteLastMeasureAction (final TableViewer viewer) {
		super(Messages.DeleteLastMeasure, "icons/deleteMeasure.png", viewer);
		setToolTipText(Messages.DeleteLastMeasure_TT);
	}
	
	@Override
	public void run() {
		final PVTableModel model = (PVTableModel) viewer.getInput();
        if (model == null) {
            return;
        }
        if(model.getConfig() == null) {
        	MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.InformationPopup, Messages.InformationPopup_NoConfToDel);
        	return;
        }
        if(model.getConfig().getMeasures().isEmpty() || model.getConfig().getMeasures().size() == 0){
        	MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.InformationPopup, Messages.InformationPopup_NoMeasureToDel);
        	return;
        }
        Configuration conf = model.getConfig();
        if (conf.getMeasures().size() == 0) {
        	return;
        }
        Measure lastMeasure = conf.getMeasures().get(conf.getMeasures().size()-1);
        List<PVTableItem> itemsMeasure = lastMeasure.getItems();
        for(PVTableItem itemMes : itemsMeasure) {
        	model.removeItem(itemMes);
        }
        conf.removeMeasure(lastMeasure);
        viewer.setItemCount(model.getItemCount());
        viewer.refresh();
		return;
	}
}