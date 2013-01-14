package org.csstudio.utility.channel.legacy;

import gov.bnl.channelfinder.api.Channel;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.eclipse.core.runtime.IAdapterFactory;

public class ChannelAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		Channel channel = ((Channel) adaptableObject);
		if (adapterType == IProcessVariable.class) {
			return CentralItemFactory.createProcessVariable(channel.getName());
		} else if (adapterType == IProcessVariableWithArchive.class) {
			return CentralItemFactory.createProcessVariableWithArchive(
					channel.getName(), null, 0, null);
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[] { IProcessVariable.class,
				IProcessVariableWithArchive.class };
	}
}
