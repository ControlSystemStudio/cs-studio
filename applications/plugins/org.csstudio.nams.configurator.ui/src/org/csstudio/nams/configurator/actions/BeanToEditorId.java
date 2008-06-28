package org.csstudio.nams.configurator.actions;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.editor.AlarmbearbeiterEditor;
import org.csstudio.nams.configurator.editor.AlarmbearbeitergruppenEditor;
import org.csstudio.nams.configurator.editor.AlarmtopicEditor;
import org.csstudio.nams.configurator.editor.FilterEditor;
import org.csstudio.nams.configurator.editor.FilterbedingungEditor;

public enum BeanToEditorId {
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