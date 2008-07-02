package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class DuplicateConfigurationBeanAction extends Action implements IViewActionDelegate{

	private IConfigurationBean bean;
	private static ConfigurationBeanService beanService;

	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action) {
		ConfigurationEditorInput editorInput;
		try {
			IConfigurationBean duplicateBean = bean.getClone();
			duplicateBean.setID(-1);
			duplicateBean = beanService.save(duplicateBean);
			editorInput = new ConfigurationEditorInput(duplicateBean);

			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			String editorId = BeanToEditorId.getEnumForClass((Class<IConfigurationBean>) bean.getClass())
					.getEditorId();

			activePage.openEditor(editorInput, editorId);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	
	public void selectionChanged(IAction action, ISelection selection) {
		IStructuredSelection sSelection = (IStructuredSelection) selection;
		Object source = sSelection.getFirstElement();
		bean = (IConfigurationBean) source;
	}

	public static void staticInject(ConfigurationBeanService beanService) {
		DuplicateConfigurationBeanAction.beanService = beanService;
	}
	
}
