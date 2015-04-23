/**
 * 
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;

/**
 * @author shroffk
 * 
 */
public abstract class AbstractPointDatasetGraph2DConfigurationDialog<T extends AbstractPointDatasetGraph2DWidget<?, ?>, S extends AbstractPointDatasetGraph2DConfigurationPanel>
		extends
		AbstractGraph2DConfigurationDialog<T, S> {

	protected AbstractPointDatasetGraph2DConfigurationDialog(T control, String title) {
		super(control, title);
		addInitialValues("xColumnFormula", getWidget().getXColumnFormula());
		addInitialValues("yColumnFormula", getWidget().getYColumnFormula());
	}

	@Override
	protected void onPropertyChange(PropertyChangeEvent evt) {
		super.onPropertyChange(evt);
		getWidget().setXColumnFormula(
				getConfigurationComposite().getXColumnFormula());
		getWidget().setYColumnFormula(
				getConfigurationComposite().getYColumnFormula());
	}

	@Override
	protected void populateInitialValues() {
		super.populateInitialValues();
		getConfigurationComposite().setXColumnFormula(
				(String) getInitialValues().get("xColumnFormula"));
		getConfigurationComposite().setYColumnFormula(
				(String) getInitialValues().get("yColumnFormula"));
	}

}
