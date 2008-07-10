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
public class NewBeanAction extends Action implements
		IViewActionDelegate {

	private Class<IConfigurationBean> bean;

	public NewBeanAction() {

	}

	public void init(IViewPart view) {

	}

	public void run(IAction action) {
		ConfigurationEditorInput editorInput;
		try {
			editorInput = new ConfigurationEditorInput(bean.newInstance());

			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			String editorId = BeanToEditorId.getEnumForClass(bean)
					.getEditorId();

			activePage.openEditor(editorInput, editorId);
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		IStructuredSelection sSelection = (IStructuredSelection) selection;
		Object source = sSelection.getFirstElement();
		bean = (Class<IConfigurationBean>) source.getClass();
	}

}
