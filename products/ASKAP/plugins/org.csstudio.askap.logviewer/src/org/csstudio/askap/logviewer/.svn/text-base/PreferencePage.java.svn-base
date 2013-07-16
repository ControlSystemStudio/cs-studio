package org.csstudio.askap.logviewer;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public PreferencePage() {
        super(GRID);

        final IScopeContext scope = InstanceScope.INSTANCE;
        // 'main' pref. store for most of the settings
		setPreferenceStore(new ScopedPreferenceStore(scope, Activator.ID));
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		setMessage("Log Viewer Settings");
		final Composite parent = getFieldEditorParent();
		addField(new StringFieldEditor(Preferences.LOG_MESSAGE_TOPIC_NAME, "Log Message Topic Name:", parent));
		addField(new StringFieldEditor(Preferences.LOG_SUBSCRIBER_END_POINT_NAME, "Log Subscriber End Point Name:", parent));
		addField(new IntegerFieldEditor(Preferences.LOG_VIEW_MAX_MESSAGES, "Max Number of Messages:", parent));
		addField(new IntegerFieldEditor(Preferences.LOG_VIEW_REFRESH_PERIOD, "Table Refresh period (ms):", parent));
		
		addField(new StringFieldEditor(Preferences.LOG_QUERY_ADAPTOR_NAME, "Log Query Adaptor Name:", parent));
		addField(new IntegerFieldEditor(Preferences.LOG_QUERY_MAX_MESSAGES_PER_QUERY, "Max Number of Messages Per Query:", parent));
	}

}
