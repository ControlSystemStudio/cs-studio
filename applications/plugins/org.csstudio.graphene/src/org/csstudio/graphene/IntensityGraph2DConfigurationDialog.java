/**
 * 
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 * 
 */
public class IntensityGraph2DConfigurationDialog
		extends
		AbstractGraph2DConfigurationDialog<IntensityGraph2DWidget, IntensityGraph2DConfigurationPanel> {

	protected IntensityGraph2DConfigurationDialog(IntensityGraph2DWidget control, String title) {
		super(control, title);
	}

	@Override
	protected void onPropertyChange(PropertyChangeEvent evt) {
		super.onPropertyChange(evt);
	}

	@Override
	protected void populateInitialValues() {
		super.populateInitialValues();
	}

	@Override
	protected IntensityGraph2DConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new IntensityGraph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
	}
}
