
package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.csstudio.nams.service.regelwerkbuilder.extensionPoint.RegelwerkBuilderServiceFactory;

public class RegelwerkBuilderServiceFactoryImpl implements
		RegelwerkBuilderServiceFactory {

	public RegelwerkBuilderServiceFactoryImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
    public RegelwerkBuilderService createService() {
		// TODO Auto-generated method stub
		return new RegelwerkBuilderServiceImpl();
	}
}
