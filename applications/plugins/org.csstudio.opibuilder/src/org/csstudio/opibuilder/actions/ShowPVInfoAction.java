package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.utility.pv.PV;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**Show details information of widget's primary PV.
 * @author Xihui Chen
 *
 */
public class ShowPVInfoAction implements IObjectActionDelegate {

	private IStructuredSelection selection;
	private IWorkbenchPart targetPart;
	
	public ShowPVInfoAction() {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void run(IAction action) {
		if(getSelectedWidget() == null || 
				getSelectedWidget().getPV(AbstractPVWidgetModel.PROP_PVNAME) == null){
			MessageDialog.openError(null, "Error", "No related PV information");
			return;
		}
		StringBuilder sb = new StringBuilder();
		
		PV pv = getSelectedWidget().getPV(AbstractPVWidgetModel.PROP_PVNAME);
		
		sb.append("Name: " + pv.getName() + "\n"); //$NON-NLS-2$
		sb.append("State: " + pv.getStateInfo()+ "\n"); //$NON-NLS-2$
		sb.append("Connected: " + pv.isConnected()+ "\n"); //$NON-NLS-2$
		sb.append("Running: " + pv.isRunning()+ "\n"); //$NON-NLS-2$
		if(pv.isConnected()){
			sb.append("Value: " + pv.getValue()+ "\n"); //$NON-NLS-2$
			sb.append("Meta Data: " + pv.getValue().getMetaData());
		}
		
		
		MessageDialog.openInformation(targetPart.getSite().getShell(), "PV Info", sb.toString());
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}
	
	private AbstractPVWidgetEditPart getSelectedWidget(){ 
		if(selection.getFirstElement() instanceof AbstractPVWidgetEditPart){
			return (AbstractPVWidgetEditPart)selection.getFirstElement();
		}else
			return null;
	}

}
