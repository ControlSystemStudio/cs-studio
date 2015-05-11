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
        addInitialValues("highlightFocusValue", getWidget()
                .isHighlightSelectionValue());
        addInitialValues("sizeColumnFormula", getWidget()
                .getSizeColumnFormula());
        addInitialValues("colorColumnFormula", getWidget()
                .getColorColumnFormula());
    }

    @Override
    protected void onPropertyChange(PropertyChangeEvent evt) {
        super.onPropertyChange(evt);
        getWidget().setHighlightSelectionValue(
                getConfigurationComposite().isHighlightFocusValue());
        getWidget().setSizeColumnFormula(
                getConfigurationComposite().getSizeColumnFormula());
        getWidget().setColorColumnFormula(
                getConfigurationComposite().getColorColumnFormula());
    }

    @Override
    protected void populateInitialValues() {
        super.populateInitialValues();
        getConfigurationComposite().setHighlightFocusValue(
                (Boolean) getInitialValues().get("highlightFocusValue"));
        getConfigurationComposite().setSizeColumnFormula(
                (String) getInitialValues().get("sizeColumnFormula"));
        getConfigurationComposite().setColorColumnFormula(
                (String) getInitialValues().get("colorColumnFormula"));
    }

    @Override
    protected BubbleGraph2DConfigurationPanel createConfigurationComposite(
            Shell shell) {
        return new BubbleGraph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
    }
}
