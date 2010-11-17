package org.csstudio.platform.ui.internal.workbench;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.PlatformUI;

/**The factory to provide UndoContextAdapter.
 * @author Abadie Lana (ITER)
 *
 */
public class UndoContextAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IUndoContext.class) {
        	return getUndoContext(adaptableObject);
        }
        return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
        return new Class[] { IUndoContext.class };
    }
    
    /**
     * Returns the IUndoContext for an object.
     */
    protected Object getUndoContext(Object o) {
        if (o instanceof IWorkspace) {
            return PlatformUI.getWorkbench().getOperationSupport().getUndoContext();
        }
        return null;
    }

}
