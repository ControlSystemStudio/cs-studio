package org.csstudio.dct.ui.editor.outline.internal;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;

/**
 * Base class for actions that are used in the outline's view menu.
 *
 * @author Sven Wende
 *
 */
abstract class AbstractOutlineViewAction implements IViewActionDelegate {
    private ContentOutline outline;

    /**
     *{@inheritDoc}
     */
    @Override
    public final void init(IViewPart view) {
        this.outline = (ContentOutline) view;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void run(IAction action) {
        OutlinePage outlinePage = null;

        if (outline.getCurrentPage() instanceof OutlinePage) {
            outlinePage = (OutlinePage) outline.getCurrentPage();
        }

        if (outlinePage != null) {
            doRun(outlinePage, action);
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final void selectionChanged(IAction action, ISelection selection) {

    }

    /**
     * Performs this action.
     *
     * @param outlinePage
     *            the outline page
     * @param action
     *            the action proxy that handles the presentation portion of the
     *            action
     */
    protected abstract void doRun(OutlinePage outlinePage, IAction action);
}
