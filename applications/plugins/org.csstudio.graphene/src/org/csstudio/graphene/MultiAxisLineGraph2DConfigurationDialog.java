/**
 *
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.epics.graphene.InterpolationScheme;

/**
 * @author shroffk
 *
 */
public class MultiAxisLineGraph2DConfigurationDialog
        extends AbstractPointDatasetGraph2DConfigurationDialog<MultiAxisLineGraph2DWidget, MultiAxisLineGraph2DConfigurationPanel> {

    protected MultiAxisLineGraph2DConfigurationDialog(MultiAxisLineGraph2DWidget control,
            String title) {
        super(control, title);
        addInitialValues("interpolation", getWidget().getInterpolation());
        addInitialValues("separateAreas", getWidget().isSeparateAreas());
    }

    @Override
    protected void onPropertyChange(PropertyChangeEvent evt) {
        super.onPropertyChange(evt);
        getWidget().setInterpolation(
                getConfigurationComposite().getInterpolation());
        getWidget().setSeparateAreas(
                getConfigurationComposite().isSeparateAreas());
    }

    @Override
    protected void populateInitialValues() {
        super.populateInitialValues();
        getConfigurationComposite().setInterpolation(
                (InterpolationScheme) getInitialValues().get("interpolation"));
        getConfigurationComposite().setSeparateAreas(
                (Boolean) getInitialValues().get("separateAreas"));
    }

    @Override
    protected MultiAxisLineGraph2DConfigurationPanel createConfigurationComposite(
            Shell shell) {
        return new MultiAxisLineGraph2DConfigurationPanel(shell, SWT.DIALOG_TRIM);
    }
}
