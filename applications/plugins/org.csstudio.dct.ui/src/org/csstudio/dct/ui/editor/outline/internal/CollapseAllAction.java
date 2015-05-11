package org.csstudio.dct.ui.editor.outline.internal;

import org.eclipse.jface.action.IAction;

/**
 * View action the collapses all tree items.
 *
 * @author Sven Wende
 *
 */
public final class CollapseAllAction extends AbstractOutlineViewAction {

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRun(OutlinePage outlinePage, IAction action) {
        outlinePage.getViewer().collapseAll();
    }
}
