package org.csstudio.saverestore;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 *
 * <code>PreferencesPage</code> is an empty preference page for save and restore to which other pages can be plugged in.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class PreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    /**
     * Constructs a new preferences page.
     */
    public PreferencesPage() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(SaveRestoreService.getInstance().getPreferences());
        setMessage("Save and Restore Properties");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {
        // nothing to initialise
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        addField(new IntegerFieldEditor(SaveRestoreService.PREF_NUMBER_OF_SNAPSHOTS,
            "Number of snapshots loaded at once (all=0)", parent));

    }

}
