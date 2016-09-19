package org.csstudio.opibuilder.widgets.actions;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgets.editparts.LinkingContainerEditpart;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.csstudio.ui.util.perspective.PerspectiveHelper;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;


public class EditEmbeddedOPIHandler extends AbstractHandler implements IHandler {

    private static final String OPI_EDITOR_ID = "org.csstudio.opibuilder.OPIEditor"; //$NON-NLS-1$
    private static final String OPI_EDITOR_PERSPECTIVE_ID = "org.csstudio.opibuilder.opieditor"; //$NON-NLS-1$

    /**
     * Determine the widget that was the object of the mouse click.
     * If it can be established to be a LinkingContainerEditpart, extract
     * the path of the embedded opi and request opening an OPIEditor with this file.
     * Warning: this shares code with EditOPIHandler in the org.csstudio.opibuilder.editor plugin.
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IPath path = null;
        LinkingContainerEditpart linkingContainer = null;

        ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            Object o = structuredSelection.getFirstElement();
            if (o instanceof LinkingContainerEditpart) {
                linkingContainer = (LinkingContainerEditpart) o;
                path = ((LinkingContainerModel) linkingContainer.getModel()).getOPIFilePath();
            }
        }

        if (path != null) {
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window != null) {
                IWorkbenchPage page = window.getActivePage();
                if (page != null) {
                    try {
                        IEditorInput editorInput = null;
                        // Files outside the workspace are handled differently
                        // by Eclipse.
                        if (!ResourceUtil.isExistingWorkspaceFile(path)
                                && ResourceUtil.isExistingLocalFile(path)) {
                            // IEditorInput editorInput = new
                            // FileStoreEditorInput(file);
                            IFileStore fileStore = EFS.getLocalFileSystem()
                                    .getStore(file.getFullPath());
                            editorInput = new FileStoreEditorInput(fileStore);
                        } else {
                            editorInput = new FileEditorInput(file);
                        }
                        // Need to match on both Editor ID and file to prevent
                        // eclipse choosing an OPIRunner instance
                        page.openEditor(editorInput, OPI_EDITOR_ID, true,
                                IWorkbenchPage.MATCH_ID | IWorkbenchPage.MATCH_INPUT);
                        // force switch to edit perspective
                        PerspectiveHelper.showPerspective(
                                OPI_EDITOR_PERSPECTIVE_ID, page);
                    } catch (PartInitException ex) {
                        System.err.println("Error starting OPI Editor"
                                + ex.toString());
                        ErrorHandlerUtil.handleError(
                                "Failed to open current OPI in editor", ex);
                    }
                }
            }
        }
        // required return value
        return null;
    }

}
