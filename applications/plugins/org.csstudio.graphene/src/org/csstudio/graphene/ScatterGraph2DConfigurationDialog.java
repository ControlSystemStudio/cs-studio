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
public class ScatterGraph2DConfigurationDialog
        extends
        AbstractPointDatasetGraph2DConfigurationDialog<ScatterGraph2DWidget, ScatterGraph2DConfigurationPanel> {

    protected ScatterGraph2DConfigurationDialog(ScatterGraph2DWidget control, String title) {
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
    protected ScatterGraph2DConfigurationPanel createConfigurationComposite(
            Shell shell) {
        return new ScatterGraph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
    }
}
