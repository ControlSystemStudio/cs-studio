/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.csstudio.sds.internal.rules.RuleService;
import org.csstudio.ui.util.wizards.WizardNewFileCreationPage;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Wizard page for the creation of new SDS script rules.
 *
 * @author Alexander Will
 * @version $Revision: 1.5 $
 *
 */
public final class NewScriptRuleWizardPage extends WizardNewFileCreationPage {

    /**
     * The initial script file contents.
     */
    private static final String INITIAL_CONTENTS = "var compatibleProperties = \"sds.double\";\n"
            + "var description = \"Rule name\";\n"
            + "var parameters = new Array(\"Description of argument 1\", \"Description of argument 2\", \"Description of argument 3\");\n"
            + "var parameterTypes = new Array(\"java.lang.Double\", \"java.lang.Double\", \"java.lang.Double\");\n\n"
            + "function execute(args) {\n"
            + "\tvar argument1 = args[0];\n"
            + "\tvar argument2 = args[1];\n"
            + "\tvar argument3 = args[2];\n\n"
            + "\treturn argument1;\n"
            +"}";

    /**
     * Creates a new SDS script rule creation wizard page.
     *
     * @param pageName
     *            the name of the page
     * @param selection
     *            the current resource selection
     */
    public NewScriptRuleWizardPage(final String pageName,
            final IStructuredSelection selection) {
        super(pageName, selection, true);
        setTitle("Create a new scripted rule");
        setDescription("Create a new scripted rule.");
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
        return "Rule name:";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileExtension() {
        return RuleService.SCRIPT_FILE_EXTENSION;
    }
}
