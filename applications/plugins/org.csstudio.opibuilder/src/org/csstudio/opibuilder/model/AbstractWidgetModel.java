package org.csstudio.opibuilder.model;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.RGB;
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
	
	private Map<String, IPropertyDescriptor> propertyDescriptors;
	
	private LinkedHashMap<StringProperty, PVValueProperty> pvMap;

	public AbstractWidgetModel() {
		propertyMap = new HashMap<String, AbstractWidgetProperty>();
		propertyDescriptors = new HashMap<String, IPropertyDescriptor>();
		pvMap = new LinkedHashMap<StringProperty, PVValueProperty>();
		configureBaseProperties();
		configureProperties();	
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
		addProperty(new ColorProperty(PROP_COLOR_BACKGROUND, "Background Color",
				WidgetPropertyCategory.Display, true, new RGB(240, 240, 240)));
		addProperty(new ColorProperty(PROP_COLOR_FOREGROUND, "Foreground Color",
				WidgetPropertyCategory.Display, true, new RGB(192, 192, 192)));
		addProperty(new ComboProperty(PROP_BORDER_STYLE,"Border Style", 
				WidgetPropertyCategory.Border, true, BorderStyle.stringValues(), 0));
		addProperty(new ColorProperty(PROP_BORDER_COLOR, "Border Color",
				WidgetPropertyCategory.Border, true, new RGB(0, 128, 255)));
		addProperty(new IntegerProperty(PROP_BORDER_WIDTH, "Border Width", 
				WidgetPropertyCategory.Border, true, 1, 0, 1000));
		addProperty(new BooleanProperty(PROP_ENABLED, "Enabled", 
				WidgetPropertyCategory.Behavior, true, true));
		addProperty(new BooleanProperty(PROP_VISIBLE, "Visible", 
				WidgetPropertyCategory.Behavior, true, true));
		addProperty(new StringProperty(PROP_NAME, "Name",
				WidgetPropertyCategory.Display, true, getTypeID().substring(
						getTypeID().lastIndexOf(".")+1)));
		
		
	}
	
	/**
	 * @return the unique typeID of the model.
	 */
	public abstract String getTypeID();
	
	/**Add a property to the widget.
	 * @param property the property to be added.
	 */
	public void addProperty(final AbstractWidgetProperty property){
		assert property != null;
		propertyMap.put(property.getPropertyID(), property);
		if(property.isVisibleInPropSheet())
			propertyDescriptors.put(property.getPropertyID(), property.getPropertyDescriptor());		
	}
	
	/**Add a PVNameProperty and its value property correspondingly.
	 * @param pvNameProperty
	 * @param pvValueProperty
	 */
	public void addPVProperty(final StringProperty pvNameProperty, 
			final PVValueProperty pvValueProperty){
		addProperty(pvNameProperty);
		addProperty(pvValueProperty);
		pvMap.put(pvNameProperty, pvValueProperty);
	}
	
	/**Remove a PV p
	 * @param pvNamePropId
	 * @param pvValuePropId
	 */
	public void removePVProperty(final String pvNamePropId, final String pvValuePropId){
		removeProperty(pvNamePropId);
		removeProperty(pvValuePropId);
		pvMap.remove(getProperty(pvNamePropId));
	}
	
	
	
	/**Remove a property from the model.
	 * @param prop_id
	 */
	public void removeProperty(final String prop_id){
		assert propertyMap.containsKey(prop_id);
		AbstractWidgetProperty property = propertyMap.get(prop_id);
		property.removeAllPropertyChangeListeners();
		if(property.isVisibleInPropSheet())
			propertyDescriptors.remove(prop_id);
		propertyMap.remove(prop_id);
	}
	
	public void setPropertyVisible(final String prop_id, final boolean visible){
		Assert.isTrue(propertyMap.containsKey(prop_id));
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
		Assert.isTrue(propertyMap.containsKey(id));
		return propertyMap.get(id).getPropertyValue();
	}
	
	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
		Assert.isTrue(propertyMap.containsKey(id));
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
		Assert.isTrue(prop_id != null);
		Assert.isTrue(propertyMap.containsKey(prop_id));
		return propertyMap.get(prop_id);
	}
	
	public LinkedHashMap<StringProperty, PVValueProperty> getPVMap(){
		return pvMap;
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
		return (String)getCastedPropertyValue(PROP_NAME);
	}
	
	public Boolean isEnabled(){
		return (Boolean)getCastedPropertyValue(PROP_ENABLED);
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
	
	public BorderStyle getBorderStyle(){
		Integer i = (Integer)getCastedPropertyValue(PROP_BORDER_STYLE);
		return BorderStyle.values()[i];
	}
	
	
	public RGB getBorderColor(){
		return (RGB)getCastedPropertyValue(PROP_BORDER_COLOR);
	}
	
	public int getBorderWidth(){
		return (Integer)getCastedPropertyValue(PROP_BORDER_WIDTH);
	}
	
	public RGB getBackgroundColor(){
		return (RGB)getCastedPropertyValue(PROP_COLOR_BACKGROUND);
	}
	
	public RGB getForegroundColor(){
		return (RGB)getCastedPropertyValue(PROP_COLOR_FOREGROUND);
	}
	
	public void setSize(int width, int height){
		setPropertyValue(PROP_WIDTH, width);
		setPropertyValue(PROP_HEIGHT, height);
	}
	
	public void setLocation(int x, int y){
		setPropertyValue(PROP_XPOS, x);
		setPropertyValue(PROP_YPOS, y);
	}
	
	public void setForegroundColor(RGB color){
		setPropertyValue(PROP_COLOR_FOREGROUND, color);
	}
	
	public void setBackgroundColor(RGB color){
		setPropertyValue(PROP_COLOR_BACKGROUND, color);
	}
	
	public void setPropertyDescription(String prop_id, String description){
		getProperty(prop_id).setDescription(description);
		if(propertyDescriptors.containsKey(prop_id))
			propertyDescriptors.put(prop_id, getProperty(prop_id).getPropertyDescriptor());
	}
	
	
}
