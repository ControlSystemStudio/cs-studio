package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.composite.FilterableBeanList;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Öffnet den Editor zur Auswahl in der Auflistungs-View ({@link FilterableBeanList}).
 */
public class OpenConfigurationEditorAction extends Action {

	private final IConfigurationBean bean;

	public OpenConfigurationEditorAction(IConfigurationBean bean) {
		this.bean = bean;
	}

	@Override
	public void run() {

		ConfigurationEditorInput editorInput = new ConfigurationEditorInput(bean);

		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		String editorId = BeanToEditorId.getEnumForClass(bean.getClass()).getEditorId();
		
		try {
			activePage.openEditor(editorInput, editorId);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}
	
	
}
