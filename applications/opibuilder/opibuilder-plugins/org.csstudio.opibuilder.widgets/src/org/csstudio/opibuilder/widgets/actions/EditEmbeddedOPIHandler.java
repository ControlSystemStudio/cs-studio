package org.csstudio.opibuilder.widgets.actions;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgets.editparts.LinkingContainerEditpart;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;


public class EditEmbeddedOPIHandler extends AbstractHandler implements IHandler {

    private static final String OPI_EDITOR_ID = "org.csstudio.opibuilder.OPIEditor"; //$NON-NLS-1$

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
                path = linkingContainer.getWidgetModel().getOPIFilePath();
            }
        }

        if (path != null) {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window != null) {
                IWorkbenchPage page = window.getActivePage();
                if (page != null) {
                    try {
                        IEditorInput editorInput =  ResourceUtil.editorInputFromPath(path);
                        // Need to match on both Editor ID and file to prevent
                        // eclipse choosing an OPIRunner instance
                        page.openEditor(editorInput, OPI_EDITOR_ID, true,
                                IWorkbenchPage.MATCH_ID | IWorkbenchPage.MATCH_INPUT);
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
