/**
 * 
 */
package org.csstudio.utility.channel.adapters;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * @author shroffk
 * 
 *         Adapter factory for ICSSChannel
 * 
 */
public class ICSSChannelAdapterFactory implements IAdapterFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
	 * java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		ICSSChannel channel = ((ICSSChannel)adaptableObject);
		if (adapterType == String.class) {
			return channel.toString();
		} else if (adapterType == IProcessVariable.class) {			
			return null;	
		} else if (adapterType == IProcessVariableWithArchive.class) {
			return null;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class[] getAdapterList() {
		return new Class[] { String.class, IProcessVariable.class,
				IProcessVariableWithArchive.class };
	}

}
