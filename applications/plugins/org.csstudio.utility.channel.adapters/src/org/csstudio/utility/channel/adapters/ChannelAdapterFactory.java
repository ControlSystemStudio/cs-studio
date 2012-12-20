/**
 * 
 */
package org.csstudio.utility.channel.adapters;

import gov.bnl.channelfinder.api.Channel;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * @author shroffk
 * 
 */
public class ChannelAdapterFactory implements IAdapterFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
	 * java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		Channel channel = ((Channel) adaptableObject);
		if (adapterType == String.class) {
			return channel.toString();
		} else if (adapterType == ProcessVariable.class) {
			return new ProcessVariable(channel.getName());
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
		return new Class[] { String.class, ProcessVariable.class };
	}

}
