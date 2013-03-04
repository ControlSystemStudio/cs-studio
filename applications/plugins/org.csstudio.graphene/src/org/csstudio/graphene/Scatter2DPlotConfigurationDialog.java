/**
 * 
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;

import org.csstudio.ui.util.AbstractConfigurationDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 * 
 */
public class Scatter2DPlotConfigurationDialog
	extends
	AbstractConfigurationDialog<Scatter2DPlotWidget, Scatter2DPlotConfigurationPanel> {

    protected Scatter2DPlotConfigurationDialog(Scatter2DPlotWidget control) {
	super(control, SWT.DIALOG_TRIM, "Configure Scatter2D Plot");
	addInitialValues("showAxisScroll", getWidget().getShowAxis());
	addInitialValues("yPv", getWidget().getPvName());
	addInitialValues("xPv", getWidget().getXpvName());
    }

    @Override
    protected void onPropertyChange(PropertyChangeEvent evt) {
	getWidget().setPvName(getConfigurationComposite().getYPv());
	getWidget().setXPvName(getConfigurationComposite().getXPv());
	getWidget().setShowAxis(getConfigurationComposite().getShowAxis());
    }

    @Override
    protected void populateInitialValues() {
	getConfigurationComposite().setYPv((String) getInitialValues().get("yPv"));
	getConfigurationComposite().setXPv((String) getInitialValues().get("xPv"));
	getConfigurationComposite().setShowAxis((Boolean) getInitialValues().get("showAxisScroll"));
    }

    @Override
    protected Scatter2DPlotConfigurationPanel createConfigurationComposite(Shell shell) {
	return new Scatter2DPlotConfigurationPanel(shell, SWT.DIALOG_TRIM);
    }
}
