package org.csstudio.opibuilder.editparts;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/**The adaptor factory to make a PV widget as a PV provider for css context menu.
 * @author Xihui Chen
 *
 */
public class PVWidgetAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if(adapterType == ProcessVariable.class && adaptableObject instanceof AbstractPVWidgetEditPart){
			return new ProcessVariable(((AbstractPVWidgetEditPart)adaptableObject).getName());
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
        return new Class<?>[] { ProcessVariable.class };
	}

}
