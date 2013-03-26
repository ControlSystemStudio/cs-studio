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
public class LineGraph2DConfigurationDialog extends
	AbstractConfigurationDialog<LineGraph2DWidget, LineGraph2DConfigurationPanel> {

    protected LineGraph2DConfigurationDialog(LineGraph2DWidget control) {
	super(control, SWT.DIALOG_TRIM, "Configure Line2D Plot");
	addInitialValues("showAxisScroll", getWidget().isShowAxis());
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
    protected LineGraph2DConfigurationPanel createConfigurationComposite(Shell shell) {
	return new LineGraph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
    }

}
