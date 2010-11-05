package org.csstudio.opibuilder.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.platform.ui.wizards.WizardNewFileCreationPage;
import org.eclipse.jface.viewers.IStructuredSelection;

/**Wizard page for the creation of new OPI files.
 * @author Xihui Chen
 *
 */
public class NewOPIFileWizardPage extends WizardNewFileCreationPage {

	public NewOPIFileWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setTitle("Create a new OPI File");
		setDescription("Create a new OPI file in the selected project or folder.");
	}
	
	@Override
	protected InputStream getInitialContents() {
		String s = XMLUtil.widgetToXMLString(new DisplayModel(), true);
		InputStream result = new ByteArrayInputStream(s.getBytes());
		return result;
	}
	
	
	@Override
	protected String getNewFileLabel() {
		return "OPI File Name:";
	}
	
	@Override
	public String getFileExtension() {
		return OPIBuilderPlugin.OPI_FILE_EXTENSION;
	}

}
