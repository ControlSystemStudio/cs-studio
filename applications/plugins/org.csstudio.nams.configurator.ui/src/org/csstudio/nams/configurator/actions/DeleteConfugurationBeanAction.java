package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
	}

	public void init(final IViewPart view) {

	}

	public void run(final IAction action) {
		if (this.bean != null) {
			try {
				DeleteConfugurationBeanAction.configurationBeanService
						.delete(this.bean);
			} catch (final StorageError e) {
				throw new RuntimeException("failed to delete", e);
			} catch (final StorageException e) {
				throw new RuntimeException("failed to delete", e);
			} catch (final InconsistentConfigurationException e) {
				throw new RuntimeException("failed to delete", e);
			}
		}
	}

	public void selectionChanged(final IAction action,
			final ISelection selection) {
		final IStructuredSelection sSelection = (IStructuredSelection) selection;
		final Object source = sSelection.getFirstElement();
		this.bean = (IConfigurationBean) source;
	}

}
