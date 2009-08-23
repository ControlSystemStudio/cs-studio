package org.csstudio.opibuilder.widgetActions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * The abstract widget action, which can be executed from the widget by click or context menu. 
 * @author Xihui Chen
 *
 */
public abstract class AbstractWidgetAction implements IAdaptable {
	
	
	private Map<String, AbstractWidgetProperty> propertyMap;
	
	private Map<String, IPropertyDescriptor> propertyDescriptors;
	
	
	public AbstractWidgetAction() {
		propertyMap = new LinkedHashMap<String, AbstractWidgetProperty>();
	}
	
	/**Add a property to the widget.
	 * @param property the property to be added.
	 */
	public void addProperty(final AbstractWidgetProperty property){
		Assert.isNotNull(property);
		propertyMap.put(property.getPropertyID(), property);
		if(property.isVisibleInPropSheet())
			propertyDescriptors.put(property.getPropertyID(), property.getPropertyDescriptor());		
	}
	
	protected abstract void configureProperties();
	
	public abstract void run();
	
	
	public abstract ActionType getActionType();
	
	public AbstractWidgetProperty[] getAllProperties(){
		AbstractWidgetProperty[] propArray = new AbstractWidgetProperty[propertyMap.size()];
		int i=0;
		for(AbstractWidgetProperty p : propertyMap.values())
			propArray[i++] = p;
		return propArray;
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] propArray = new IPropertyDescriptor[propertyDescriptors.size()];
		int i=0;
		for(IPropertyDescriptor p : propertyDescriptors.values())
			propArray[i++] = p;		
			
		return propArray;
	}
	public Object getPropertyValue(Object id) {
		Assert.isTrue(propertyMap.containsKey(id));
		return propertyMap.get(id).getPropertyValue();
	}
	
	public void setPropertyValue(Object id, Object value) {
		Assert.isTrue(propertyMap.containsKey(id));
		propertyMap.get(id).setPropertyValue(value);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public AbstractWidgetAction getCopy() {
		AbstractWidgetAction action = WidgetActionFactory.createWidgetAction(getActionType());
		for(String id : propertyMap.keySet()){
			action.setPropertyValue(id, getPropertyValue(id));
		}
		return action;
	}

}
