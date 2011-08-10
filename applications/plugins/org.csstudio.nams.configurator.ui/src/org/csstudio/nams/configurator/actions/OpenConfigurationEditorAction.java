
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

	public OpenConfigurationEditorAction(final IConfigurationBean b) {
		this.bean = b;
	}

	@Override
	public void run() {

		final ConfigurationEditorInput editorInput = new ConfigurationEditorInput(
				this.bean);

		final IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		final String editorId = BeanToEditorId.getEnumForClass(
				this.bean.getClass()).getEditorId();

		try {
			activePage.openEditor(editorInput, editorId);
		} catch (final PartInitException e) {
			e.printStackTrace();
		}
	}
}
