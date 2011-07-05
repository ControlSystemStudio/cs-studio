
package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class DeleteConfugurationBeanAction extends Action implements
		IViewActionDelegate {

	private static ConfigurationBeanService configurationBeanService;

	public static void staticInject(final ConfigurationBeanService service) {
		DeleteConfugurationBeanAction.configurationBeanService = service;
	}

	private IConfigurationBean bean;

	public DeleteConfugurationBeanAction() {
	    // Nothing to do
	}

	@Override
    public void init(final IViewPart view) {
        // Nothing to do
	}

	@Override
    public void run(final IAction action) {
		if (this.bean != null) {
			try {
				if (DeleteConfugurationBeanAction.configurationBeanService
						.isDeleteable(this.bean)) {
					DeleteConfugurationBeanAction.configurationBeanService
							.delete(this.bean);
				} else {
					// message box mit info das es nicht gelöscht werden kann
					MessageDialog
							.openWarning(
									Display.getDefault().getActiveShell(),
									Messages.DeleteConfugurationBeanAction_title,
									Messages.DeleteConfugurationBeanAction_text);
				}
			} catch (final StorageError e) {
				throw new RuntimeException("failed to delete", e); //$NON-NLS-1$
			} catch (final StorageException e) {
				throw new RuntimeException("failed to delete", e); //$NON-NLS-1$
			} catch (final InconsistentConfigurationException e) {
				throw new RuntimeException("failed to delete", e); //$NON-NLS-1$
			}
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
