package org.csstudio.opibuilder.model;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public abstract class AbstractContainerModel extends AbstractWidgetModel {
	
	public static final String PROP_CHILDREN = "children";
	
	private AbstractWidgetProperty childrenProperty;
	
	private List<AbstractWidgetModel> childrenList;
	
	public AbstractContainerModel() {
		super();
		childrenList = new LinkedList<AbstractWidgetModel>();
		childrenProperty = new AbstractWidgetProperty(
				PROP_CHILDREN, "children", WidgetPropertyCategory.Behavior, false, childrenList){

			@Override
			public Object checkValue(Object value) {
				if(value instanceof List)
					return value;
				return null;
			}

			@Override
			protected IPropertyDescriptor createPropertyDescriptor() {
				return null;
			}

			@Override
			public String getPropertyValueInString() {
				return "";
			}
			
		};
	}
	
	/**add child to the end of the children list.
	 * @param child the widget to be added
	 */
	public void addChild(AbstractWidgetModel child){
		assert child != null : "child is null";
		childrenList.add(child);
		childrenProperty.firePropertyChange(null, childrenList);
	}
	
	public void addChild(int index, AbstractWidgetModel child){
		assert child != null : "child is null";
		childrenList.add(index, child);
		childrenProperty.firePropertyChange(null, childrenList);
	}
	
	public void removeChild(AbstractWidgetModel child){
		if(child != null && childrenList.remove(child)) {
			childrenProperty.firePropertyChange(null, childrenList);
		}
	}
	
	@Override
	protected void configureBaseProperties() {
		super.configureBaseProperties();		
	}

	@Override
	public List<AbstractWidgetModel> getChildren() {
		return childrenList;
	}
	
	/**
	 * @param widget
	 * @return the index of the widget in the children list, which is also 
	 * the order of the widget in the display.
	 */
	public final int getIndexOf(final AbstractWidgetModel widget){
		return childrenList.indexOf(widget);
	}
	
	
}
