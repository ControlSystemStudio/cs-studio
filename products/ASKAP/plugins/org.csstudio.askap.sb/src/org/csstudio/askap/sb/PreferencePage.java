package org.csstudio.askap.sb;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
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
		setMessage("Scheduling Block Maintenance Configuration");
		final Composite parent = getFieldEditorParent();
		addField(new StringFieldEditor(Preferences.OBS_DEFAULT_PROGRAM_NAME, "Default Obs Program:", parent));
		addField(new StringFieldEditor(Preferences.EPHEMERIS_ICE_NAME, "Ephemeris Service Ice Name:", parent));
		
		addField(new StringFieldEditor(Preferences.EXECUTIVE_MONITOR_ICE_NAME, "Executive Status Monitor Ice Name:", parent));
		addField(new StringFieldEditor(Preferences.EXECUTIVE_MONITOR_POINT_NAME, "Executive Status Monitor Point Name:", parent));

		addField(new StringFieldEditor(Preferences.OPL_MONITOR_ICE_NAME, "OPL Status Monitor Ice Name:", parent));

		addField(new StringFieldEditor(Preferences.EXECUTIVE_LOG_SUBSCRIBER_NAME, "Executive Log Subscriber Name:", parent));
		addField(new StringFieldEditor(Preferences.EXECUTIVE_LOG_TOPIC_NAME, "Executive Log Topic Name:", parent));
		addField(new StringFieldEditor(Preferences.EXECUTIVE_LOG_ORIGIN, "Executive Log origin:", parent));
		
		addField(new StringFieldEditor(Preferences.SB_TEMPLATE_ICE_NAME, "SB Template Service Ice Name:", parent));
		addField(new StringFieldEditor(Preferences.SCHEDULING_BLOCK_ICE_NAME, "SB Service Ice Name:", parent));
		
		addField(new StringFieldEditor(Preferences.OBS_PROGRAM_ICE_NAME, "Obs Program Service Ice Name:", parent));
		
		addField(new StringFieldEditor(Preferences.EXECUTIVE_ICE_NAME, "Executive Service Ice Name:", parent));
		
		addField(new StringFieldEditor(Preferences.SB_EXECUTION_STATE_POLLING_PERIOD, "Executive Polling Period(ms):", parent));
		addField(new StringFieldEditor(Preferences.SB_EXECUTION_MAX_NUMBER_SB, "Max Number of executed SB to display:", parent));
		
		addField(new StringFieldEditor(Preferences.SB_MAINTENANCE_POLLING_PERIOD, "SB Polling Period(ms):", parent));
		addField(new StringFieldEditor(Preferences.SOURCE_SEARCH_MAX_MESSAGES, "Max Number of Messages per Source Search:", parent));
		
	}

}
