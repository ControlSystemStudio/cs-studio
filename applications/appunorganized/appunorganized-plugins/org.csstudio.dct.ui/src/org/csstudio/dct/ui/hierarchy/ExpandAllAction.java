package org.csstudio.dct.ui.hierarchy;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * View action the collapses all tree items in the hierarchy view.
 *
 * @author Sven Wende
 *
 */
public final class ExpandAllAction implements IViewActionDelegate {
    private HierarchyView view;

    @Override
    public void init(IViewPart view) {
        this.view = (HierarchyView) view;
    }

    @Override
    public void run(IAction action) {
        view.getTreeViewer().expandAll();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {

    }
}
