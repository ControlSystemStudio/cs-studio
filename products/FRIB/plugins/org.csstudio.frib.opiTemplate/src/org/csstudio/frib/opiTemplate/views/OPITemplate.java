package org.csstudio.frib.opiTemplate.views;

import org.csstudio.channel.views.AbstractChannelQueryView;
import org.csstudio.frib.opiTemplate.widgets.OPITemplateWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

public class OPITemplate extends AbstractChannelQueryView<OPITemplateWidget> {
	
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.frib.opiTemplate.views.OPITemplate";

	@Override
	public void saveWidgetState(OPITemplateWidget widget, IMemento memento) {	
	}

	@Override
	public void loadWidgetState(OPITemplateWidget widget, IMemento memento) {	
	}

	@Override
	protected OPITemplateWidget createChannelQueryWidget(Composite parent,
			int style) {
		return new OPITemplateWidget(parent, style);
	}

}
