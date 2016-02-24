package org.csstudio.channelfinder.preferences;

import org.csstudio.security.ui.PasswordFieldEditor;
import org.csstudio.utility.channelfinder.Activator;
import org.csstudio.utility.channelfinder.PreferenceConstants;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class ChannelFinderPreferencePage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

    StringFieldEditor urlField;
    FileFieldEditor truststoreField;

    public ChannelFinderPreferencePage() {
    super(GRID);
    setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID));
    setMessage("ChannelFinderAPI Client Preferences");
    setDescription("ChannelFinder preference page");
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    public void createFieldEditors() {
    urlField = new StringFieldEditor(PreferenceConstants.ChannelFinder_URL,
        "ChannelFinder Service URL:", getFieldEditorParent());
    // no need to override checkstate
    // urlField.setEmptyStringAllowed(false);
    addField(urlField);

    addField(new StringFieldEditor(PreferenceConstants.Username,
        "username:", getFieldEditorParent()));

    addField(new PasswordFieldEditor(Activator.PLUGIN_ID,
        PreferenceConstants.Password, "user password: ",
        getFieldEditorParent()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
    super.propertyChange(event);
    if (event.getProperty().equals(FieldEditor.VALUE)) {
        checkState();
    }
    }

    @Override
    public boolean performOk() {
    boolean ret = super.performOk();
    return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }
}