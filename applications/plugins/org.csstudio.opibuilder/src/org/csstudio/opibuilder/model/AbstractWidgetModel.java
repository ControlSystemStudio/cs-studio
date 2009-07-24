package org.csstudio.opibuilder.model;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**The abstract base model for display and all widgets .
 * @author Xihui Chen
 *
 */
public abstract class AbstractWidgetModel implements IAdaptable,
		IPropertySource {
	
	public static final String PROP_NAME = "name";//$NON-NLS-1$
	
	public static final String PROP_SCRIPT = "script";//$NON-NLS-1$
	
	public static final String PROP_XPOS = "x";//$NON-NLS-1$
	
	public static final String PROP_YPOS = "y";//$NON-NLS-1$
	
	public static final String PROP_WIDTH = "width";//$NON-NLS-1$
	
	public static final String PROP_HEIGHT = "height";//$NON-NLS-1$
	
	public static final String PROP_COLOR_BACKGROUND = "color.background";//$NON-NLS-1$
	
	public static final String PROP_COLOR_FOREGROUND = "color.foreground";//$NON-NLS-1$
	
	public static final String PROP_VISIBLE = "visible";//$NON-NLS-1$
	
	public static final String PROP_ENABLED = "enabled";//$NON-NLS-1$
	
	public static final String PROP_ACTIONDATA = "actionData";//$NON-NLS-1$
	
	public static final String PROP_TOOLTIP = "tooltip"; //$NON-NLS-1$
	
	public static final String PROP_BORDER_COLOR = "border.color"; //$NON-NLS-1$

	public static final String PROP_BORDER_WIDTH = "border.width"; //$NON-NLS-1$

	public static final String PROP_BORDER_STYLE = "border.style"; //$NON-NLS-1$
	

	private Map<String, AbstractWidgetProperty> propertyMap;
	
	protected PropertyChangeSupport pcsDelegate;
	
	private Map<String, IPropertyDescriptor> propertyDescriptors;

	public AbstractWidgetModel() {
		propertyMap = new HashMap<String, AbstractWidgetProperty>();
		pcsDelegate = new PropertyChangeSupport(this);
		propertyDescriptors = new HashMap<String, IPropertyDescriptor>();
		configureBaseProperties();
		configureProperties();	
		setSize(100, 100);
	}
	
	protected void configureBaseProperties() {
		addProperty(new IntegerProperty(PROP_WIDTH, "Width", 
				WidgetPropertyCategory.Position, true, 100, 1, 10000));
		addProperty(new IntegerProperty(PROP_HEIGHT, "Height", 
				WidgetPropertyCategory.Position, true, 100, 1, 10000));		
		addProperty(new IntegerProperty(PROP_XPOS, "X", 
				WidgetPropertyCategory.Position, true, 100));
		addProperty(new IntegerProperty(PROP_YPOS, "Y", 
				WidgetPropertyCategory.Position, true, 100));	
	}
	
	/**
	 * @return the unique typeID of the model.
	 */
	public abstract String getTypeID();
	
	public void addProperty(final AbstractWidgetProperty property){
		assert property != null;
		property.setPCSDelegate(pcsDelegate);
		propertyMap.put(property.getPropertyID(), property);
		if(property.isVisibleInPropSheet())
			propertyDescriptors.put(property.getPropertyID(), property.getPropertyDescriptor());		
	}
	
	/**Remove a property from the model.
	 * @param prop_id
	 */
	public void removeProperty(final String prop_id){
		assert propertyMap.containsKey(prop_id);
		AbstractWidgetProperty property = propertyMap.get(prop_id);
		if(property.isVisibleInPropSheet())
			propertyDescriptors.remove(prop_id);
		propertyMap.remove(prop_id);
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
		IPropertyDescriptor[] propArray = new IPropertyDescriptor[propertyDescriptors.size()];
		int i=0;
		for(IPropertyDescriptor p : propertyDescriptors.values())
			propArray[i++] = p;		
			
		return propArray;
	}

	public Object getPropertyValue(Object id) {
		assert propertyMap.containsKey(id);
		return propertyMap.get(id).getPropertyValueInString();
	}
	
	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
		assert propertyMap.containsKey(id);
		propertyMap.get(id).setPropertyValue(value);
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**This should be override by container model.
	 * @return the children of the widget.
	 */
	public List<AbstractWidgetModel> getChildren(){
		return null;
	}
	
	public Set<String> getAllPropertyIDs(){
		return new HashSet<String>(propertyMap.keySet());
	}
	
	public AbstractWidgetProperty getProperty(String prop_id){
		assert prop_id != null;
		assert propertyMap.containsKey(prop_id);
		return propertyMap.get(prop_id);
	}
	/**
	 * Return the casted value of a property of this widget model.
	 * 
	 * @param <TYPE>
	 *            The return type of the property value.
	 * @param propertyName
	 *            The ID of the property.
	 * @return The casted value of a property of this widget model.
	 */
	@SuppressWarnings("unchecked")
	protected <TYPE> TYPE getCastedPropertyValue(final String propertyName) {
		return (TYPE) getProperty(propertyName).getPropertyValue();
	}
	
	public String getName(){
		return getCastedPropertyValue(PROP_NAME);
	}
	
	public Dimension getSize(){
		return new Dimension(
				((Integer)getCastedPropertyValue(PROP_WIDTH)).intValue(),
				((Integer)getCastedPropertyValue(PROP_HEIGHT)).intValue());
	}
	
	public Point getLocation(){
		return new Point(
				((Integer)getCastedPropertyValue(PROP_XPOS)).intValue(),
				((Integer)getCastedPropertyValue(PROP_YPOS)).intValue());
	}
	
	
	public void setSize(int width, int height){
		setPropertyValue(PROP_WIDTH, width);
		setPropertyValue(PROP_HEIGHT, height);
	}
	
	public void setLocation(int x, int y){
		setPropertyValue(PROP_XPOS, x);
		setPropertyValue(PROP_YPOS, y);
	}
	

	
	
}
