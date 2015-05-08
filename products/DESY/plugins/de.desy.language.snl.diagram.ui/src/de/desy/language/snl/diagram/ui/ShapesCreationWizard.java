package de.desy.language.snl.diagram.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

import de.desy.language.snl.diagram.model.SNLDiagram;

/**
 * Create new new .shape-file. Those files can be used with the ShapesEditor
 * (see plugin.xml).
 */
public class ShapesCreationWizard extends Wizard implements INewWizard {

    private static int fileCount = 1;
    private CreationPage page1;

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    @Override
    public void addPages() {
        // add pages to this wizard
        addPage(page1);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(final IWorkbench workbench,
            final IStructuredSelection selection) {
        // create pages for this wizard
        page1 = new CreationPage(workbench, selection);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        return page1.finish();
    }

    /**
     * This WizardPage can create an empty .shapes file for the ShapesEditor.
     */
    private class CreationPage extends WizardNewFileCreationPage {
        private static final String DEFAULT_EXTENSION = ".std";
        private final IWorkbench workbench;

        /**
         * Create a new wizard page instance.
         *
         * @param workbench
         *            the current workbench
         * @param selection
         *            the current object selection
         * @see ShapesCreationWizard#init(IWorkbench, IStructuredSelection)
         */
        CreationPage(final IWorkbench workbench,
                final IStructuredSelection selection) {
            super("shapeCreationPage1", selection);
            this.workbench = workbench;
            setTitle("Create a new " + DEFAULT_EXTENSION + " file");
            setDescription("Create a new " + DEFAULT_EXTENSION + " file");
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.eclipse.ui.dialogs.WizardNewFileCreationPage#createControl(org
         * .eclipse.swt.widgets.Composite)
         */
        @Override
        public void createControl(final Composite parent) {
            super.createControl(parent);
            setFileName("shapesExample" + fileCount + DEFAULT_EXTENSION);
            setPageComplete(validatePage());
        }

        /** Return a new ShapesDiagram instance. */
        private Object createDefaultContent() {
            return new SNLDiagram();
        }

        /**
         * This method will be invoked, when the "Finish" button is pressed.
         *
         * @see ShapesCreationWizard#performFinish()
         */
        boolean finish() {
            // create a new file, result != null if successful
            final IFile newFile = createNewFile();
            fileCount++;

            // open newly created file in the editor
            final IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
                    .getActivePage();
            if (newFile != null && page != null) {
                try {
                    IDE.openEditor(page, newFile, true);
                } catch (final PartInitException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
         */
        @Override
        protected InputStream getInitialContents() {
            ByteArrayInputStream bais = null;
            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(createDefaultContent()); // argument must be
                                                            // Serializable
                oos.flush();
                oos.close();
                bais = new ByteArrayInputStream(baos.toByteArray());
            } catch (final IOException ioe) {
                ioe.printStackTrace();
            }
            return bais;
        }

        /**
         * Return true, if the file name entered in this page is valid.
         */
        private boolean validateFilename() {
            if (getFileName() != null
                    && getFileName().endsWith(DEFAULT_EXTENSION)) {
                return true;
            }
            setErrorMessage("The 'file' name must end with "
                    + DEFAULT_EXTENSION);
            return false;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validatePage()
         */
        @Override
        protected boolean validatePage() {
            return super.validatePage() && validateFilename();
        }
    }
}
