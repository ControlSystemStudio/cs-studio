package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class ChannelTreeByPropertyConfigurationDialog
extends AbstractConfigurationDialog<ChannelTreeByPropertyWidget, PropertyListSelectionWidget> {

	public ChannelTreeByPropertyConfigurationDialog(ChannelTreeByPropertyWidget widget) {
		super(widget, SWT.DIALOG_TRIM, "Select properties...");
		addInitialValues("channels", widget.getChannelQuery().getResult().channels);
		addInitialValues("selectedProperties", widget.getProperties());
	}
	
	protected void onPropertyChange(PropertyChangeEvent evt) {
		getWidget().setProperties(getConfigurationComposite().getSelectedProperties());
	}
	
	@SuppressWarnings("unchecked")
	protected void populateInitialValues() {
		getConfigurationComposite().setChannels((Collection<Channel>) getInitialValues().get("channels"));
		getConfigurationComposite().setSelectedProperties((List<String>) getInitialValues().get("selectedProperties"));
	}

	@Override
	protected PropertyListSelectionWidget createConfigurationComposite(
			Shell shell) {
		return new PropertyListSelectionWidget(shell, SWT.DIALOG_TRIM);
	}
	
}
