package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.IConfigurationModel;
import org.csstudio.nams.configurator.editor.ConfigurationEditor;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenConfigurationEditor extends Action {

	private final IConfigurationBean bean;
	private final IConfigurationModel model;

	public OpenConfigurationEditor(IConfigurationBean bean,
			IConfigurationModel model) {
		this.bean = bean;
		this.model = model;
	}

	@Override
	public void run() {

		ConfigurationEditorInput editorInput = new ConfigurationEditorInput(
				bean, this.model);

		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart activeEditor = activePage.getActiveEditor();
		//
		IConfigurationBean openEditorBean = null;
		if (activeEditor instanceof ConfigurationEditor) {
			ConfigurationEditor editor = (ConfigurationEditor) activeEditor;
			openEditorBean = ((ConfigurationEditorInput) editor
					.getEditorInput()).getBean();

		}

		if (openEditorBean != null && openEditorBean.equals(this.bean)) {
			activePage.activate(activeEditor);
		} else {

			try {
				activePage.openEditor(editorInput, ConfigurationEditor.ID);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}

	}
}
