package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.beans.PropertyChangeEvent;
import java.util.Collection;

import org.csstudio.channel.widgets.ChannelLinePlotWidget.XAxis;
import org.csstudio.utility.pvmanager.widgets.AbstractConfigurationDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class ChannelLinePlotConfigurationDialog
		extends
		AbstractConfigurationDialog<ChannelLinePlotWidget, ChannelLinePlotConfigurationPanel> {

	protected ChannelLinePlotConfigurationDialog(ChannelLinePlotWidget widget) {
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
	protected ChannelLinePlotConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new ChannelLinePlotConfigurationPanel(shell, SWT.DIALOG_TRIM);
	}

}
