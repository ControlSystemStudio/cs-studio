package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
@Deprecated
public class DeleteConfiguration extends AbstractHandler implements
		IHandler {
	@Deprecated
	private static LocalStoreConfigurationService localStore;
	@Deprecated
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AlarmbearbeiterBean object = (AlarmbearbeiterBean) arg0.getObjectParameterForExecution("deleteConfiguration.command.target");
		//TODO implement delete
		return null;
	}
	@Deprecated
	public static void staticInject(LocalStoreConfigurationService localStore){
		DeleteConfiguration.localStore = localStore;
		
	}

}
