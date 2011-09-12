
package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class DuplicateConfigurationBeanAction extends Action implements
		IViewActionDelegate {

	private static ConfigurationBeanService beanService;

	public static void staticInject(final ConfigurationBeanService bean) {
		DuplicateConfigurationBeanAction.beanService = bean;
	}

	private IConfigurationBean bean;

	@Override
    public void init(final IViewPart view) {
		// TODO Auto-generated method stub
	}

	@Override
    public void run(final IAction action) {
		ConfigurationEditorInput editorInput;
		try {
			IConfigurationBean duplicateBean = this.bean.getClone();
			duplicateBean.setID(-1);
			duplicateBean.setDisplayName(duplicateBean.getDisplayName()
					+ "-" + Messages.DuplicateConfigurationBeanAction_copy); //$NON-NLS-1$
			final IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			try {
				duplicateBean = DuplicateConfigurationBeanAction.beanService
						.save(duplicateBean);
			} catch (final Throwable e) {
				final MessageBox messageBox = new MessageBox(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell());
				messageBox.setText(e.getClass().toString());
				messageBox.setMessage(e.getMessage());
			}
			editorInput = new ConfigurationEditorInput(duplicateBean);

			final String editorId = BeanToEditorId.getEnumForClass(
					this.bean.getClass()).getEditorId();

			activePage.openEditor(editorInput, editorId);
		} catch (final PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
    public void selectionChanged(final IAction action,
			final ISelection selection) {
		final IStructuredSelection sSelection = (IStructuredSelection) selection;
		final Object source = sSelection.getFirstElement();
		this.bean = (IConfigurationBean) source;
	}
}
