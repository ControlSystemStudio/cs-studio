package de.desy.language.snl.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SNLPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	public SNLPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected void createFieldEditors() {
		Group container = new Group(getFieldEditorParent(), SWT.NONE);
		Label snlPageDescritption = new Label(container, SWT.WRAP);
		GridData gridData = new GridData();
		gridData.widthHint=500;
		snlPageDescritption.setLayoutData(gridData);
		snlPageDescritption
				.setText("On pages of this category you will find options of the state"
						+ " notation development support. You should check the compiler"
						+ " option and EPICS page to be configured with the correct"
						+ " path-location information.");
	}

	public void init(final IWorkbench workbench) {
		this.noDefaultAndApplyButton();
	}

}
