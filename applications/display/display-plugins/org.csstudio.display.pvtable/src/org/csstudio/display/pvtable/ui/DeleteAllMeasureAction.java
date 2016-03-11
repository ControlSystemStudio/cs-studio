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
public class DeleteAllMeasureAction extends PVTableAction {
    public DeleteAllMeasureAction(final TableViewer viewer) {
        super(Messages.DeleteAllMeasures, "icons/delAllMeasure.png", viewer); //$NON-NLS-1$
        setToolTipText(Messages.DeleteAllMeasures_TT);
    }

    @Override
    public void run() {
        final PVTableModel model = (PVTableModel) viewer.getInput();
        if (model == null) {
            return;
        }
        if (model.getConfig() == null && model.getNbMeasure() == 0) {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.InformationPopup,
                    Messages.InformationPopup_NoConfToDel);
            return;
        }
        if (model.getConfig().getMeasures().isEmpty() || model.getConfig().getMeasures().size() == 0) {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.InformationPopup,
                    Messages.InformationPopup_NoMeasureToDel);
            return;
        }
        boolean validation = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                Messages.QuestionPopupDelAllMeasure, Messages.QuestionPopupDelAllMeasure_TT);
        if (validation != true) {
            return;
        }
        Configuration conf = model.getConfig();
        List<Measure> allMeasures = conf.getMeasures();
        for (Measure measure : allMeasures) {
            List<PVTableItem> itemsMeasure = measure.getItems();
            for (PVTableItem itemMes : itemsMeasure) {
                model.removeItem(itemMes);
            }
        }
        conf.removeAllMeasures();
        model.setNbMeasure(1);
        viewer.setItemCount(model.getItemCount() + 1);
        viewer.refresh();
        return;
    }
}