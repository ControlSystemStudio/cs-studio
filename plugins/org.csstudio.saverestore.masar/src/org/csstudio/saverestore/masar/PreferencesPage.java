package org.csstudio.saverestore.masar;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <code>PreferencesPage</code> provides means to set the properties for the masar data provider.
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
        setPreferenceStore(Activator.getInstance().getPreferenceStore());
        setMessage("Save and Restore MASAR Properties");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        addField(new ServicesFieldEditor(parent));
    }

}
