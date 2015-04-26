package org.csstudio.opibuilder.adl2boy.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.csstudio.opibuilder.adl2boy.ADL2BOYPlugin;

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

public class adl2boyPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor colorPrefix;
	private StringFieldEditor fontPrefix;

	public adl2boyPreferencePage() {
		super(GRID);
		setPreferenceStore(ADL2BOYPlugin.getDefault().getPreferenceStore());
		setDescription("Set Prefix to be used to create color and font maps");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		colorPrefix = new StringFieldEditor(PreferenceConstants.P_COLOR_PREFIX,
				"Color Map Prefix:", getFieldEditorParent());
		addField(colorPrefix);
		fontPrefix = new StringFieldEditor(PreferenceConstants.P_FONT_PREFIX,
				"Font Map Prefix:", getFieldEditorParent());
		addField(fontPrefix);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void checkState() {
		super.checkState();
		if (!isValid()) {
			return;
		}
		String colorPrefixText = colorPrefix.getStringValue();
		String fontPrefixText = fontPrefix.getStringValue();
		if (!(colorPrefixText.trim().matches("[a-zA-Z0-9_]+"))
				|| !(fontPrefixText.trim().matches("[a-zA-Z0-9_]+"))) {
			setErrorMessage("Valid prefixes contain alphanumerics and _ with no white space");
			setValid(false);
		} else {
			setErrorMessage(null);
			setValid(true);

		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if (event.getProperty().equals(FieldEditor.VALUE)) {
			if (event.getSource() == colorPrefix
					|| event.getSource() == fontPrefix) {
				checkState();
			}
		}
	}
}