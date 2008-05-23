package org.csstudio.nams.configurator.treeviewer.actions;

import java.util.Collection;

import org.csstudio.nams.configurator.editor.ConfigurationEditor;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.csstudio.nams.configurator.treeviewer.model.IConfigurationBean;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenConfigurationEditor extends Action {

	private final Collection<String> groupNames;
	private final IConfigurationBean bean;

	public OpenConfigurationEditor(IConfigurationBean bean,
			Collection<String> groupNames) {
		this.bean = bean;
		this.groupNames = groupNames;
	}

	@Override
	public void run() {

		ConfigurationEditorInput editorInput = new ConfigurationEditorInput(
				bean, this.groupNames);

		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart activeEditor = activePage.getActiveEditor();

		if (activeEditor instanceof ConfigurationEditor) {
			activePage.closeEditor(activeEditor, true);
		}

		try {
			activePage.openEditor(editorInput, ConfigurationEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}
}
