package org.csstudio.dct.ui.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.ExtensionPointUtil;
import org.csstudio.dct.IRecordFunction;
import org.csstudio.dct.ServiceExtension;
import org.csstudio.dct.model.IProject;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * Manages the installation/deinstallation of global actions for DCT editors.
 * Responsible for the redirection of global actions to the active editor.
 */
public final class DctEditorContributor extends MultiPageEditorActionBarContributor {
    private DctEditor activeEditor;
    private IEditorPart activeEditorPart;

    private List<RecordFunctionAction> recordFunctionActions;

    /**
     * Constructor.
     */
    public DctEditorContributor() {
        super();
        createActions();
    }

    private void createActions() {
        recordFunctionActions = new ArrayList<RecordFunctionAction>();
        Map<String, ServiceExtension<IRecordFunction>> extensions = ExtensionPointUtil
                .lookupNamingServiceExtensions(DctActivator.EXTPOINT_RECORD_FUNCTIONS);

        for (final ServiceExtension<IRecordFunction> extension : extensions.values()) {
            recordFunctionActions.add(new RecordFunctionAction(extension));
        }
    }

    /**
     * Returns the action registered with the given text editor.
     *
     * @param editor
     *            the text edit editor
     * @param actionID
     *            the action id
     *
     * @return IAction or null if editor is null.
     */
    protected IAction getAction(ITextEditor editor, String actionID) {
        return (editor == null ? null : editor.getAction(actionID));
    }

    @Override
    public void setActiveEditor(IEditorPart part) {
        if (part instanceof DctEditor) {
            activeEditor = (DctEditor) part;

            for (RecordFunctionAction a : recordFunctionActions) {
                a.setProject(activeEditor.getProject());
            }
        } else {
            activeEditor = null;
        }

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void setActivePage(IEditorPart part) {
        if (activeEditorPart == part) {
            return;
        }

        activeEditorPart = part;

        IActionBars actionBars = getActionBars();
        if (actionBars != null) {

            ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;

            actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), getAction(editor, ITextEditorActionConstants.DELETE));
            actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), getAction(editor, ITextEditorActionConstants.UNDO));
            actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), getAction(editor, ITextEditorActionConstants.REDO));
            actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), getAction(editor, ITextEditorActionConstants.CUT));
            actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), getAction(editor, ITextEditorActionConstants.COPY));
            actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), getAction(editor, ITextEditorActionConstants.PASTE));
            actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), getAction(editor, ITextEditorActionConstants.SELECT_ALL));
            actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), getAction(editor, ITextEditorActionConstants.FIND));
            actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(), getAction(editor, IDEActionFactory.BOOKMARK.getId()));
            actionBars.updateActionBars();
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void contributeToMenu(IMenuManager manager) {
        IMenuManager menu = new MenuManager("DCT");
        manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);

        // actions for record functions
        for (Action a : recordFunctionActions) {
            menu.add(a);
        }
    }

    private static final class RecordFunctionAction extends Action {
        private IProject project;
        private ServiceExtension<IRecordFunction> extension;

        public RecordFunctionAction(ServiceExtension<IRecordFunction> extension) {
            this.extension = extension;
            setText(extension.getName());
            setDescription(extension.getName());
            if (extension.getIconPath() != null) {
                setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(extension.getPluginId(),
                        extension.getIconPath()));
            }
        }

        public void setProject(IProject project) {
            this.project = project;
        }

        @Override
        public void run() {
            if (project != null) {
                final IRecordFunction function = extension.getService();

                ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());

                try {
                    dialog.run(false, true, new IRunnableWithProgress() {
                        @Override
                        public void run(IProgressMonitor monitor) {
                            function.run(project, monitor);
                        }
                    });
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void contributeToToolBar(IToolBarManager manager) {
        manager.add(new Separator());
    }

}
