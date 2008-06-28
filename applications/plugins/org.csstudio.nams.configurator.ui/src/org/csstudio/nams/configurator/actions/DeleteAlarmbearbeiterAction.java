package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class DeleteAlarmbearbeiterAction extends Action implements IViewActionDelegate {

	private static ConfigurationBeanService configurationBeanService;
	private AlarmbearbeiterBean bean;
	
	public DeleteAlarmbearbeiterAction() {
	}

	public void init(IViewPart view) {
		
	}

	public void run(IAction action) {
		if(bean != null) {
			configurationBeanService.delete(bean);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		IStructuredSelection sSelection = (IStructuredSelection) selection;
		Object source = sSelection.getFirstElement();
		bean = (AlarmbearbeiterBean) source;
	}
	
	public static void staticInject(ConfigurationBeanService service) {
		configurationBeanService = service;
	}

}
