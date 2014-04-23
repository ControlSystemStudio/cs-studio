package org.csstudio.utility.scan.ui;


import org.csstudio.utility.scan.PreferenceConstants;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor urlField;


	public PreferencePage() {
		super(GRID);
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE,
				org.csstudio.utility.scan.Activator.PLUGIN_ID));
		setMessage("Scan Client Preferences");
		setDescription("Scan preference page");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		urlField = new StringFieldEditor(PreferenceConstants.Scan_URL,
				"Scan Service URL:", getFieldEditorParent());
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
		super.setVisible(visible);
	}

}
