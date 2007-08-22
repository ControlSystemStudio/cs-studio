package org.csstudio.platform.ui.dnd.rfc;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAdress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressListProvider;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Base class for popup menu actions used in Object contributions for
 * {@link IProcessVariableAdressListProvider}.
 * 
 * @author Sven Wende
 * 
 */
public abstract class ProcessVariablePopupAction implements IObjectActionDelegate {

	private List<IProcessVariableAdressListProvider> _pvAdressListProviders;

	public ProcessVariablePopupAction() {
		_pvAdressListProviders = new ArrayList<IProcessVariableAdressListProvider>();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

	public void run(IAction action) {
		for (IProcessVariableAdressListProvider provider : _pvAdressListProviders) {
			handlePvs(provider.getPVAdressList());
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		_pvAdressListProviders.clear();

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;

			for (Object o : sel.toList()) {
				if (o instanceof IProcessVariableAdressListProvider) {
					_pvAdressListProviders.add((IProcessVariableAdressListProvider) o);
				}
			}
		}

	}

	protected abstract void handlePvs(List<IProcessVariableAdress> pvs);

}
