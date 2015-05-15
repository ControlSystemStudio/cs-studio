package org.csstudio.sds.ui.internal.preferences;

import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class RuleFoldersPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    @Override
    protected void createFieldEditors() {
        addField(new FolderFieldEditor(PreferenceConstants.PROP_RULE_FOLDERS, "Additional folders containing rules", getFieldEditorParent()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return SdsUiPlugin.getCorePreferenceStore();
    }

    public void init(IWorkbench workbench) {
        // nothing to do
    }

}
