package org.csstudio.opibuilder.properties;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**The base widget property class for all kinds of widget property.
 * @author Xihui Chen
 *
 */
public abstract class AbstractWidgetProperty {
	
	protected String prop_id;
	
	protected String description; 
	
	private PropertyChangeSupport pcsDelegate;
	
	private IPropertyDescriptor propertyDescriptor;
	
	protected Object propertyValue;
	
	protected Object defaultValue;
	
	protected WidgetPropertyCategory category;
	
	protected boolean visibleInPropSheet;
	
	
	public AbstractWidgetProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean visibleInPropSheet,
			Object defaultValue) {
		this.prop_id = prop_id;
		this.description = description;
		this.category = category;
		this.visibleInPropSheet = visibleInPropSheet;
		this.defaultValue = defaultValue;
		this.propertyValue = defaultValue;
		createPropertyDescriptor(visibleInPropSheet);
	}
	
	public synchronized final void addPropertyChangeListener(PropertyChangeListener listener){
		if(listener == null){
			return;
		}
		pcsDelegate.addPropertyChangeListener(listener);
	}
	
	/**Check if the requestNewValue is convertible or legal.
	 * @param value the value to be checked.
	 * @return The value after being checked. It might be coerced if the requestValue 
	 * is illegal. return null if it is not convertible or illegal.
	 */
	public abstract Object checkValue(final Object value);
	
	public final void firePropertyChange(final Object oldValue, final Object newValue){
		if(pcsDelegate.hasListeners(prop_id))
			pcsDelegate.firePropertyChange(prop_id, oldValue, newValue);
	}
	
	public final WidgetPropertyCategory getCategory() {
		return category;
	}

	public final Object getDefaultValue() {
		return defaultValue;
	}
	
	public final String getDescription() {
		return description;
	}

	public final IPropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public final String getPropertyID() {
		return prop_id;
	}

	public Object getPropertyValue() {
		return propertyValue;
	}

	public abstract String getPropertyValueInString();
	
	public final boolean isVisibleInPropSheet() {
		return visibleInPropSheet;
	}

	public final void removeAllPropertyChangeListeners(){
		for(PropertyChangeListener l : pcsDelegate.getPropertyChangeListeners(prop_id))
			pcsDelegate.removePropertyChangeListener(l);
	}

	public final void setCategory(WidgetPropertyCategory category) {
		this.category = category;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public final void setPCSDelegate(PropertyChangeSupport pcsDelegate){
		this.pcsDelegate = pcsDelegate;
	}

	public final void setPropertyValue(Object value) {
		//do conversion and legally check
		Object newValue = checkValue(value);
		if(newValue == null || newValue.equals(propertyValue))
			return;
		firePropertyChange(propertyValue, newValue);
		propertyValue = newValue;		
	}
	
	/**
	 * @param visibleInPropSheet
	 * @return true if visibility changed.
	 */
	public final boolean setVisibleInPropSheet(boolean visibleInPropSheet) {
		if(visibleInPropSheet == this.visibleInPropSheet)
			return false;
		createPropertyDescriptor(visibleInPropSheet);
		this.visibleInPropSheet = visibleInPropSheet;
		return true;
	}
	
	private void createPropertyDescriptor(final boolean visibleInPropSheet){
		if(visibleInPropSheet)
			propertyDescriptor = createPropertyDescriptor();
		else
			propertyDescriptor = null;
	}
	
	/**
	 * Create the {@link IPropertyDescriptor}
	 */
	protected abstract IPropertyDescriptor createPropertyDescriptor();
	
}
