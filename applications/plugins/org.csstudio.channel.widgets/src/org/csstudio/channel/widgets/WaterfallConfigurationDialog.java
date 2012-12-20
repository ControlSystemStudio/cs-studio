package org.csstudio.channel.widgets;

import java.beans.PropertyChangeEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.epics.pvmanager.util.TimeDuration;

/**
 * Dialog used by the waterfall widget to modify the WaterfallWidget.
 * 
 * @author carcassi
 */
public class WaterfallConfigurationDialog 
extends AbstractConfigurationDialog<WaterfallWidget, WaterfallConfigurationPanel>  {

	public WaterfallConfigurationDialog(WaterfallWidget widget) {
		super(widget, SWT.DIALOG_TRIM, "Select properties...");
		addInitialValues("showTimeAxis", widget.isShowTimeAxis());
		addInitialValues("adaptiveRange", widget.isAdaptiveRange());
		addInitialValues("sortProperty", widget.getSortProperty());
		addInitialValues("scrollDirection", widget.getScrollDirection());
		addInitialValues("resolution", widget.getPixelDuration());
	}
	
	protected void onPropertyChange(PropertyChangeEvent evt) {
		getWidget().setPixelDuration(getConfigurationComposite().getResolution());
		getWidget().setShowTimeAxis(getConfigurationComposite().isShowTimeAxis());
		getWidget().setSortProperty(getConfigurationComposite().getSortProperty());
		getWidget().setScrollDirection(getConfigurationComposite().getScrollDirection());
		getWidget().setAdaptiveRange(getConfigurationComposite().isAdaptiveRange());
	}
	
	protected void populateInitialValues() {
		getConfigurationComposite().setShowTimeAxis((Boolean) getInitialValues().get("showTimeAxis"));
		getConfigurationComposite().setAdaptiveRange((Boolean) getInitialValues().get("adaptiveRange"));
		getConfigurationComposite().setSortProperty((String) getInitialValues().get("sortProperty"));
		getConfigurationComposite().setScrollDirection((Integer) getInitialValues().get("scrollDirection"));
		getConfigurationComposite().setResolution((TimeDuration) getInitialValues().get("resolution"));
	}

	@Override
	protected WaterfallConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new WaterfallConfigurationPanel(shell, SWT.DIALOG_TRIM);
	}
}
