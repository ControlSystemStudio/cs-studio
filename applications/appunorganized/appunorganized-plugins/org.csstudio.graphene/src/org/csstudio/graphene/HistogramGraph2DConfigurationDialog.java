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
public class HistogramGraph2DConfigurationDialog
        extends
        AbstractGraph2DConfigurationDialog<HistogramGraph2DWidget, HistogramGraph2DConfigurationPanel> {

    protected HistogramGraph2DConfigurationDialog(HistogramGraph2DWidget control, String title) {
        super(control, title);
        addInitialValues("highlightFocusValue", getWidget()
                .isHighlightSelectionValue());
    }

    @Override
    protected void onPropertyChange(PropertyChangeEvent evt) {
        super.onPropertyChange(evt);
        getWidget().setHighlightSelectionValue(
                getConfigurationComposite().isHighlightFocusValue());
    }

    @Override
    protected void populateInitialValues() {
        super.populateInitialValues();
        getConfigurationComposite().setHighlightFocusValue(
                (Boolean) getInitialValues().get("highlightFocusValue"));
    }

    @Override
    protected HistogramGraph2DConfigurationPanel createConfigurationComposite(
            Shell shell) {
        return new HistogramGraph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
    }
}
