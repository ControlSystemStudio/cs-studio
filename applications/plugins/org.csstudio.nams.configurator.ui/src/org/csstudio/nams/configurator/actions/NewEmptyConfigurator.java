package org.csstudio.nams.configurator.actions;

import org.eclipse.jface.action.Action;

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
