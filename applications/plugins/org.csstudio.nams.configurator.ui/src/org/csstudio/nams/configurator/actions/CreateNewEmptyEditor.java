package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.editor.ConfigurationEditor;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
@Deprecated
public class CreateNewEmptyEditor extends AbstractHandler implements IHandler {
	@Deprecated
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		IConfigurationBean bean = new AlarmbearbeiterBean();
		ConfigurationEditorInput editorInput = new ConfigurationEditorInput(bean);

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

		if (openEditorBean != null && openEditorBean.equals(bean)) {
			activePage.activate(activeEditor);
		} else {

			try {
				activePage.openEditor(editorInput, ConfigurationEditor.ID);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
