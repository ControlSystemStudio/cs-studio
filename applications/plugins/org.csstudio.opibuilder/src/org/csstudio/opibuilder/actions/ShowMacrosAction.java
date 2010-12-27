package org.csstudio.opibuilder.actions;

import java.util.Map;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**Show the predefined macros of the selected widget in console and message dialog.
 * @author Xihui Chen
 *
 */
public class ShowMacrosAction implements IObjectActionDelegate {

	private IStructuredSelection selection;
	private IWorkbenchPart targetPart;	
	


	public void run(IAction action) {
		AbstractWidgetModel widget = getSelectedWidget().getWidgetModel();
		String message = NLS.bind("The predefined macros of {0}:\n", widget.getName());
		StringBuilder sb = new StringBuilder(message);
		Map<String, String> macroMap = OPIBuilderMacroUtil.getWidgetMacroMap(widget);
		for(final Map.Entry<String, String> entry: macroMap.entrySet()){
			sb.append(entry.getKey() + "=" + entry.getValue() + "\n");
		}
		ConsoleService.getInstance().writeInfo(sb.toString());
		MessageDialog.openInformation(targetPart.getSite().getShell(),
				"Predefined Macros", sb.toString());		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}


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
