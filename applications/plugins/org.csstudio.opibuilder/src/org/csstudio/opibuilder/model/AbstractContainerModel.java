package org.csstudio.opibuilder.model;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

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
			protected PropertyDescriptor createPropertyDescriptor() {
				return null;
			}

			@Override
			public void writeToXML(Element propElement) {				
			}

			@Override
			public Object readValueFromXML(Element propElement) {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}
	
	/**add child to the end of the children list.
	 * @param child the widget to be added
	 */
	public void addChild(AbstractWidgetModel child){
		Assert.isNotNull(child);
		childrenList.add(child);
		childrenProperty.firePropertyChange(null, childrenList);
	}
	
	public void addChild(int index, AbstractWidgetModel child){
		Assert.isNotNull(child);
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

	public AbstractWidgetProperty getChildrenProperty() {
		return childrenProperty;
	}
	
	/**Change the order of the child.
	 * @param child
	 * @param newIndex
	 */
	public final void changeChildOrder(final AbstractWidgetModel child, final int newIndex){
		if(childrenList.contains(child) && newIndex >= 0 && newIndex < childrenList.size()){
			if(newIndex == childrenList.indexOf(child))
				return;
			childrenList.remove(child);
			childrenList.add(newIndex, child);
			childrenProperty.firePropertyChange(null, childrenList);
		}
	}
	
	
	
}
