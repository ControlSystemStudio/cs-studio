package org.csstudio.opibuilder.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.MacrosProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.MacrosInput;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**The model which could contain children.
 * @author Xihui Chen
 *
 */
public abstract class AbstractContainerModel extends AbstractWidgetModel {
	
	public static final String PROP_CHILDREN = "children";
	
	public static final String PROP_SELECTION = "selection";

	public static final String PROP_MACROS = "macros";
	
	private AbstractWidgetProperty childrenProperty;
	
	private AbstractWidgetProperty selectionProperty;
	
	private List<AbstractWidgetModel> childrenList;
	
	private Map<String, String> macroMap;
	
	
	public AbstractContainerModel() {
		super();
		childrenList = new LinkedList<AbstractWidgetModel>();
		macroMap = new HashMap<String, String>();
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
	 * @param changeParent true if the widget's parent should be changed.
	 */
	public synchronized void addChild(AbstractWidgetModel child, boolean changeParent){
		if(child != null && !childrenList.contains(child)){
			childrenList.add(child);
			if(changeParent)
				child.setParent(this);
			childrenProperty.firePropertyChange(-1, child);
		}
		
	}
	
	public void addChild(AbstractWidgetModel child){
		addChild(child, true);
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
		addProperty(new MacrosProperty(
				PROP_MACROS, "Macros", WidgetPropertyCategory.Basic, 
				new MacrosInput(new HashMap<String, String>(), true)));
	}

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
	
	
	public void setMacroMap(Map<String, String> macroMap) {
		this.macroMap = macroMap;
	}
	
	public Map<String, String> getMacroMap() {
		return macroMap;
	}	

	public MacrosInput getMacrosInput(){
		return (MacrosInput)getCastedPropertyValue(PROP_MACROS);
	}
	

	/**
	 * @return the macros of its parent.
	 */
	public Map<String, String> getParentMacroMap(){
		if(getParent() != null)
			return getParent().getMacroMap();
		else
			return PreferencesHelper.getMacros();
	}
}
