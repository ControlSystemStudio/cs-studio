package org.csstudio.opibuilder.widgetActions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * The abstract widget action, which can be executed from the widget by click or context menu. 
 * @author Xihui Chen
 *
 */
public abstract class AbstractWidgetAction implements IAdaptable {
	
	
	private Map<String, AbstractWidgetProperty> propertyMap;
	
	private boolean enabled = true;
	
	
	
	public AbstractWidgetAction() {
		propertyMap = new LinkedHashMap<String, AbstractWidgetProperty>();
		
		configureProperties();
	}
	
	/**Add a property to the widget.
	 * @param property the property to be added.
	 */
	public void addProperty(final AbstractWidgetProperty property){
		Assert.isNotNull(property);
		propertyMap.put(property.getPropertyID(), property);				
	}
	
	protected abstract void configureProperties();
	
	public String getDescription(){
		return getActionType().getDescription();
	}
	
	public abstract void run();
	
	
	public abstract ActionType getActionType();
	
	public AbstractWidgetProperty[] getAllProperties(){
		AbstractWidgetProperty[] propArray = new AbstractWidgetProperty[propertyMap.size()];
		int i=0;
		for(AbstractWidgetProperty p : propertyMap.values())
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
		if(adapter == IWorkbenchAdapter.class)
			return new IWorkbenchAdapter() {
				
				public Object getParent(Object o) {
					return null;
				}
				
				public String getLabel(Object o) {
					return getActionType().getDescription();
				}
				
				public ImageDescriptor getImageDescriptor(Object object) {
					return getActionType().getIconImage();
				}
				
				public Object[] getChildren(Object o) {
					return new Object[0];
				}
			};
		
		return null;
	}

	public AbstractWidgetAction getCopy() {
		AbstractWidgetAction action = WidgetActionFactory.createWidgetAction(getActionType());
		for(String id : propertyMap.keySet()){
			action.setPropertyValue(id, getPropertyValue(id));
		}
		return action;
	}

	public Set<String> getAllPropertyIDs() {
		return propertyMap.keySet();
	}

	public AbstractWidgetProperty getProperty(String propId) {
		return propertyMap.get(propId);
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

}
