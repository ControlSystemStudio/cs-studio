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
public class LineGraph2DConfigurationDialog
		extends
		AbstractPointDatasetGraph2DConfigurationDialog<LineGraph2DWidget, LineGraph2DConfigurationPanel> {

	protected LineGraph2DConfigurationDialog(LineGraph2DWidget control, String title) {
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
	protected LineGraph2DConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new LineGraph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
	}
}
