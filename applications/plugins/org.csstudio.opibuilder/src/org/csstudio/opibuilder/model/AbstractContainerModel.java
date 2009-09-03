package org.csstudio.opibuilder.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**The model which could contain children.
 * @author Xihui Chen
 *
 */
public abstract class AbstractContainerModel extends AbstractWidgetModel {
	
	public static final String PROP_CHILDREN = "children";
	
	public static final String PROP_SELECTION = "selection";
	
	private AbstractWidgetProperty childrenProperty;
	
	private AbstractWidgetProperty selectionProperty;
	
	private List<AbstractWidgetModel> childrenList;
	
	
	public AbstractContainerModel() {
		super();
		childrenList = new LinkedList<AbstractWidgetModel>();
		childrenProperty = new AbstractWidgetProperty(
				PROP_CHILDREN, "children", WidgetPropertyCategory.Behavior, childrenList){

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
			public void writeToXML(Element propElement) {}

			@Override
			public Object readValueFromXML(Element propElement) {
				return null;
			}
			
		};
		
		selectionProperty = new AbstractWidgetProperty(
				PROP_SELECTION, "selection", WidgetPropertyCategory.Behavior, null){

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
			public void writeToXML(Element propElement) {}

			@Override
			public Object readValueFromXML(Element propElement) {
				return null;
			}
			
		};
	}
	
	/**add child to the end of the children list.
	 * @param child the widget to be added
	 */
	public synchronized void addChild(AbstractWidgetModel child){
		if(child != null && !childrenList.contains(child)){
			childrenList.add(child);
			child.setParent(this);
			childrenProperty.firePropertyChange(-1, child);
		}
		
	}
	
	public synchronized void addChild(int index, AbstractWidgetModel child){
		if(child != null && !childrenList.contains(child)){
			childrenList.add(index, child);
			child.setParent(this);
			childrenProperty.firePropertyChange(index, child);
		}
	}
	
	public synchronized void removeChild(AbstractWidgetModel child){
		if(child != null && childrenList.remove(child)) {
			child.setParent(null);
			childrenProperty.firePropertyChange(child, null);
		}
	}
	
	public synchronized void removeAllChildren(){
		childrenList.clear();
		childrenProperty.firePropertyChange(childrenList, null);
	}
	
	@Override
	protected void configureBaseProperties() {
		super.configureBaseProperties();		
	}

	@Override
	public List<AbstractWidgetModel> getChildren() {
		return childrenList;
	}
	
	public AbstractWidgetModel getChildByName(String name){
		for(AbstractWidgetModel child : getChildren()){
			if(child.getName().equals(name))
				return child;
		}
		return null;
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
			removeChild(child);
			addChild(newIndex, child);
			childrenProperty.firePropertyChange(null, childrenList);
		}
	}
	
	public AbstractWidgetProperty getSelectionProperty() {
		return selectionProperty;
	}
	
	public void selectWidgets(List<AbstractWidgetModel> widgets, boolean append){
		selectionProperty.firePropertyChange(append, widgets);
	}

	public void selectWidget(AbstractWidgetModel newWidget, boolean append) {
		selectWidgets(Arrays.asList(newWidget), append);
	}
	
	
	
	
}
