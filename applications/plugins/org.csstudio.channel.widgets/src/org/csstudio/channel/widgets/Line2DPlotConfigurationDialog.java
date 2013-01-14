package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.beans.PropertyChangeEvent;
import java.util.Collection;

import org.csstudio.channel.widgets.Line2DPlotWidget.XAxis;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class Line2DPlotConfigurationDialog
		extends
		AbstractConfigurationDialog<Line2DPlotWidget, Line2DPlotConfigurationPanel> {

	protected Line2DPlotConfigurationDialog(Line2DPlotWidget widget) {
		super(widget, SWT.DIALOG_TRIM, "Title");
		addInitialValues("xOrdering", widget.getxOrdering());
		addInitialValues("xChannelQuery",
				widget.getXChannelQuery() != null ? widget.getXChannelQuery()
						.getQuery() : "");
		addInitialValues("offset",
				widget.getOffset() != null ? widget.getOffset() : "");
		addInitialValues("increment",
				widget.getIncrement() != null ? widget.getIncrement() : "");
		addInitialValues("sortProperty",
				widget.getSortProperty() != null ? widget.getSortProperty()
						: "");
		addInitialValues("properties", widget.getProperties());
	}

	@Override
	protected void onPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("xOrdering")) {
			getWidget().setxOrdering((XAxis) evt.getNewValue());
		} else if (evt.getPropertyName().equals("xChannelQuery")) {
			getWidget().setXChannelQuery(
					ChannelQuery.query(
							getConfigurationComposite().getXChannelQuery())
							.build());
		} else if (evt.getPropertyName().equals("sortProperty")) {
			getWidget().setSortProperty(
					getConfigurationComposite().getSortProperty());
		} else if (evt.getPropertyName().equals("offset")
				|| evt.getPropertyName().equals("increment")) {
			getWidget().setOffset(getConfigurationComposite().getOffset());
			getWidget()
					.setIncrement(getConfigurationComposite().getIncrement());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void populateInitialValues() {
		getConfigurationComposite().setXOrdering(
				(XAxis) getInitialValues().get("xOrdering"));
		getConfigurationComposite().setXChannelQuery(
				(String) getInitialValues().get("xChannelQuery"));
		getConfigurationComposite().setOffset(
				(String) getInitialValues().get("offset"));
		getConfigurationComposite().setIncrement(
				(String) getInitialValues().get("increment"));
		getConfigurationComposite().setProperties(
				(Collection<String>) getInitialValues().get("properties"));
		getConfigurationComposite().setSortProperty(
				(String) getInitialValues().get("sortProperty"));
	}

	@Override
	protected Line2DPlotConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new Line2DPlotConfigurationPanel(shell, SWT.DIALOG_TRIM);
	}

}
