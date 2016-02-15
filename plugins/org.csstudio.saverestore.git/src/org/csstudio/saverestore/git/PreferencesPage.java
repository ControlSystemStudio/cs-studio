package org.csstudio.saverestore.git;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <code>PreferencesPage</code> provides means to set the properties for the git data provider. These properties include
 * the remote repository URL, destination folder etc.
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
        setMessage("Save and Restore Git Properties");
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

        StringFieldEditor url = new StringFieldEditor(Activator.PREF_URL, "Git repository URL or path:", parent) {
            @Override
            protected boolean doCheckState() {
                String txt = getTextControl().getText();
                try {
                    URL url = new URL(txt);
                    return !url.getHost().isEmpty() && !url.getPath().isEmpty();
                } catch (MalformedURLException e) {
                    File file = new File(txt);
                    return file.exists();
                }
            }
        };
        url.setEmptyStringAllowed(false);
        addField(url);
        StringButtonFieldEditor destination = new StringButtonFieldEditor(Activator.PREF_DESTINATION,
            "Local git working folder (empty for default setting):", parent) {
            @Override
            protected String changePressed() {
                IPath startPath = new Path(getTextControl().getText());
                ResourceSelectionDialog rsDialog = new ResourceSelectionDialog(Display.getCurrent().getActiveShell(),
                    "Choose File", new String[0]);
                rsDialog.setSelectedResource(startPath);
                return rsDialog.open() == Window.OK ? rsDialog.getSelectedResource().toPortableString() : null;
            }
        };

        addField(destination);
        addField(new BooleanFieldEditor(Activator.PREF_AUTOMATIC_SYNC,
            "Automatically synchronise repository after every save?", parent));
    }

}
