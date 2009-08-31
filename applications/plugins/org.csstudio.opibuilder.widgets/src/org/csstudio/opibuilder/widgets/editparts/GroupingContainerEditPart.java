package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.GroupingContainerFigure;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

/**The Editpart Controller for a Grouping Container
 * @author Xihui Chen
 *
 */
public class GroupingContainerEditPart extends AbstractContainerEditpart {

	@Override
	protected IFigure doCreateFigure() {
		Figure f = new GroupingContainerFigure();
		f.setOpaque(!getCastedModel().isTransparent());
		
		
		return f;
	}
	
	@Override
	public GroupingContainerModel getCastedModel() {
		return (GroupingContainerModel)getModel();
	}

	
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ContainerHighlightEditPolicy());
		
	}
	
	@Override
	public IFigure getContentPane() {
		return ((GroupingContainerFigure)getFigure()).getContentPane();
	}
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setOpaque(!(Boolean)newValue);			
				return true;
			}
		};
		
		setPropertyChangeHandler(GroupingContainerModel.PROP_TRANSPARENT, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				for(AbstractWidgetModel child : getCastedModel().getChildren()){
					child.setEnabled((Boolean)newValue);
				}
				return true;
			}
		};
		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED, handler);
		
		
	}
	

}
