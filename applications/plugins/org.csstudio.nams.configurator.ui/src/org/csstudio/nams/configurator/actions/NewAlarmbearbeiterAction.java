package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class NewAlarmbearbeiterAction extends Action implements IViewActionDelegate {

	public NewAlarmbearbeiterAction() {
		
	}

	public void init(IViewPart view) {
		
	}

	public void run(IAction action) {
		ConfigurationEditorInput editorInput = new ConfigurationEditorInput(new AlarmbearbeiterBean());

		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		String editorId = BeanToEditorId.getEnumForClass(AlarmbearbeiterBean.class).getEditorId();
		
		try {
			activePage.openEditor(editorInput, editorId);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}

}
