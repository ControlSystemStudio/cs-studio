package org.csstudio.nams.configurator.ui.views;

import org.csstudio.nams.configurator.model.declaration.ConfigurationElementModelAccessService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class AlarmbearbeiterView extends ViewPart {

	private static Logger logger;
	private static ConfigurationElementModelAccessService configurationElementModelAccessService;

	public AlarmbearbeiterView() {
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		logger.logInfoMessage(this, "AlarmbearbeiterView#init(IViewSite");
		
		// TODO Initalie Daten holen...
	}
	
	@Override
	public void createPartControl(Composite parent) {
		new FilteredListWidget(parent, SWT.None);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	public static void staticInject(Logger logger) {
		AlarmbearbeiterView.logger = logger;
	}

	public static void staticInject(
			ConfigurationElementModelAccessService configurationElementModelAccessService) {
		AlarmbearbeiterView.configurationElementModelAccessService = configurationElementModelAccessService;
	}

}
