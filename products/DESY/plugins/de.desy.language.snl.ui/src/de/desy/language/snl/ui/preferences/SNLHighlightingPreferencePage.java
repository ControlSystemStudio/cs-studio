package de.desy.language.snl.ui.preferences;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.desy.language.snl.configurationservice.PreferenceConstants;
import de.desy.language.snl.ui.SNLUiActivator;
import de.desy.language.snl.ui.rules.SNLCodeElementTextAttributeConstants;

/**
 * A preference page to set the colors and the font-styles for highlighting.
 */
public class SNLHighlightingPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public SNLHighlightingPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		this.setMessage("Set colors and font-styles for highlighting");
		this.setPreferenceStore(SNLUiActivator.getDefault()
				.getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		Label label = new Label(this.getFieldEditorParent(), SWT.NONE);
		label = new Label(this.getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		label.setAlignment(SWT.CENTER);
		label.setText("Color");
		label = new Label(this.getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		label.setAlignment(SWT.CENTER);
		label.setText("Bold");
		label = new Label(this.getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		label.setAlignment(SWT.CENTER);
		label.setText("Italic");
		label = new Label(this.getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		label.setAlignment(SWT.CENTER);
		label.setText("Underline");
		label = new Label(this.getFieldEditorParent(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		label.setAlignment(SWT.CENTER);
		label.setText("Strikethrough");
		final SNLCodeElementTextAttributeConstants[] sortedValues = this
				.sortValues(SNLCodeElementTextAttributeConstants.values());
		for (final SNLCodeElementTextAttributeConstants constant : sortedValues) {
			final FieldEditor editor = new ColorFieldEditor(constant
					.asStringId()
					+ PreferenceConstants.COLOR_POST_FIX, constant
					.getShortDescription(), this.getFieldEditorParent());
			this.addField(editor);
			this.addField(new BooleanFieldEditor(constant.asStringId()
					+ PreferenceConstants.BOLD_POST_FIX, "", this
					.getFieldEditorParent()));
			this.addField(new BooleanFieldEditor(constant.asStringId()
					+ PreferenceConstants.ITALIC_POST_FIX, "", this
					.getFieldEditorParent()));
			this.addField(new BooleanFieldEditor(constant.asStringId()
					+ PreferenceConstants.UNDERLINE_POST_FIX, "", this
					.getFieldEditorParent()));
			this.addField(new BooleanFieldEditor(constant.asStringId()
					+ PreferenceConstants.STRIKETHROUGH_POST_FIX, "", this
					.getFieldEditorParent()));
		}
	}

	private SNLCodeElementTextAttributeConstants[] sortValues(
			final SNLCodeElementTextAttributeConstants[] values) {
		final SNLCodeElementTextAttributeConstants[] sortedValues = new SNLCodeElementTextAttributeConstants[values.length];
		System.arraycopy(values, 0, sortedValues, 0, values.length);

		Arrays.sort(sortedValues,
				new Comparator<SNLCodeElementTextAttributeConstants>() {
					public int compare(
							final SNLCodeElementTextAttributeConstants o1,
							final SNLCodeElementTextAttributeConstants o2) {
						return o1.getShortDescription().compareTo(
								o2.getShortDescription());
					}
				});

		return sortedValues;
	}

	@Override
	protected void adjustGridLayout() {
		final int numColumns = 6;
		((GridLayout) this.getFieldEditorParent().getLayout()).numColumns = numColumns;
	}

	public void init(final IWorkbench workbench) {
		// do nothing
	}

}
