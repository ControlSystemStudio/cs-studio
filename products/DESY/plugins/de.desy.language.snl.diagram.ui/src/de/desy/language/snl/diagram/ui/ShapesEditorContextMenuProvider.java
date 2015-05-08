package de.desy.language.snl.diagram.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;

/**
 * Provides context menu actions for the ShapesEditor.
 */
class ShapesEditorContextMenuProvider extends ContextMenuProvider {

    /** The editor's action registry. */
    private final ActionRegistry actionRegistry;

    /**
     * Instantiate a new menu context provider for the specified EditPartViewer
     * and ActionRegistry.
     *
     * @param viewer
     *            the editor's graphical viewer
     * @param registry
     *            the editor's action registry
     * @throws IllegalArgumentException
     *             if registry is <tt>null</tt>.
     */
    public ShapesEditorContextMenuProvider(final EditPartViewer viewer,
            final ActionRegistry registry) {
        super(viewer);
        if (registry == null) {
            throw new IllegalArgumentException();
        }
        actionRegistry = registry;
    }

    /**
     * Called when the context menu is about to show. Actions, whose state is
     * enabled, will appear in the context menu.
     *
     * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void buildContextMenu(final IMenuManager menu) {
        // Add standard action groups to the menu
        GEFActionConstants.addStandardActionGroups(menu);

        // Add actions to the menu
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO, // target group id
                getAction(ActionFactory.UNDO.getId())); // action to add
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO,
                getAction(ActionFactory.REDO.getId()));
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT,
                getAction(ActionFactory.DELETE.getId()));
    }

    private IAction getAction(final String actionId) {
        return actionRegistry.getAction(actionId);
    }

}
