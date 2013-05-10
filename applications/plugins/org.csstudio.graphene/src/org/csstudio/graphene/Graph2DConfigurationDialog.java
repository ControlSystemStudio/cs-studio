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
public class Graph2DConfigurationDialog
	extends
	AbstractConfigurationDialog<AbstractPointDatasetGraph2DWidget, Graph2DConfigurationPanel> {

    protected Graph2DConfigurationDialog(AbstractPointDatasetGraph2DWidget control, String title) {
	super(control, SWT.DIALOG_TRIM, title);
	addInitialValues("showAxisScroll", getWidget().isShowAxis());
//	addInitialValues("yPv", getWidget().getPvName());
//	addInitialValues("xPv", getWidget().getXpvName());
    }

    @Override
    protected void onPropertyChange(PropertyChangeEvent evt) {
//	getWidget().setPvName(getConfigurationComposite().getYPv());
//	getWidget().setXPvName(getConfigurationComposite().getXPv());
	getWidget().setShowAxis(getConfigurationComposite().getShowAxis());
    }

    @Override
    protected void populateInitialValues() {
	getConfigurationComposite().setYPv((String) getInitialValues().get("yPv"));
	getConfigurationComposite().setXPv((String) getInitialValues().get("xPv"));
	getConfigurationComposite().setShowAxis((Boolean) getInitialValues().get("showAxisScroll"));
    }

    @Override
    protected Graph2DConfigurationPanel createConfigurationComposite(Shell shell) {
	return new Graph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
    }
}
