package org.csstudio.nams.configurator.actions;

import org.csstudio.nams.configurator.views.AlarmbearbeiterView;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ConfiguratorActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction newAction;
	private IWorkbenchAction deleteAction;
	
	public ConfiguratorActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}
	
	protected void makeActions(final IWorkbenchWindow window){
		IWorkbenchPage activePage = window.getActivePage();
		if (activePage instanceof AlarmbearbeiterView){
//			newAction = WorkbenchA
//				new NewEmptyConfigurator(AlarmbearbeiterBean.class, new ConfigurationModel());
		}
	}

}
