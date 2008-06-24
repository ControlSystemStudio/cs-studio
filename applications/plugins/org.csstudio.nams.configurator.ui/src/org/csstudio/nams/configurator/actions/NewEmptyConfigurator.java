package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.ConfigurationModel;
import org.csstudio.nams.configurator.editor.ConfigurationEditor;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class NewEmptyConfigurator<IConfigurationBean> extends Action{
	
	private Class<IConfigurationBean> beanType;

	public void setType(Class<IConfigurationBean> beanType){
		this.beanType = beanType;
	}
	
	@Override
	public void run(){
//		IConfigurationBean newBean = beanType.newInstance();
		
//		ConfigurationEditorInput editorInput = new ConfigurationEditorInput(
//				newBean , new ConfigurationModel(null));
//
//		IWorkbenchPage activePage = PlatformUI.getWorkbench()
//				.getActiveWorkbenchWindow().getActivePage();
//		IEditorPart activeEditor = activePage.getActiveEditor();
//		//
//		IConfigurationBean openEditorBean = null;
//		if (activeEditor instanceof ConfigurationEditor) {
//			ConfigurationEditor editor = (ConfigurationEditor) activeEditor;
//			openEditorBean = ((ConfigurationEditorInput) editor
//					.getEditorInput()).getBean();
//
//		}
//
//		if (openEditorBean != null && openEditorBean.equals(this.bean)) {
//			activePage.activate(activeEditor);
//		} else {
//
//			try {
//				activePage.openEditor(editorInput, ConfigurationEditor.ID);
//			} catch (PartInitException e) {
//				e.printStackTrace();
//			}
//		}
	}
}
