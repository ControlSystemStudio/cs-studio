package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editor.Activator;
import org.csstudio.opibuilder.util.SchemaService;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ReloadSchemaAction extends Action implements IWorkbenchWindowActionDelegate {

    public static final String ID = "org.csstudio.opibuilder.reloadSchemaAction";
    public static final String ACTION_DEFINITION_ID = "org.csstudio.opibuilder.reloadschema";

    public ReloadSchemaAction() {
        super("Reload Schema & Styles", CustomMediaFactory.getInstance()
            .getImageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/refresh_nav.png"));
        setId(ID);
        setActionDefinitionId(ACTION_DEFINITION_ID);
    }

    @Override
    public void run(IAction action) {
        run();
    }

    @Override
    public void run() {
        SchemaService.getInstance().reLoad();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void init(IWorkbenchWindow window) {
    }

}
