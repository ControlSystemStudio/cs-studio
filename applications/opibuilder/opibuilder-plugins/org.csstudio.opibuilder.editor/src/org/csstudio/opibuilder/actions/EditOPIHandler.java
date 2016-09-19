package org.csstudio.opibuilder.actions;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.OPIView;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;


public class EditOPIHandler extends AbstractHandler implements IHandler {

    private static final String OPI_EDITOR_ID = "org.csstudio.opibuilder.OPIEditor"; //$NON-NLS-1$

    /** EditOPI action
     *  - if selected part is an OPIShell open this in the main CSS window in edit mode
     *  - if the selected part is in the CSS window  as an OPIView open as an editor
     *  - if the selected part is in the CSS in run mode, open in edit mode
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        IOPIRuntime opiRuntime = SingleSourceHelper.getOPIShellForShell(HandlerUtil.getActiveShell(event));
        if (opiRuntime == null) {
            // if the selected object isn't an OPIShell so grab the
            // OPIView or OPIRunner currently selected
            IWorkbenchPart part = HandlerUtil.getActivePart(event);
            if (part instanceof IOPIRuntime)
            {
                opiRuntime = (IOPIRuntime)part;
            }
        }

        if (opiRuntime != null) {
            IPath path = opiRuntime.getDisplayModel().getOpiFilePath();
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

    /** Extract the workbench part from an evaluation context (this
     *  is equivalent to HandleUtils.getActivePart() for the
     *  IEvaluationContext argument)
     */
    private IWorkbenchPart getActivePart(IEvaluationContext context) {
        Object var = context.getVariable(ISources.ACTIVE_PART_NAME);
        if (var instanceof IWorkbenchPart) {
            return (IWorkbenchPart) var;
        }
        return null;
    }

    /** Extract the active shell from an evaluation context (this
     *  is equivalent to HandleUtils.getActiveShell() for the
     *  IEvaluationContext argument)
     */
    private Shell getActiveShell(IEvaluationContext context) {
        Object var = context.getVariable(ISources.ACTIVE_SHELL_NAME);
        if (var instanceof Shell) {
            return (Shell) var;
        }
        return null;
    }

    /** The handler is enabled if:
     *  - selected object is an OPIShell (i.e. an EDM window)
     *  - selected object is an OPIView (i.e. a CSS view)
     *  - selected object is an OPIRunner (e.g. a CSS editor panel in runmode)
     *  The handler is disabled if:
     *  - the open resource is a URL (i.e. the content is served over http)
     *  - CSS is in no-edit mode
     */
    @Override
    public void setEnabled(Object evaluationContext) {
        boolean enabled = false;
        if (!PreferencesHelper.isNoEdit()) {
            if (evaluationContext instanceof IEvaluationContext) {
                IWorkbenchPart part = getActivePart((IEvaluationContext) evaluationContext);
                IOPIRuntime opiShell = SingleSourceHelper.getOPIShellForShell(
                        getActiveShell((IEvaluationContext) evaluationContext));
                IPath path = null;
                if (opiShell != null) {
                    path = opiShell.getDisplayModel().getOpiFilePath();
                } else if (part instanceof OPIView) {
                    DisplayModel displayModel = ((OPIView) part).getDisplayModel();
                    if (displayModel != null) {
                        path = displayModel.getOpiFilePath();
                    }
                }
                // We only support filesystem paths.
                enabled = (path instanceof Path);
            }
        }

        setBaseEnabled(enabled);
    }

}

