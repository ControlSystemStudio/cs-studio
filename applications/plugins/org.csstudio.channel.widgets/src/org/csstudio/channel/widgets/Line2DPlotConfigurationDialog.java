package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.beans.PropertyChangeEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class Line2DPlotConfigurationDialog
		extends
		AbstractConfigurationDialog<Line2DPlotWidget, Line2DPlotConfigurationPanel> {

	protected Line2DPlotConfigurationDialog(Line2DPlotWidget control) {
		super(control, SWT.DIALOG_TRIM, "Title");
		addInitialValues("xChannelQuery",
				control.getXChannelQuery() != null ? control.getXChannelQuery()
						.getQuery() : "");
	}

	@Override
	protected void onPropertyChange(PropertyChangeEvent evt) {
		getWidget()
				.setXChannelQuery(
						ChannelQuery.query(
								getConfigurationComposite().getXChannelQuery())
								.build());
	}

	@Override
	protected void populateInitialValues() {
		getConfigurationComposite().setXChannelQuery(
				(String) getInitialValues().get("xChannelQuery"));
	}

	@Override
	protected Line2DPlotConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new Line2DPlotConfigurationPanel(shell, SWT.DIALOG_TRIM);
	}

}
