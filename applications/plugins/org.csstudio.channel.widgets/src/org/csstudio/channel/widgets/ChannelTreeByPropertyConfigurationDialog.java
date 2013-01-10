package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class ChannelTreeByPropertyConfigurationDialog
extends AbstractConfigurationDialog<ChannelTreeByPropertyWidget, ChannelTreeByPropertyConfigurationPanel> {

	public ChannelTreeByPropertyConfigurationDialog(ChannelTreeByPropertyWidget widget) {
		super(widget, SWT.DIALOG_TRIM, "Select properties...");
		addInitialValues("channels", widget.getChannelQuery().getResult().channels);
		addInitialValues("selectedProperties", widget.getProperties());
		addInitialValues("showChannelNames", widget.isShowChannelNames());
	}
	
	protected void onPropertyChange(PropertyChangeEvent evt) {
		getWidget().setProperties(getConfigurationComposite().getSelectedProperties());
		getWidget().setShowChannelNames(getConfigurationComposite().isShowChannelNames());
	}
	
	@SuppressWarnings("unchecked")
	protected void populateInitialValues() {
		getConfigurationComposite().setChannels((Collection<Channel>) getInitialValues().get("channels"));
		getConfigurationComposite().setSelectedProperties((List<String>) getInitialValues().get("selectedProperties"));
		getConfigurationComposite().setShowChannelNames((Boolean) getInitialValues().get("showChannelNames"));
	}

	@Override
	protected ChannelTreeByPropertyConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new ChannelTreeByPropertyConfigurationPanel(shell, SWT.DIALOG_TRIM);
	}
	
}
