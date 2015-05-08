package org.csstudio.sds.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.csstudio.sds.internal.rules.RuleService;
import org.csstudio.ui.util.wizards.WizardNewFileCreationPage;
import org.eclipse.jface.viewers.IStructuredSelection;

public class NewScriptWizardPage extends WizardNewFileCreationPage {

    /**
     * The initial script file contents.
     */
    private static final String INITIAL_CONTENTS = "importPackage(Packages.org.csstudio.sds.ui.scripting);\n\n" +
            "function execute() {\n" +
            "\t/*Add your code here*/\n" +
            "}";

    public NewScriptWizardPage(String pageName, IStructuredSelection selection) {
        super(pageName, selection, true);
        setTitle("Create a new script");
        setDescription("Create a new script.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected InputStream getInitialContents() {
        return new ByteArrayInputStream(INITIAL_CONTENTS.getBytes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getNewFileLabel() {
        return "Script name:";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileExtension() {
        return RuleService.SCRIPT_FILE_EXTENSION;
    }

}
