
package org.csstudio.nams.configurator.views;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class ShowAMS implements IWorkbenchWindowActionDelegate {

    /** A workbench window handle. */
    private IWorkbenchWindow _window;

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    @Override
    public void init(IWorkbenchWindow window) {
        _window = window;
    }

    @Override
    public void run(IAction action) {
        try {
            PlatformUI.getWorkbench().showPerspective(
                    "org.csstudio.nams.newconfigurator.perspective", _window);
        } catch (WorkbenchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // TODO Auto-generated method stub
    }
}
