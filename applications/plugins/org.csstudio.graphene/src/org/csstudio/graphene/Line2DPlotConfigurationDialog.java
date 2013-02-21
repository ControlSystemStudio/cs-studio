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
public class Line2DPlotConfigurationDialog extends
	AbstractConfigurationDialog<Line2DPlotWidget, Line2DPlotConfigurationPanel> {

    protected Line2DPlotConfigurationDialog(Line2DPlotWidget control) {
	super(control, SWT.DIALOG_TRIM, "Configure Line2D Plot");
    }

    @Override
    protected void onPropertyChange(PropertyChangeEvent evt) {
	getWidget().setPvName(getConfigurationComposite().getYPv());
	getWidget().setXPvName(getConfigurationComposite().getXPv());
	getWidget().setShowAxis(getConfigurationComposite().getShowAxis());
    }

    @Override
    protected void populateInitialValues() {
	getConfigurationComposite().setYPv(getWidget().getPvName());
	getConfigurationComposite().setXPv(getWidget().getXpvName());
	getConfigurationComposite().setShowAxis(getWidget().getShowAxis());
    }

    @Override
    protected Line2DPlotConfigurationPanel createConfigurationComposite(Shell shell) {
	return new Line2DPlotConfigurationPanel(shell, SWT.DIALOG_TRIM);
    }

}
