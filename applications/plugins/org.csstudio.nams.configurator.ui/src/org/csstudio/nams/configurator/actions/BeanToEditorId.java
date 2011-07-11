
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

/**
 * Zuordnung der Editoren zu den Konfigurationselementen.
 * 
 * Note: Mapped Bean-Typen auf Eclipse-editor-extension-point-ids.
 */
public enum BeanToEditorId {
	Alarmbearbeiter(AlarmbearbeiterBean.class, AlarmbearbeiterEditor.getId()), 
	Alarmbearbeitergruppe(AlarmbearbeiterGruppenBean.class, AlarmbearbeitergruppenEditor.getId()),
	AlarmTopic(AlarmtopicBean.class, AlarmtopicEditor.getId()),
	Filter(FilterBean.class, FilterEditor.getId()),
	Filterbedingung(FilterbedingungBean.class, FilterbedingungEditor.getId());

	public static BeanToEditorId getEnumForClass(
			final Class<? extends IConfigurationBean> clazz) {
		for (final BeanToEditorId value : BeanToEditorId.values()) {
			if (clazz.equals(value._clazz)) {
				return value;
			}
		}
		return null;
	}

	private Class<? extends IConfigurationBean> _clazz;

	private final String _editorId;

	private BeanToEditorId(final Class<? extends IConfigurationBean> clazz,
			final String editorId) {
		this._clazz = clazz;
		this._editorId = editorId;
	}

	public String getEditorId() {
		return this._editorId;
	}
}