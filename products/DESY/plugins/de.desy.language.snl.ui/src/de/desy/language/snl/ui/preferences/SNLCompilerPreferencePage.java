package de.desy.language.snl.ui.preferences;


import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.desy.language.snl.ui.SNLUiActivator;

/**
 * A preference page to set the colors and the font-styles for highlighting.
 */
public class SNLCompilerPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public SNLCompilerPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		this.setMessage("Specifies compiler location and options.");
		this.setPreferenceStore(SNLUiActivator.getDefault()
				.getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		Group groupOfLocationElement = new Group(getFieldEditorParent(), SWT.NONE);
		groupOfLocationElement.setText("Location of EPICS SNL compiler");
		groupOfLocationElement.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.addField(new DirectoryFieldEditor(SNLUiActivator.PLUGIN_ID + PreferenceConstants.SNC_LOCATION_POST_FIX, "", groupOfLocationElement));
		
		/*-
		 * <pre>
		 * SNC Version 2.0.11: Fri Jan 18 15:18:15 2008
		 * usage: snc <options> <infile>
		 * options:
		 *   -o <outfile> - override name of output file [Note: used internally by SNL-DT and will not apear in the preference page]
		 *   +a           - do asynchronous pvGet
		 *   -c           - don't wait for all connects
		 *   +d           - turn on debug run-time option
		 *   -e           - don't use new event flag mode
		 *   -l           - suppress line numbering
		 *   +m           - generate main program
		 *   -i           - don't register commands/programs
		 *   +r           - make reentrant at run-time
		 *   -w           - suppress compiler warnings
		 * example:
		 *  snc +a -c vacuum.st
		 * </pre>
		 */
		
		Group groupOfCompilerOptions = new Group(getFieldEditorParent(), SWT.NONE);
		groupOfCompilerOptions.setText("EPICS SNL compiler options");
		groupOfCompilerOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label compilerOptionsDescritption = new Label(groupOfCompilerOptions, SWT.WRAP);
		GridData gridData = new GridData();
		gridData.widthHint=500;
		compilerOptionsDescritption.setLayoutData(gridData);
		compilerOptionsDescritption.setText("The options selected below will be added to the command line. Not selected options will not be present at the command line; default may depends on the compiler version.");
		this.addField(new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID + PreferenceConstants.SNC_OPTIONS_ASYNCHRONOUS_PVGET_POST_FIX, "+a - do asynchronous pvGet", groupOfCompilerOptions));
		this.addField(new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID + PreferenceConstants.SNC_OPTIONS_DONT_WAIT_FOR_CONNECTIONS_POST_FIX, "-c - don't wait for all connects", groupOfCompilerOptions));
		this.addField(new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID + PreferenceConstants.SNC_OPTIONS_TURN_ON_DEBUG_RUNTIME_OPTION_POST_FIX, "+d - turn on debug run-time option", groupOfCompilerOptions));
		this.addField(new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID + PreferenceConstants.SNC_OPTIONS_DONT_USE_NEW_EVENT_FLAG_MODE_POST_FIX, "-e - don't use new event flag mode", groupOfCompilerOptions));
		this.addField(new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID + PreferenceConstants.SNC_OPTIONS_SUPRESS_LINE_NUMBERING_POST_FIX, "-l - suppress line numbering", groupOfCompilerOptions));
		this.addField(new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID + PreferenceConstants.SNC_OPTIONS_GENERATE_MAIN_PROGRAM_POST_FIX, "+m - generate main program", groupOfCompilerOptions));
		this.addField(new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID + PreferenceConstants.SNC_OPTIONS_DONT_REGISTER_COMMANDS_OR_PROGRAM_POST_FIX, "-i - don't register commands/programs", groupOfCompilerOptions));
		this.addField(new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID + PreferenceConstants.SNC_OPTIONS_MAKE_REENTRANT_AT_RUN_TIME_POST_FIX, "+r - make reentrant at run-time", groupOfCompilerOptions));
		this.addField(new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID + PreferenceConstants.SNC_OPTIONS_SUPRESS_COMPILER_WARNINGS_POST_FIX, "-w - suppress compiler warnings", groupOfCompilerOptions));
	}

	@Override
	protected void adjustGridLayout() {
		final int numColumns = 1;
		((GridLayout) this.getFieldEditorParent().getLayout()).numColumns = numColumns;
	}
	
	public void init(final IWorkbench workbench) {
		// do nothing
	}

}
