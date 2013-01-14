/**
 * 
 */
package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 * 
 */
public class TunerConfigurationDialog extends
		AbstractConfigurationDialog<TunerWidget, TunerConfigurationPanel> {

	protected TunerConfigurationDialog(TunerWidget widget) {
		super(widget, SWT.DIALOG_TRIM, "Configure Tuner");
		addInitialValues("possibleProperties", new ArrayList<String>(
				ChannelUtil.getPropertyNames(widget.getChannels())));
		addInitialValues("selectedProperties", widget.getProperties());
		addInitialValues(
				"possibleTags",
				new ArrayList<String>(ChannelUtil.getAllTagNames(widget
						.getChannels())));
		addInitialValues("selectedTags", widget.getTags());
	}

	@Override
	protected void onPropertyChange(PropertyChangeEvent evt) {
		getWidget().setProperties(
				getConfigurationComposite().getSelectedProperties());
		getWidget().setTags(getConfigurationComposite().getSelectedTags());
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
	}

	@Override
	protected TunerConfigurationPanel createConfigurationComposite(Shell shell) {
		return new TunerConfigurationPanel(shell, SWT.None);
	}

}
