package org.csstudio.saverestore.ui.util;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.saverestore.data.SaveSetEntry;
import org.csstudio.saverestore.ui.ObservableSaveSetEntry;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factory for {@link SaveSetEntry}
 * @author Kunal Shroff
 *
 */
public class DataAdapterFactory implements IAdapterFactory {

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if(adaptableObject instanceof ObservableSaveSetEntry){
            final ObservableSaveSetEntry entry = ((ObservableSaveSetEntry) adaptableObject);
            return new ProcessVariable(entry.getPvname());
        }
        return null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { ProcessVariable.class };
    }

}
