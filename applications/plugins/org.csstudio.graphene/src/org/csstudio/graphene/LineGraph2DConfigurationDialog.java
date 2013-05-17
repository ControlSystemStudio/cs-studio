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
public class LineGraph2DConfigurationDialog
		extends
		AbstractConfigurationDialog<LineGraph2DWidget, LineGraph2DConfigurationPanel> {

	protected LineGraph2DConfigurationDialog(LineGraph2DWidget control, String title) {
		super(control, SWT.DIALOG_TRIM, title);
		addInitialValues("dataFormula", getWidget().getDataFormula());
		addInitialValues("xColumnFormula", getWidget().getXColumnFormula());
		addInitialValues("yColumnFormula", getWidget().getYColumnFormula());
	}

	@Override
	protected void onPropertyChange(PropertyChangeEvent evt) {
		getWidget()
				.setDataFormula(getConfigurationComposite().getDataFormula());
		getWidget().setXColumnFormula(
				getConfigurationComposite().getXColumnFormula());
		getWidget().setYColumnFormula(
				getConfigurationComposite().getYColumnFormula());
	}

	@Override
	protected void populateInitialValues() {
		getConfigurationComposite().setDataFormula(
				(String) getInitialValues().get("dataFormula"));
		getConfigurationComposite().setXColumnFormula(
				(String) getInitialValues().get("xColumnFormula"));
		getConfigurationComposite().setYColumnFormula(
				(String) getInitialValues().get("yColumnFormula"));
	}

	@Override
	protected LineGraph2DConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new LineGraph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
	}
}
