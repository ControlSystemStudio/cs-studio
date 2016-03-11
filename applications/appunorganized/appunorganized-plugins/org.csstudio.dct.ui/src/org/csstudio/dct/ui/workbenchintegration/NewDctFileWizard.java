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
package org.csstudio.dct.ui.workbenchintegration;

import java.io.InputStream;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.internal.ProjectFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * A wizard for creating new DCT files.
 *
 * @version $Revision$
 *
 */
public final class NewDctFileWizard extends Wizard implements INewWizard {
    private NewDisplayWizardPage _sdsFilePage;

    /**
     * The current selection.
     */
    private IStructuredSelection _selection;

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {
        _sdsFilePage = new NewDisplayWizardPage("sdsFilePage", //$NON-NLS-1$
                _selection);
        addPage(_sdsFilePage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performFinish() {
        boolean result = true;
        IFile file = _sdsFilePage.createNewFile();

        if (file == null) {
            result = false;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final IWorkbench workbench, final IStructuredSelection selection) {
        _selection = selection;

        setDefaultSelection();
    }

    /**
     * Set the active workspace project selection to the default SDS project.
     */
    private void setDefaultSelection() {
        if ((_selection == null) || (_selection.isEmpty())) {
            IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject("DCT");
            if (p.exists()) {
                _selection = new StructuredSelection(p);
            }
        }
    }

    final class NewDisplayWizardPage extends WizardNewFileCreationPage {
        /**
         * Creates a new SDS file creation wizard page.
         *
         * @param pageName
         *            the name of the page
         * @param selection
         *            the current resource selection
         */
        public NewDisplayWizardPage(final String pageName,
                final IStructuredSelection selection) {
            super(pageName, selection);
            setTitle("Create a new display");
            setDescription("Create a new display in the selected project or folder.");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected InputStream getInitialContents() {
            Project project = ProjectFactory.createNewDCTProjectFile(getFileName());
            try {
                return DctActivator.getDefault().getPersistenceService().getAsStream(project);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String getNewFileLabel() {
            return "DCT File Name:";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getFileExtension() {
            return ProjectFactory.getDctFileExtension();
        }
    }

}
