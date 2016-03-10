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
package org.csstudio.sds.importer.ui.internal.wizards;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.importer.AbstractDisplayImporter;
import org.csstudio.sds.ui.wizards.NewDisplayWizardPage;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract super class for display import wizards.
 *
 * @author Alexander Will
 * @version $Revision: 1.1 $
 *
 */
public abstract class AbstractDisplayImportWizard extends Wizard implements
        IImportWizard {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDisplayImportWizard.class);

    /**
     * This wizard page is used to enter the file name and the target
     * project/folder for the imported display.
     */
    private NewDisplayWizardPage _sdsFilePage;

    /**
     * This wizard page is used to enter the file name and path of the file that
     * is to be imported.
     */
    private ImportSourceSelectionWizardPage _importSourcePage;

    /**
     * The current selection.
     */
    private IStructuredSelection _selection;

    /**
     * The workbench reference.
     */
    private IWorkbench _workbench;

    /**
     * The used display importer.
     */
    private AbstractDisplayImporter _importer;

    /**
     * Standard constructor.
     */
    public AbstractDisplayImportWizard() {
        _importer = getImporter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addPages() {
        _sdsFilePage = new NewDisplayWizardPage("sdsFilePage", //$NON-NLS-1$
                _selection);
        _importSourcePage = new ImportSourceSelectionWizardPage(
                "importSourcePage"); //$NON-NLS-1$

        addPage(_importSourcePage);
        addPage(_sdsFilePage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean performFinish() {
        boolean result = false;

        try {
            result = _importer.importDisplay(_importSourcePage
                    .getSelectedFilePath(),
                    _sdsFilePage.getContainerFullPath(), _sdsFilePage
                            .getFileName());
        } catch (Exception e) {
            MessageDialog.openWarning(_workbench.getDisplay().getActiveShell(),
                    "Error", "Error during import: " + e.getMessage());
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void init(final IWorkbench workbench,
            final IStructuredSelection selection) {
        _workbench = workbench;
        _selection = selection;

        setDefaultSelection();
    }

    /**
     * Return the used importer.
     *
     * @return The used importer.
     */
    protected abstract AbstractDisplayImporter getImporter();

    /**
     * Set the active workspace project selection to the default SDS project.
     */
    private void setDefaultSelection() {
        if ((_selection == null) || (_selection.isEmpty())) {
            IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(
                    SdsPlugin.DEFAULT_PROJECT_NAME);
            if (p.exists()) {
                _selection = new StructuredSelection(p);
            }
        }
    }
}
