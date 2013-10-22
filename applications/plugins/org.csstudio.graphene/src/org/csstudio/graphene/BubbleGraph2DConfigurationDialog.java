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
public class BubbleGraph2DConfigurationDialog
		extends
		AbstractPointDatasetGraph2DConfigurationDialog<BubbleGraph2DWidget, BubbleGraph2DConfigurationPanel> {

	protected BubbleGraph2DConfigurationDialog(BubbleGraph2DWidget control, String title) {
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
	protected BubbleGraph2DConfigurationPanel createConfigurationComposite(
			Shell shell) {
		return new BubbleGraph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
	}
}
