package org.csstudio.opibuilder.editparts;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/**The adaptor factory to make a PV widget as a PV provider for css context menu.
 * @author Xihui Chen
 *
 */
public class PVWidgetAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if(adaptableObject instanceof AbstractPVWidgetEditPart){
			if (adapterType == ProcessVariable.class) {
				return new ProcessVariable(((AbstractPVWidgetEditPart) adaptableObject).getName());
			}else if(adapterType == ProcessVariable[].class) {
				String[] allPVNames = ((AbstractPVWidgetEditPart) adaptableObject)
						.getAllPVNames();
				ProcessVariable[] pvs = new ProcessVariable[allPVNames.length];
				int i = 0;
				for (String s : allPVNames) {
					pvs[i++] = new ProcessVariable(s);
				}
				return pvs;
			}
			
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
        return new Class<?>[] { ProcessVariable.class, ProcessVariable[].class };
	}

}
