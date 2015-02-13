package org.csstudio.askap.chat;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PreferencePage() {
        super(GRID);
        final IScopeContext scope = InstanceScope.INSTANCE;
        // 'main' pref. store for most of the settings
		setPreferenceStore(new ScopedPreferenceStore(scope, Activator.PLUGIN_ID));
	}


	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		setMessage("Log Viewer Settings");
		final Composite parent = getFieldEditorParent();
		addField(new StringFieldEditor(Preferences.CHAT_JMS_SERVER_URL, "Chat JMS server URL:", parent));
		addField(new StringFieldEditor(Preferences.CHAT_JMS_MESSAGE_TOPIC, "Chat JMS Messagee Topic Name:", parent));
		addField(new StringFieldEditor(Preferences.CHAT_JMS_HEART_BEAT_TOPIC, "Chat JMS Heartbeat Topic Name:", parent));
		addField(new StringFieldEditor(Preferences.CHAT_HEART_BEAT_PERIOD, "Chat JMS Heartbeat check period:", parent));
		addField(new StringFieldEditor(Preferences.CHAT_HEART_BEAT_MIN_PERIOD, "Chat JMS Heartbeat min period:", parent));
		addField(new IntegerFieldEditor(Preferences.CHAT_MESSAGE_TIME_TO_LIVE, "Chat Message Time to Live (ms):", parent));
	}

}
