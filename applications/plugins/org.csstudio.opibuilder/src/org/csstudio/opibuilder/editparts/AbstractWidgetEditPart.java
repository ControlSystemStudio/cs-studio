package org.csstudio.opibuilder.editparts;

import org.csstudio.opibuilder.commands.WidgetDeleteCommand;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**The abstract editpart for all widgets.
 * @author Xihui Chen
 *
 */
public abstract class AbstractWidgetEditPart extends AbstractBaseEditPart {	

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy(){
			@Override
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				Object containerModel = getHost().getParent().getModel();
				Object widget = (AbstractWidgetModel)getHost().getModel();
				
				if(containerModel instanceof AbstractContainerModel && 
						widget instanceof AbstractWidgetModel)
					return new WidgetDeleteCommand((AbstractContainerModel)containerModel,
							(AbstractWidgetModel)widget);				
				return super.createDeleteCommand(deleteRequest);
			}
	
		});	
	}
	

}
