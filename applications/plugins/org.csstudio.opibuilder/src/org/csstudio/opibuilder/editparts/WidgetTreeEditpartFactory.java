package org.csstudio.opibuilder.editparts;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**The factory creating tree editpart from model.
 * @author Xihui Chen
 *
 */
public class WidgetTreeEditpartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		if(model instanceof AbstractContainerModel)
			return new ContainerTreeEditpart((AbstractContainerModel) model);
		if(model instanceof AbstractWidgetModel)
			return new WidgetTreeEditpart((AbstractWidgetModel) model);
		return null;
	}

}
