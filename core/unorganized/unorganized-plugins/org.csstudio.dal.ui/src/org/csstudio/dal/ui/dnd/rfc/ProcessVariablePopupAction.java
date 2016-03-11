/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.dal.ui.dnd.rfc;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
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
public abstract class ProcessVariablePopupAction implements
        IObjectActionDelegate {

    private List<WeakReference<IProcessVariableAdressProvider>> _pvAdressListProviders;

    public ProcessVariablePopupAction() {
        _pvAdressListProviders = new ArrayList<WeakReference<IProcessVariableAdressProvider>>();
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {

    }

    public void run(IAction action) {
        Set<IProcessVariableAddress> pvs = new HashSet<IProcessVariableAddress>();

        for (WeakReference<IProcessVariableAdressProvider> ref : _pvAdressListProviders) {
            IProcessVariableAdressProvider provider = ref.get();
            if (provider != null) {
                pvs.addAll(provider.getProcessVariableAdresses());
            }
        }

        handlePvs(pvs);
    }

    public void selectionChanged(IAction action, ISelection selection) {
        _pvAdressListProviders.clear();

        if (selection instanceof IStructuredSelection) {
            IStructuredSelection sel = (IStructuredSelection) selection;

            for (Object o : sel.toList()) {
                if (o instanceof IProcessVariableAdressProvider) {
                    _pvAdressListProviders
                            .add(new WeakReference<IProcessVariableAdressProvider>((IProcessVariableAdressProvider) o));
                }
            }
        }

    }

    protected abstract void handlePvs(Set<IProcessVariableAddress> pvs);

}
