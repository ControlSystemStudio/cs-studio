package org.csstudio.dct.ui.editor.outline.internal;

import org.eclipse.jface.action.IAction;

/**
 * View action the expands all tree items.
 *
 * @author Sven Wende
 *
 */
public final class ExpandAllAction extends AbstractOutlineViewAction {

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRun(OutlinePage outlinePage, IAction action) {
        outlinePage.getViewer().expandAll();
    }
}
