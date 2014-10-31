package de.desy.language.snl.ui.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.desy.language.snl.configurationservice.ConfigurationService;
import de.desy.language.snl.configurationservice.PreferenceConstants;
import de.desy.language.snl.ui.SNLUiActivator;

public class SNLPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public SNLPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		this.setPreferenceStore(SNLUiActivator.getDefault()
				.getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		String preferenceID = SNLUiActivator.PLUGIN_ID
				+ PreferenceConstants.TARGET_PLATFORM;
		String[][] labelAndValues = createLabelAndValues();
		FieldEditor editor = new RadioGroupFieldEditor(preferenceID,
				"Choose target platform", 1, labelAndValues,
				getFieldEditorParent(), true);
		addField(editor);

		editor = new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID
				+ PreferenceConstants.SAVE_AND_COMPILE_POST_FIX,
				"Start compilation after saving", getFieldEditorParent());
		addField(editor);
		
		editor = new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID
				+ PreferenceConstants.KEEP_GENERATED_FILES_POST_FIX,
				"Keep temporary compilation results", getFieldEditorParent());
		addField(editor);
	}

	private String[][] createLabelAndValues() {
		ConfigurationService service = ConfigurationService.getInstance();
		Set<String> allIDs = service.getAllIDs();
		List<String[]> labelsAndValues = new ArrayList<String[]>();
		labelsAndValues.add(new String[] { "None", "none" });

		for (String id : allIDs) {
			String description = service.getFullDescription(id);
			labelsAndValues.add(new String[] { description, id });
		}

		return labelsAndValues.toArray(new String[0][0]);
	}

	public void init(final IWorkbench workbench) {
		this.noDefaultAndApplyButton();
	}

}
