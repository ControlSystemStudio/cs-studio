package org.csstudio.display.pvtable.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.Preferences;
import org.csstudio.display.pvtable.model.Measure;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VString;
import org.diirt.vtype.ValueFactory;

/**
 *
 * @author A.PHILIPPE, L.PHILIPPE GANIL/FRANCE
 */
public class MeasureAction extends PVTableAction {
    public MeasureAction(final TableViewer viewer) {
        super(Messages.MeasureAction, "icons/measure.png", viewer); //$NON-NLS-1$
        setToolTipText(Messages.MeasureAction_TT);
    }

    @Override
    public void run() {
        final PVTableModel model = (PVTableModel) viewer.getInput();
        if (model == null) {
            return;
        }
        if (model.getConfig() == null) {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.MeasureImpossible,
                    Messages.MeasureImpossible_NoConf);
            return;
        }
        model.saveConf();
        ;
        Measure measure = model.getConfig().addMeasure();
        VString value = ValueFactory.newVString("", ValueFactory.newAlarm(AlarmSeverity.NONE, ""),
                ValueFactory.timeNow());
        PVTableItem measureHeader;
        measureHeader = model.addItem("#mesure#" + Messages.Measure + " " + model.getNbMeasure(), value);
        measure.getItems().add(measureHeader);
        measureHeader.setMeasure(measure);
        for (PVTableItem configItem : model.getConfig().getItems()) {
            if (configItem.isSelected() && !configItem.isConfHeader()) {
                PVTableItem measureItem = model.addItem(configItem.getName(), Preferences.getTolerance(),
                        configItem.getSavedValue().get(), configItem.getTime_saved(), false, measure);
                measure.getItems().add(measureItem);
            }
        }
        viewer.setSelection(null);
        viewer.setItemCount(model.getItemCount());
        viewer.refresh();
        IRunnableWithProgress operation = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().doSave(monitor);
            }
        };
        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, false, operation);
        } catch (InvocationTargetException | InterruptedException e) {
            Plugin.getLogger().log(Level.WARNING, "Cannot auto-save after measure.", e);
        }
    }
}