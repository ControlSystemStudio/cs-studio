package org.csstudio.opibuilder.editparts;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.properties.WidgetPropertyChangeListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public abstract class AbstractBaseEditpart extends AbstractGraphicalEditPart{

	protected Map<String, WidgetPropertyChangeListener> propertyListenerMap;
	
	private ExecutionMode executionMode;
	
	public AbstractBaseEditpart() {
		propertyListenerMap = new HashMap<String, WidgetPropertyChangeListener>();	
	}
	
	@Override
	public void activate() {
		if(!isActive()){
			super.activate();
			//add listener to all properties.
			for(String id : getCastedModel().getAllPropertyIDs()){
				WidgetPropertyChangeListener listener = 
					new WidgetPropertyChangeListener(this);
				getCastedModel().getProperty(id).addPropertyChangeListener(
					listener);
				propertyListenerMap.put(id, listener);
			}
			registerBasePropertyChangeHandlers();
			registerPropertyChangeHandlers();
		}		
	}
	
	@Override
	public void deactivate() {
		if(isActive()){
			super.deactivate();
			//remove listener from all properties.
			for(String id : getCastedModel().getAllPropertyIDs()){
				getCastedModel().getProperty(id).removeAllPropertyChangeListeners();
				propertyListenerMap.clear();
			}
		}
		
	}

	/**
	 * Registers a property change handler for the specified property id.
	 * 
	 * @param propertyId
	 *            the property id
	 * @param handler
	 *            the property change handler
	 */
	protected final void setPropertyChangeHandler(final String propertyId, final IWidgetPropertyChangeHandler handler) {
		WidgetPropertyChangeListener listener = propertyListenerMap.get(propertyId);
		if (listener != null) {
			listener.addHandler(handler);
		}
	}
	
	private void registerBasePropertyChangeHandlers(){
		IWidgetPropertyChangeHandler refreshVisualHandler = new IWidgetPropertyChangeHandler(){

				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					refreshVisuals();
					return false;
				}			
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_XPOS, refreshVisualHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_YPOS, refreshVisualHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDTH, refreshVisualHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_HEIGHT, refreshVisualHandler);
		
	}
	
	protected abstract void registerPropertyChangeHandlers();
	
	
	
	public AbstractWidgetModel getCastedModel(){
		return (AbstractWidgetModel)getModel();
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void refreshVisuals() {
		doRefreshVisuals(getFigure());
	}

	/**
	 * Resizes the figure. Use {@link AbstractBaseEditPart} to implement more
	 * complex refreshing behavior.
	 * 
	 * @param refreshableFigure
	 *            the figure
	 */
	protected synchronized void doRefreshVisuals(final IFigure refreshableFigure) {
		super.refreshVisuals();
		AbstractWidgetModel model = getCastedModel();
		GraphicalEditPart parent = (GraphicalEditPart) getParent();
		if(parent != null){
			parent.setLayoutConstraint(this, refreshableFigure, new Rectangle(
					model.getLocation(), model.getSize()));
		}		
	}

	/**
	 * @param executionMode the executionMode to set
	 */
	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}

	/**
	 * @return the executionMode
	 */
	public ExecutionMode getExecutionMode() {
		return executionMode;
	}
}
