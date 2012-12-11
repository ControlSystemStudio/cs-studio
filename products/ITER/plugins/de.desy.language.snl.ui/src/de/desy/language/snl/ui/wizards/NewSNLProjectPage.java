package de.desy.language.snl.ui.wizards;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class NewSNLProjectPage extends WizardNewProjectCreationPage {
	IWorkbench workbench;

	String nature;

	public NewSNLProjectPage(final String pageName) {
		super("Project Creation Page");
		this.setTitle("Create a new SNL project");
		this.setDescription("Enter a name for the new SNL project");
	}
}
