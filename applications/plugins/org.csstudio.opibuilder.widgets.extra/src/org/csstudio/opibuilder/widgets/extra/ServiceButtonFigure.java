/**
 * 
 */
package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.csstudio.utility.pvmanager.widgets.ServiceButton;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 * 
 */
public class ServiceButtonFigure extends
	AbstractSWTWidgetFigure<ServiceButton> {

    public ServiceButtonFigure(AbstractBaseEditPart editpart) {
	super(editpart);
    }

    @Override
    protected ServiceButton createSWTWidget(Composite parent, int style) {
	return new ServiceButton(parent);
    }

    public boolean isRunMode() {
	return runmode;
    }

}
