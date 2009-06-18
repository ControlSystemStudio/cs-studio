package org.csstudio.dct.ui;

import static org.csstudio.dct.PreferenceSettings.DATALINK_FUNCTION_PARAMETER_3_PROPOSAL;
import static org.csstudio.dct.PreferenceSettings.DATALINK_FUNCTION_PARAMETER_4_PROPOSAL;
import static org.csstudio.dct.PreferenceSettings.FIELD_DESCRIPTION_SHOW_DESCRIPTION;
import static org.csstudio.dct.PreferenceSettings.FIELD_DESCRIPTION_SHOW_INITIAL_VALUE;

import java.util.Arrays;

import org.csstudio.platform.util.StringUtil;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * Preference Page for the DCT.
 * 
 * @author Sven Wende
 * 
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected void createFieldEditors() {
		Label l = new Label(getFieldEditorParent(), SWT.None);
		l.setText("first column in field table:");
		addField(new BooleanFieldEditor(FIELD_DESCRIPTION_SHOW_DESCRIPTION.name(), FIELD_DESCRIPTION_SHOW_DESCRIPTION.getLabel(),
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(FIELD_DESCRIPTION_SHOW_INITIAL_VALUE.name(), FIELD_DESCRIPTION_SHOW_INITIAL_VALUE.getLabel(),
				getFieldEditorParent()));

		addField(new StringListEditor(DATALINK_FUNCTION_PARAMETER_3_PROPOSAL.name(), DATALINK_FUNCTION_PARAMETER_3_PROPOSAL.getLabel(), getFieldEditorParent()));
		addField(new StringListEditor(DATALINK_FUNCTION_PARAMETER_4_PROPOSAL.name(), DATALINK_FUNCTION_PARAMETER_4_PROPOSAL.getLabel(), getFieldEditorParent()));
	}

	/**
	 *{@inheritDoc}
	 */
	public void init(IWorkbench workbench) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getCorePreferenceStore();
	}
	
	private final class StringListEditor extends ListEditor {
		private StringListEditor(String name, String labelText, Composite parent) {
			super(name, labelText, parent);
		}

		@Override
		protected String createList(String[] items) {
			return StringUtil.toSeparatedString(Arrays.asList(items), ",");
		}

		@Override
		protected String getNewInputObject() {
			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "New value",
					"Please enter a new value", "", new IInputValidator() {
						public String isValid(String newText) {
							if (!StringUtil.hasLength(newText)) {
								return "Value cannot be empty";
							}
							return null;
						}
					});

			String result = null;

			if (dialog.open() == Window.OK) {
				result = dialog.getValue();
			}

			return result;
		}

		@Override
		protected String[] parseString(String stringList) {
			return stringList.split(",");
		}
	}

}
