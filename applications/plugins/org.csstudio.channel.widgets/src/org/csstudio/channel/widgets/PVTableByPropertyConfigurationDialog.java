package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog used by the waterfall widget to modify the WaterfallWidget.
 * 
 * @author carcassi
 */
public class PVTableByPropertyConfigurationDialog 
extends AbstractConfigurationDialog<PVTableByPropertyWidget, PVTableByPropertyConfigurationPanel>  {

	public PVTableByPropertyConfigurationDialog(PVTableByPropertyWidget widget) {
		super(widget, SWT.DIALOG_TRIM, "Configure...");
		addInitialValues("possibleProperties", ChannelUtil.getPropertyNames(widget.getChannelQuery().getResult().channels));
		addInitialValues("possibleTags", ChannelUtil.getAllTagNames(widget.getChannelQuery().getResult().channels));
		addInitialValues("rowProperty", widget.getRowProperty());
		addInitialValues("columnProperty", widget.getColumnProperty());
		addInitialValues("columnTags", widget.getColumnTags());
	}
	
	protected void onPropertyChange(PropertyChangeEvent evt) {
		getWidget().setRowProperty(getConfigurationComposite().getRowProperty());
		getWidget().setColumnProperty(getConfigurationComposite().getColumnProperty());
		getWidget().setColumnTags(getConfigurationComposite().getColumnTags());
	}
	
	@SuppressWarnings("unchecked")
	protected void populateInitialValues() {
		getConfigurationComposite().setPossibleProperties((Collection<String>) getInitialValues().get("possibleProperties"));
		getConfigurationComposite().setPossibleTags((Collection<String>) getInitialValues().get("possibleTags"));
		getConfigurationComposite().setRowProperty((String) getInitialValues().get("rowProperty"));
		getConfigurationComposite().setColumnProperty((String) getInitialValues().get("columnProperty"));
		getConfigurationComposite().setColumnTags((List<String>) getInitialValues().get("columnTags"));
	}

	@Override
	protected PVTableByPropertyConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new PVTableByPropertyConfigurationPanel(shell, SWT.DIALOG_TRIM);
	}
}
