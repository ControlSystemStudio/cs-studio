package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenConfigurationEditorAction extends Action {

	private final IConfigurationBean bean;

	public OpenConfigurationEditorAction(IConfigurationBean bean) {
		this.bean = bean;
	}

	@Override
	public void run() {

		ConfigurationEditorInput editorInput = new ConfigurationEditorInput(bean);

		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

//		IConfigurationBean openEditorBean = null;
//		
//		IEditorReference[] editorReferences = activePage.getEditorReferences();
//		for (IEditorReference editorReference : editorReferences) {
//			IEditorPart editorPart = editorReference.getEditor(false);
//			
//			if (editorPart instanceof ConfigurationEditor) {
//				ConfigurationEditor editor = (ConfigurationEditor) editorPart;
//				openEditorBean = ((ConfigurationEditorInput) editor
//						.getEditorInput()).getBean();
//				
//			}
//			
//			if (openEditorBean != null && openEditorBean.equals(this.bean)) {
//				activePage.activate(editorPart);
//				return;
//			}			
//		}
		
		String editorId = BeanToEditorId.getEnumForClass(bean.getClass()).getEditorId();
		
		try {
			activePage.openEditor(editorInput, editorId);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}
	
	
}
