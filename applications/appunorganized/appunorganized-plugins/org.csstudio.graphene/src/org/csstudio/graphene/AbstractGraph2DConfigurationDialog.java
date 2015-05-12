/**
 *
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;

import org.csstudio.utility.pvmanager.widgets.AbstractConfigurationDialog;
import org.eclipse.swt.SWT;

/**
 * @author shroffk
 *
 */
public abstract class AbstractGraph2DConfigurationDialog<T extends AbstractGraph2DWidget<?, ?>, S extends AbstractGraph2DConfigurationPanel>
        extends
        AbstractConfigurationDialog<T, S> {

    protected AbstractGraph2DConfigurationDialog(T control, String title) {
        super(control, SWT.DIALOG_TRIM, title);
        addInitialValues("dataFormula", getWidget().getDataFormula());
    }

    @Override
    protected void onPropertyChange(PropertyChangeEvent evt) {
        getWidget()
                .setDataFormula(getConfigurationComposite().getDataFormula());
    }

    @Override
    protected void populateInitialValues() {
        getConfigurationComposite().setDataFormula(
                (String) getInitialValues().get("dataFormula"));
    }

}
