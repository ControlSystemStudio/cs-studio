package org.csstudio.opibuilder.model;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.property.AbstractWidgetProperty;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public abstract class AbstractWidgetModel implements IAdaptable,
		IPropertySource {

	private Map<String, AbstractWidgetProperty> propertyMap;
	
	private PropertyChangeSupport pcsDelegate;
	
	private Map<String, IPropertyDescriptor> propertyDescriptors;

	public AbstractWidgetModel() {
		propertyMap = new HashMap<String, AbstractWidgetProperty>();
		pcsDelegate = new PropertyChangeSupport(this);
		propertyDescriptors = new HashMap<String, IPropertyDescriptor>();
		configureBaseProperties();
		configureProperties();		
	}
	
	private void configureBaseProperties() {
		
	}
	
	/**
	 * @return the unique typeID of the model.
	 */
	public abstract String getTypeID();
	
	public void addProperty(final String prop_id, final AbstractWidgetProperty property){
		property.setPCSDelegate(pcsDelegate);
		propertyMap.put(prop_id, property);
		if(property.isVisibleInPropSheet())
			propertyDescriptors.put(prop_id, property.getPropertyDescriptor());		
	}
	
	public void setPropertyVisible(final String prop_id, final boolean visible){
		assert propertyMap.containsKey(prop_id);
		AbstractWidgetProperty property = propertyMap.get(prop_id);
		if(property.setVisibleInPropSheet(visible)){
			if(visible)
				propertyDescriptors.put(prop_id, property.getPropertyDescriptor());
			else
				propertyDescriptors.remove(prop_id);
		}			
	}
	
	protected abstract void configureProperties();
	
	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return (IPropertyDescriptor[]) propertyDescriptors.values().toArray();
	}

	public Object getPropertyValue(Object id) {
		return propertyMap.get(id).getPropertyValueInString();
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
		propertyMap.get(id).setPropertyValue(value);
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
}
