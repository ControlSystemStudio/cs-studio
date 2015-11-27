package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.PVWidgetEditpartDelegate;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class AcknowledgeAlarmAction implements IObjectActionDelegate {

    private IStructuredSelection selection;
    private IWorkbenchPart targetPart;

	@Override
	public void run(IAction action) {
        final AbstractBaseEditPart selectedWidget = getSelectedWidget();
        
		if(selectedWidget == null || selectedWidget.getWidgetModel() == null) return;
        if (!(selectedWidget.getWidgetModel() instanceof IPVWidgetModel) || !(selectedWidget instanceof AbstractPVWidgetEditPart)) return;
        
        IPVWidgetModel model = (IPVWidgetModel) selectedWidget.getWidgetModel();
        if (model.isAlarmPVEnabled() == false) return;
        
        PVWidgetEditpartDelegate pvDelegate = ((AbstractPVWidgetEditPart) selectedWidget).getPVWidgetEditpartDelegate();
        pvDelegate.acknowledgeAlarm();
//        MessageDialog.openInformation(null, "Acknowledge PV", "When implemented, this will acknowledge AlarmPV ");
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection) selection;
        }
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
	}

    private AbstractBaseEditPart getSelectedWidget(){
        if(selection.getFirstElement() instanceof AbstractBaseEditPart){
            return (AbstractBaseEditPart)selection.getFirstElement();
        }else
            return null;
    }
	
}
