/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore;

import org.eclipse.jface.preference.BooleanFieldEditor;
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
        IntegerFieldEditor numberOfSnapshots = new IntegerFieldEditor(SaveRestoreService.PREF_NUMBER_OF_SNAPSHOTS,
            "Number of snapshots loaded at once (0 = all)", parent);
        numberOfSnapshots.getLabelControl(parent)
            .setToolTipText("Set the maximum number of snapshots that are loaded in a single\n"
                          + "call to avoid long delays in retrieving the snapshots. Setting\n"
                          + "this value to 0 means that all snapshots are loaded.\n"
                          + "Data provider may respect this setting or not.");
        addField(numberOfSnapshots);
        BooleanFieldEditor newSnapshots = new BooleanFieldEditor(
            SaveRestoreService.PREF_OPEN_NEW_SNAPSHOTS_IN_COMPARE_VIEW, "Open new snapshots in compare view", parent);
        newSnapshots.getDescriptionControl(parent)
            .setToolTipText("When new snapshots are created in the editor,\n"
                          + "they can be opened in a new editor or added as\n"
                          + "compared snapshots to the current editor");
        addField(newSnapshots);

    }

}
