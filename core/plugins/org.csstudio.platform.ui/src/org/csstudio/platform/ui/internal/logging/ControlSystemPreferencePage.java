package org.csstudio.platform.ui.internal.logging;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ControlSystemPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public ControlSystemPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setMessage("Set the default control system");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors() {
		String[][] labelsAndValues = new String[ControlSystemEnum.values().length][2];
		for (int i = 0; i < ControlSystemEnum.values().length; i++) {
			labelsAndValues[i] = new String[] {
					ControlSystemEnum.values()[i].name(),
					ControlSystemEnum.values()[i].name() };
		}
		RadioGroupFieldEditor radioFields = new RadioGroupFieldEditor(
				ProcessVariableAdressFactory.PROP_CONTROL_SYSTEM, "Control Systems", 1,
				labelsAndValues, getFieldEditorParent());

		addField(radioFields);

		BooleanFieldEditor bfe = new BooleanFieldEditor(
				ProcessVariableAdressFactory.PROP_ASK_FOR_CONTROL_SYSTEM,
				"Ask for the right control system, each time a user drops a text String into CSS.",
				getFieldEditorParent());
		
		addField(bfe);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return CSSPlatformUiPlugin.getCorePreferenceStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(final IWorkbench workbench) {
	}

}
