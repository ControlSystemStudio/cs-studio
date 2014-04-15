/**
 * 
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.epics.graphene.NumberColorMap;

/**
 * @author shroffk
 * 
 */
public class IntensityGraph2DConfigurationDialog
		extends
		AbstractGraph2DConfigurationDialog<IntensityGraph2DWidget, IntensityGraph2DConfigurationPanel> {

	protected IntensityGraph2DConfigurationDialog(IntensityGraph2DWidget control, String title) {
		super(control, title);
		addInitialValues("colorMap", control.getColorMap());
	}

	@Override
	protected void onPropertyChange(PropertyChangeEvent evt) {
		super.onPropertyChange(evt);
		getWidget().setColorMap(getConfigurationComposite().getColorMap());
	}

	@Override
	protected void populateInitialValues() {
		super.populateInitialValues();
		getConfigurationComposite().setColorMap(
				(NumberColorMap) getInitialValues().get("colorMap"));
	}

	@Override
	protected IntensityGraph2DConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new IntensityGraph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
	}
}
