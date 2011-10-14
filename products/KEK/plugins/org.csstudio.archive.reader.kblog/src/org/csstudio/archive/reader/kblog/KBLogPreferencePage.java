package org.csstudio.archive.reader.kblog;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Preference Page for KBLog settings
 * 
 * @author Takashi Nakamoto
 */
public class KBLogPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public KBLogPreferencePage() {
		super(GRID);
		setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(), Activator.ID));
	}
	
	@Override
	protected void createFieldEditors() {
		setMessage("KBLog");
		final Composite parent = getFieldEditorParent();
		addField(new StringFieldEditor(KBLogPreferences.PATH_TO_KBLOGRD, "Path to kblogrd: ", parent));
	}

	@Override
	public void init(IWorkbench workbench) {
		// nothing to do
	}

}
