/**
 * 
 */
package org.csstudio.utility.channel.adapters;

import gov.bnl.channelfinder.api.Channel;

import org.csstudio.csdata.ProcessVariableName;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.csstudio.utility.channel.ICSSChannel;
import org.csstudio.utility.channel.nsls2.CSSChannelFactory;
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
		} else if (adapterType == ICSSChannel.class) {
			return CSSChannelFactory.getInstance().getCSSChannel(channel);
		} else if (adapterType == IProcessVariable.class) {
			return CSSChannelFactory.getInstance().getCSSChannel(channel);
		} else if (adapterType == ProcessVariableName.class) {
			return new ProcessVariableName(channel.getName());
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
		return new Class[] { String.class, ICSSChannel.class,
				IProcessVariable.class, IProcessVariableWithArchive.class,
				ProcessVariableName.class };
	}

}
