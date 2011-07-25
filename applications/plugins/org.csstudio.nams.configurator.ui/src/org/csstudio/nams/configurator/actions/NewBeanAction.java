package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

@Deprecated
public class NewBeanAction extends Action implements IViewActionDelegate {

	private Class<IConfigurationBean> bean;

	public NewBeanAction() {
        // Nothing to do
	}

	@Override
    public void init(final IViewPart view) {
        // Nothing to do
	}

	@Override
    public void run(final IAction action) {
		ConfigurationEditorInput editorInput;
		try {
			editorInput = new ConfigurationEditorInput(this.bean.newInstance());

			final IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			final String editorId = BeanToEditorId.getEnumForClass(this.bean)
					.getEditorId();

			activePage.openEditor(editorInput, editorId);
		} catch (final InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (final IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (final PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
    public void selectionChanged(final IAction action,
			final ISelection selection) {
		final IStructuredSelection sSelection = (IStructuredSelection) selection;
		final Object source = sSelection.getFirstElement();
		this.bean = (Class<IConfigurationBean>) source.getClass();
	}
}
