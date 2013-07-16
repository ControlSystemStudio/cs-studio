package org.csstudio.frib.opiTemplate.widgets;

import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.channel.widgets.AbstractConfigurationDialog;
import org.csstudio.channel.widgets.ChannelViewerConfigurationPanel;
import org.csstudio.channel.widgets.ChannelViewerWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class OPITemplateConfigurationDialog extends
AbstractConfigurationDialog<OPITemplateWidget, OPITemplateConfigurationPanel> {

	protected OPITemplateConfigurationDialog(OPITemplateWidget widget) {
		super(widget, SWT.DIALOG_TRIM, "Configure Channel Viewer");
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
	protected OPITemplateConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new OPITemplateConfigurationPanel(shell, SWT.None);
	}

}
