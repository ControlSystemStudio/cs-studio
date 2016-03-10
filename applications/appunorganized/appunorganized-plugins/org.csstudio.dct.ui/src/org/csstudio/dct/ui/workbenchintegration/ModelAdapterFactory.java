package org.csstudio.dct.ui.workbenchintegration;

import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;

/**
 * Adapter factory that covers all elements of the DCT model.
 *
 * @author Sven Wende
 *
 */
@SuppressWarnings("unchecked")
public final class ModelAdapterFactory implements IAdapterFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        Object adapter = null;

        if (adapterType == IWorkbenchAdapter.class || adapterType == IWorkbenchAdapter2.class) {
            if (adaptableObject instanceof IProject) {
                adapter = new ProjectWorkbenchAdapter();
            } else if (adaptableObject instanceof IFolder) {
                adapter = new FolderWorkbenchAdapter();
            } else if (adaptableObject instanceof IPrototype) {
                adapter = new PrototypeWorkbenchAdapter();
            } else if (adaptableObject instanceof IInstance) {
                adapter = new InstanceWorkbenchAdapter();
            } else if (adaptableObject instanceof IRecord) {
                adapter = new RecordWorkbenchAdapter();
            } else if (adaptableObject instanceof IRecordDefinition) {
                adapter = new RecordDefinitionWorkbenchAdapter();
            }
        } else if(adapterType == IActionFilter.class) {
            adapter = new ActionFilterAdapter();
        }
        return adapter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter.class, IWorkbenchAdapter2.class, IActionFilter.class };
    }

}
