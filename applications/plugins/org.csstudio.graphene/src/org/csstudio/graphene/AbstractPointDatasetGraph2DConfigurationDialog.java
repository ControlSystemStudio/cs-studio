/**
 * 
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;

import org.csstudio.ui.util.AbstractConfigurationDialog;
import org.eclipse.swt.SWT;

/**
 * @author shroffk
 * 
 */
public abstract class AbstractPointDatasetGraph2DConfigurationDialog<T extends AbstractPointDatasetGraph2DWidget<?, ?>, S extends AbstractPointDatasetGraph2DConfigurationPanel>
		extends
		AbstractConfigurationDialog<T, S> {

	protected AbstractPointDatasetGraph2DConfigurationDialog(T control, String title) {
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

}
