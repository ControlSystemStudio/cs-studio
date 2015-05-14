package de.desy.language.snl.diagram.ui;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.ActionFactory;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;

/**
 * Contributes actions to a toolbar. This class is tied to the editor in the
 * definition of editor-extension (see plugin.xml).
 */
public class ShapesEditorActionBarContributor extends ActionBarContributor {

    /**
     * Create actions managed by this contributor.
     *
     * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
     */
    @Override
    protected void buildActions() {
        addRetargetAction(new DeleteRetargetAction());
        addRetargetAction(new UndoRetargetAction());
        addRetargetAction(new RedoRetargetAction());
    }

    /**
     * Add actions to the given toolbar.
     *
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    @Override
    public void contributeToToolBar(final IToolBarManager toolBarManager) {
        toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
        toolBarManager.add(getAction(ActionFactory.REDO.getId()));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
     */
    @Override
    protected void declareGlobalActionKeys() {
        // currently none
    }

}