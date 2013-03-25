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
public class ScatterGraph2DConfigurationDialog
	extends
	AbstractConfigurationDialog<ScatterGraph2DWidget, ScatterGraph2DConfigurationPanel> {

    protected ScatterGraph2DConfigurationDialog(ScatterGraph2DWidget control) {
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
    protected ScatterGraph2DConfigurationPanel createConfigurationComposite(Shell shell) {
	return new ScatterGraph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
    }
}
