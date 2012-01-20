package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class ChannelsViewConfigurationDialog
		extends
		AbstractConfigurationDialog<ChannelsViewWidget, ChannelsViewConfigurationPanel> {

	protected ChannelsViewConfigurationDialog(ChannelsViewWidget widget,
			int style, String title) {
		super(widget, style, title);
		addInitialValues("possibleProperties", new ArrayList<String>(
				ChannelUtil.getPropertyNames(widget.getChannels())));
		addInitialValues("selectedProperties", widget.getProperties());
		addInitialValues(
				"possibleTags",
				new ArrayList<String>(ChannelUtil.getAllTagNames(widget
						.getChannels())));
		addInitialValues("selectedTags", widget.getTags());
		// addInitialValues("showChannelNames", widget.isShowChannelNames());
	}

	@Override
	protected void onPropertyChange(PropertyChangeEvent evt) {
		getWidget().setProperties(
				getConfigurationComposite().getSelectedProperties());
		getWidget().setTags(getConfigurationComposite().getSelectedTags());
		// getWidget().setShowChannelNames(getConfigurationComposite().isShowChannelNames());
	}

	@Override
	protected void populateInitialValues() {
		getConfigurationComposite().setPossibleProperties(
				(List<String>) getInitialValues().get("possibleProperties"));
		getConfigurationComposite().setSelectedProperties(
				(List<String>) getInitialValues().get("selectedProperties"));
		getConfigurationComposite().setPossibleTags(
				(List<String>) getInitialValues().get("possibleTags"));
		getConfigurationComposite().setSelectedTags(
				(List<String>) getInitialValues().get("selectedTags"));
		// getConfigurationComposite().setShowChannelNames((Boolean)
		// getInitialValues().get("showChannelNames"));
	}

	@Override
	protected ChannelsViewConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new ChannelsViewConfigurationPanel(shell, SWT.None);
	}

}
