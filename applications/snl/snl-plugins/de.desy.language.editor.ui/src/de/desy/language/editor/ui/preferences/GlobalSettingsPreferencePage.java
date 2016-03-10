package de.desy.language.editor.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.desy.language.editor.ui.EditorUIActivator;

public class GlobalSettingsPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    public GlobalSettingsPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        this.setMessage("Specifies Language editor options.");
        this.setPreferenceStore(EditorUIActivator.getDefault()
                .getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        FieldEditor editor;

        editor = new BooleanFieldEditor(PreferenceConstants.CURSOR_LINE_ENABLE.getPreferenceStoreId(), "Enables current line highlighting", getFieldEditorParent());
        this.addField(editor);
        editor = new ColorFieldEditor(PreferenceConstants.CURSOR_LINE_COLOR.getPreferenceStoreId(), "Color for line highlighting", getFieldEditorParent());
        this.addField(editor);

        editor = new BooleanFieldEditor(PreferenceConstants.MATCHING_CHARACTER_ENABLE.getPreferenceStoreId(), "Enables matching character highlighting", getFieldEditorParent());
        this.addField(editor);
        editor = new ColorFieldEditor(PreferenceConstants.MATCHING_CHARACTER_COLOR.getPreferenceStoreId(), "Color matching character highlighting", getFieldEditorParent());
        this.addField(editor);

        editor = new BooleanFieldEditor(PreferenceConstants.MARGIN_PAINTER_ENABLE.getPreferenceStoreId(), "Enables margin highlighting", getFieldEditorParent());
        this.addField(editor);
        editor = new ColorFieldEditor(PreferenceConstants.MARGIN_PAINTER_COLOR.getPreferenceStoreId(), "Color for margin line", getFieldEditorParent());
        this.addField(editor);
        editor = new IntegerFieldEditor(PreferenceConstants.MARGIN_COLUMNS.getPreferenceStoreId(), "Columns for margins", getFieldEditorParent());
        this.addField(editor);
    }

    @Override
    public void init(IWorkbench workbench) {

    }

}
