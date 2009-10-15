package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.GroupingContainerFigure;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;

/**The Editpart Controller for a Grouping Container
 * @author Xihui Chen
 *
 */
public class GroupingContainerEditPart extends AbstractContainerEditpart {

	@Override
	protected IFigure doCreateFigure() {
		Figure f = new GroupingContainerFigure();
		f.setOpaque(!getWidgetModel().isTransparent());
		return f;
	}
	
	@Override
	public GroupingContainerModel getWidgetModel() {
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
				for(AbstractWidgetModel child : getWidgetModel().getChildren()){
					child.setEnabled((Boolean)newValue);
				}
				return true;
			}
		};
		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				lockChildren((Boolean) newValue);					
				return true;
			}
		};
		
		setPropertyChangeHandler(GroupingContainerModel.PROP_LOCK_CHILDREN, handler);
		
		lockChildren(getWidgetModel().isLocked());
		
		removeAllPropertyChangeHandlers(AbstractWidgetModel.PROP_VISIBLE);
		IWidgetPropertyChangeHandler visibilityHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure refreshableFigure) {
				boolean visible = (Boolean) newValue;
				final IFigure figure = getFigure();
				figure.setVisible(visible);				
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_VISIBLE, visibilityHandler);
		
	}
	/**
	* @param lock true if the children should be locked.
	 */
	private void lockChildren(boolean lock) {
		for(Object o: getChildren()){
			if(o instanceof AbstractBaseEditPart){
				((AbstractBaseEditPart)o).setSelectable(!lock);
			}
		}
	}
	
	@Override
	protected EditPart createChild(Object model) {
		EditPart result = super.createChild(model);

		// setup selection behavior for the new child
		if (result instanceof AbstractBaseEditPart) {
			((AbstractBaseEditPart) result).setSelectable(!getWidgetModel().isLocked());
		}

		return result;
	}
	
	
	

}
