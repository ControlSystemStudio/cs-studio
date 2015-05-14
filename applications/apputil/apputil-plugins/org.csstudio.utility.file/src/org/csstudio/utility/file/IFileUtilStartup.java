package org.csstudio.utility.file;

import org.csstudio.utility.product.IWorkbenchWindowAdvisorExtPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IMemento;

public class IFileUtilStartup implements IWorkbenchWindowAdvisorExtPoint{

    @Override
    public void preWindowOpen() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean preWindowShellClose() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void postWindowRestore() {


    }

    @Override
    public void postWindowCreate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void postWindowOpen() {

    }

    @Override
    public void postWindowClose() {
        // TODO Auto-generated method stub

    }

    @Override
    public IStatus saveState(IMemento memento) {
        IFileUtil.getInstance().saveState(memento);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus restoreState(IMemento memento) {
        IFileUtil.getInstance().restoreState(memento);
        return Status.OK_STATUS;
    }



}
