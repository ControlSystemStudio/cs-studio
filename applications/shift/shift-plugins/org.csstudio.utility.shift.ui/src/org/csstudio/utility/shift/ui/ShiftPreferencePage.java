package org.csstudio.utility.shift.ui;

import org.csstudio.utility.shift.PreferenceConstants;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class ShiftPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor urlField;

	public ShiftPreferencePage() {
		super(GRID);
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE,
				org.csstudio.utility.shift.Activator.PLUGIN_ID));
		setMessage("Shift Client Preferences");
		setDescription("Shift preference page");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		urlField = new StringFieldEditor(PreferenceConstants.Shift_URL,
				"Shift Service URL:", getFieldEditorParent());
		// no need to override checkstate
		urlField.setEmptyStringAllowed(false);
		addField(urlField);
	}


	@Override
	protected void initialize() {
		super.initialize();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
	}

	@Override
	public void setVisible(boolean visible) {
		// Override it to enable/disable user and password field depending on
		// useAuthenticationField
		super.setVisible(visible);
	}

}
