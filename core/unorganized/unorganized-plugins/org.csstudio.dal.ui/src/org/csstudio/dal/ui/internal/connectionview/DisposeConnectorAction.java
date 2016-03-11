package org.csstudio.dal.ui.internal.connectionview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.platform.simpledal.IConnector;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action that dispose all currently selected connectors.
 *
 * @author Sven Wende
 *
 */
public class DisposeConnectorAction implements IObjectActionDelegate {

    private List<IConnector> _selectedConnectors;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IAction action) {
        if(_selectedConnectors!=null) {
            for(IConnector c : _selectedConnectors) {
                c.forceDispose();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection != null && selection instanceof IStructuredSelection) {
            IStructuredSelection sel = (IStructuredSelection) selection;

            _selectedConnectors = new ArrayList<IConnector>();

            Iterator it = sel.iterator();

            while (it.hasNext()) {
                Object o = it.next();

                if (o instanceof IConnector) {
                    _selectedConnectors.add((IConnector) o);
                }
            }
        }
    }

}
