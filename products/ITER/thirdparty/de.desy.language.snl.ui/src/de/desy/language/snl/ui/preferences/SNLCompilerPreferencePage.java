package de.desy.language.snl.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.desy.language.snl.configurationservice.CompilerOptionPreferenceConstants;
import de.desy.language.snl.configurationservice.PreferenceConstants;
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
		Group preCompilerGroup = new Group(getFieldEditorParent(),
				SWT.NONE);
		preCompilerGroup.setText("Location of Precompiler");
		preCompilerGroup.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		this.addField(new FileFieldEditor(SNLUiActivator.PLUGIN_ID
				+ PreferenceConstants.PRE_COMPILER_LOCATION_POST_FIX, "", true, 
				preCompilerGroup));
		
		Group groupOfLocationElement = new Group(getFieldEditorParent(),
				SWT.NONE);
		groupOfLocationElement.setText("Location of EPICS SNL compiler");
		groupOfLocationElement.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		this.addField(new FileFieldEditor(SNLUiActivator.PLUGIN_ID
				+ PreferenceConstants.SNC_LOCATION_POST_FIX, "", true, 
				groupOfLocationElement));
		
		Group cCompilerGroup = new Group(getFieldEditorParent(),
				SWT.NONE);
		cCompilerGroup.setText("Location of C Compiler");
		cCompilerGroup.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		this.addField(new FileFieldEditor(SNLUiActivator.PLUGIN_ID
				+ PreferenceConstants.C_COMPILER_LOCATION_POST_FIX, "", true, 
				cCompilerGroup));
		
		Group applicationCompilerGroup = new Group(getFieldEditorParent(),
				SWT.NONE);
		applicationCompilerGroup.setText("Location of Application Compiler");
		applicationCompilerGroup.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		this.addField(new FileFieldEditor(SNLUiActivator.PLUGIN_ID
				+ PreferenceConstants.APPLICATION_COMPILER_POST_FIX, "", true, 
				applicationCompilerGroup));
		
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

		Group groupOfCompilerOptions = new Group(getFieldEditorParent(),
				SWT.NONE);
		groupOfCompilerOptions.setText("EPICS SNL compiler options");
		groupOfCompilerOptions.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		Label compilerOptionsDescritption = new Label(groupOfCompilerOptions,
				SWT.WRAP);
		GridData gridData = new GridData();
		gridData.widthHint = 500;
		compilerOptionsDescritption.setLayoutData(gridData);
		compilerOptionsDescritption
				.setText("The options selected below will be added to the command line. Not selected options will not be present at the command line; default may depends on the compiler version.");
		createCompilerOptionEditors(groupOfCompilerOptions);
	}

	private void createCompilerOptionEditors(Composite parent) {
		for (CompilerOptionPreferenceConstants copc : CompilerOptionPreferenceConstants
				.values()) {
			this.addField(new BooleanFieldEditor(SNLUiActivator.PLUGIN_ID
					+ copc.getPreferenceStoreId(), copc.getOption() + " - "
					+ copc.getDescription(), parent));
		}
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
