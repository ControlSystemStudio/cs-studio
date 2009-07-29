package org.csstudio.opibuilder.editparts;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;

import org.csstudio.opibuilder.util.WidgetsService;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**The central factory to create editpart for all widgets.
 * @author Xihui Chen
 *
 */
public class WidgetEditPartFactory implements EditPartFactory {

	private ExecutionMode executionMode;
	
	public WidgetEditPartFactory(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}
	
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = getPartForModel(model);
		if(part != null){
			part.setModel(model);
			if(part instanceof AbstractBaseEditPart)
				((AbstractBaseEditPart)part).setExecutionMode(executionMode);
		}
		return part;
	}

	private EditPart getPartForModel(Object model){
		if(model instanceof DisplayModel)
			return new DisplayEditpart();
		if(model instanceof AbstractWidgetModel){
			AbstractWidgetEditPart editpart = 
				WidgetsService.getInstance().getWidgetDescriptor(
					((AbstractWidgetModel)model).getTypeID()).getWidgetEditpart();
			return editpart;
		}
		CentralLogger.getInstance().error(this, "Cannot create editpart for model object: "
				+ model);
		return null;
	}
	
}
