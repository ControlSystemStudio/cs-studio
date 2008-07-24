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

public class DeleteConfugurationBeanAction extends Action implements IViewActionDelegate {

	private static ConfigurationBeanService configurationBeanService;
	private IConfigurationBean bean;
	
	public DeleteConfugurationBeanAction() {
	}

	public void init(IViewPart view) {
		
	}

	public void run(IAction action) {
		if(bean != null) {
			try {
				configurationBeanService.delete(bean);
			} catch (StorageError e) {
				throw new RuntimeException("failed to delete", e);
			} catch (StorageException e) {
				throw new RuntimeException("failed to delete", e);
			} catch (InconsistentConfigurationException e) {
				throw new RuntimeException("failed to delete", e);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		IStructuredSelection sSelection = (IStructuredSelection) selection;
		Object source = sSelection.getFirstElement();
		bean = (IConfigurationBean) source;
	}
	
	public static void staticInject(ConfigurationBeanService service) {
		configurationBeanService = service;
	}

}
