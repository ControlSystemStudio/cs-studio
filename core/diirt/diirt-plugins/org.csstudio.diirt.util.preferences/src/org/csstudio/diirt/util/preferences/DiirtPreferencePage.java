package org.csstudio.diirt.util.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.jface.preference.PathEditor;

public class DiirtPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public DiirtPreferencePage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor("diirt.home", "&Diirt configuration directory:", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		final IPreferenceStore store = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, org.csstudio.diirt.util.Activator.ID);
		store.addPropertyChangeListener((PropertyChangeEvent event) -> {
			if (event.getProperty() == "diirt.home") {
				setMessage("Restart is needed", ERROR);
			}
		});
		setPreferenceStore(store);
		setDescription("Diirt preference page");
	}	

}
