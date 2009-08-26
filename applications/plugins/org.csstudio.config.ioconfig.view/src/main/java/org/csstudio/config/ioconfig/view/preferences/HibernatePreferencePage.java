package org.csstudio.config.ioconfig.view.preferences;

import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.*;
import org.csstudio.config.ioconfig.model.Activator;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 24.04.2009
 */
public class HibernatePreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    public HibernatePreferencePage() {
        super(GRID);
        ScopedPreferenceStore prefStore = new ScopedPreferenceStore(new InstanceScope(), Activator
                .getDefault().getBundle().getSymbolicName());
        setPreferenceStore(prefStore);
        setDescription("Hibernate Settings for the IO Configurator to the Device Database");
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
     * manipulate various types of preferences. Each field editor knows how to save and restore
     * itself.
     */
    public void createFieldEditors() {
        addField(new StringFieldEditor(DDB_USER_NAME, "DDB User &Name:", getFieldEditorParent()));
        StringFieldEditor editor = new StringFieldEditor(DDB_PASSWORD, "DDB &Password:", getFieldEditorParent());
        addField(new IntegerFieldEditor(DDB_TIMEOUT, "DDB &Timeout", getFieldEditorParent(),3));
        editor.getTextControl(getFieldEditorParent()).setEchoChar('*');
        addField(editor);
        addField(new StringFieldEditor(HIBERNATE_CONNECTION_DRIVER_CLASS,
                "Hibernate &connection driver:", getFieldEditorParent()));
        addField(new StringFieldEditor(DIALECT, "&Dialect:", getFieldEditorParent()));
        editor = new MultiLineStringFieldEditor(HIBERNATE_CONNECTION_URL, "&URL:", getFieldEditorParent());
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, true);
        layoutData.widthHint = 360;
        editor.getTextControl(getFieldEditorParent()).setLayoutData(layoutData);
        addField(editor);
        addField(new BooleanFieldEditor(SHOW_SQL, "&Show SQL", getFieldEditorParent()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }

}
