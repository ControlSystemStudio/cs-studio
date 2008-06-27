package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.editor.AlarmbearbeiterEditor;
import org.csstudio.nams.configurator.editor.AlarmbearbeitergruppenEditor;
import org.csstudio.nams.configurator.editor.AlarmtopicEditor;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.csstudio.nams.configurator.editor.FilterEditor;
import org.csstudio.nams.configurator.editor.FilterbedingungEditor;
import org.csstudio.nams.configurator.modelmapping.IConfigurationBean;
import org.csstudio.nams.configurator.modelmapping.IConfigurationModel;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenConfigurationEditor extends Action {

	private final IConfigurationBean bean;
	private final IConfigurationModel model;

	public OpenConfigurationEditor(IConfigurationBean bean,
			IConfigurationModel model) {
		this.bean = bean;
		this.model = model;
	}

	@Override
	public void run() {

		ConfigurationEditorInput editorInput = new ConfigurationEditorInput(
				bean, this.model);

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
	
	enum BeanToEditorId {
		Alarmbearbeiter(AlarmbearbeiterBean.class, AlarmbearbeiterEditor.getId()),
		Alarmbearbeitergruppe(AlarmbearbeiterGruppenBean.class, AlarmbearbeitergruppenEditor.getId()),
		AlarmTopic(AlarmtopicBean.class, AlarmtopicEditor.getId()),
		Filter(FilterBean.class, FilterEditor.getId()),
		Filterbedingung(FilterbedingungBean.class, FilterbedingungEditor.getId());
		
		private Class<? extends IConfigurationBean> _clazz;
		private final String _editorId;

		private BeanToEditorId(Class<? extends IConfigurationBean> clazz, String editorId) {
			_clazz = clazz;
			_editorId = editorId;
		}
		
		public static BeanToEditorId getEnumForClass(Class<? extends IConfigurationBean> clazz) {
			for (BeanToEditorId value : BeanToEditorId.values()) {
				if (clazz.equals(value._clazz)) {
					return value;
				}
			}
			return null;
		}
		
		public String getEditorId() {
			return _editorId;
		}
	}
}
