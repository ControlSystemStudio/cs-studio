package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.editor.ConfigurationEditor;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.csstudio.nams.configurator.modelmapping.IConfigurationBean;
import org.csstudio.nams.configurator.modelmapping.IConfigurationModel;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
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

		IConfigurationBean openEditorBean = null;
		
		IEditorReference[] editorReferences = activePage.getEditorReferences();
		for (IEditorReference editorReference : editorReferences) {
			IEditorPart editorPart = editorReference.getEditor(false);
			
			if (editorPart instanceof ConfigurationEditor) {
				ConfigurationEditor editor = (ConfigurationEditor) editorPart;
				openEditorBean = ((ConfigurationEditorInput) editor
						.getEditorInput()).getBean();
				
			}
			
			if (openEditorBean != null && openEditorBean.equals(this.bean)) {
				activePage.activate(editorPart);
				return;
			}			
		}
		
		try {
			activePage.openEditor(editorInput, ConfigurationEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}
}
