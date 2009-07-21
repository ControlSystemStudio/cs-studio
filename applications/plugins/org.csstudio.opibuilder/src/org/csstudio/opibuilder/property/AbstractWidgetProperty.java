package org.csstudio.opibuilder.property;

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
	
	private WidgetPropertyCategory category;
	
	protected boolean visibleInPropSheet;
	
	
	public AbstractWidgetProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean visibleInPropSheet) {
		this.prop_id = prop_id;
		this.description = description;
		this.category = category;
		this.visibleInPropSheet = visibleInPropSheet;		
	}
	
	public synchronized final void addPropertyChangeListener(PropertyChangeListener listener){
		if(listener == null){
			return;
		}
		pcsDelegate.addPropertyChangeListener(listener);
	}
	
	/**Check if the requestNewValue is convertible or legal.
	 * @param requestNewValue
	 * @return The value after being checked. It might be coerced if the requestValue 
	 * is illegal. return null if it is not convertible or illegal.
	 */
	public abstract Object checkValue(Object requestNewValue);
	
	protected final void firePropertyChange(Object oldValue, Object newValue){
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

	public final String getPropertyName() {
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
		for(PropertyChangeListener l : pcsDelegate.getPropertyChangeListeners())
			pcsDelegate.removePropertyChangeListener(prop_id, l);
	}

	public final void setCategory(WidgetPropertyCategory category) {
		this.category = category;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final void setPCSDelegate(PropertyChangeSupport pcsDelegate){
		this.pcsDelegate = pcsDelegate;
	}

	public final void setPropertyDescriptor(IPropertyDescriptor propertyDescriptor) {
		this.propertyDescriptor = propertyDescriptor;
	}

	public final void setPropertyValue(Object propertyValue) {
		//do conversion and legally check
		Object newValue = checkValue(propertyValue);
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
		this.visibleInPropSheet = visibleInPropSheet;
		return true;
	}
	
	
}
