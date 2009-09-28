package org.csstudio.opibuilder.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.ActionsProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.ScriptProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.UnchangableStringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
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
	
	public static final String VERSION = "1.0";
	
	public static final String PROP_NAME = "name";//$NON-NLS-1$
	
	public static final String PROP_SCRIPTS = "scripts";//$NON-NLS-1$
	
	public static final String PROP_XPOS = "x";//$NON-NLS-1$
	
	public static final String PROP_YPOS = "y";//$NON-NLS-1$
	
	public static final String PROP_WIDTH = "width";//$NON-NLS-1$
	
	public static final String PROP_HEIGHT = "height";//$NON-NLS-1$
	
	public static final String PROP_COLOR_BACKGROUND = "background_color";//$NON-NLS-1$
	
	public static final String PROP_COLOR_FOREGROUND = "foreground_color";//$NON-NLS-1$
	
	public static final String PROP_VISIBLE = "visible";//$NON-NLS-1$
	
	public static final String PROP_ENABLED = "enabled";//$NON-NLS-1$
	
	public static final String PROP_ACTIONS = "actions";//$NON-NLS-1$
	
	public static final String PROP_TOOLTIP = "tooltip"; //$NON-NLS-1$
	
	public static final String PROP_BORDER_COLOR = "border_color"; //$NON-NLS-1$

	public static final String PROP_BORDER_WIDTH = "border_width"; //$NON-NLS-1$

	public static final String PROP_BORDER_STYLE = "border_style"; //$NON-NLS-1$
	
	public static final String PROP_WIDGET_TYPE= "widget_type"; //$NON-NLS-1$
	
	private Map<String, AbstractWidgetProperty> propertyMap;
	
	private Map<String, IPropertyDescriptor> propertyDescriptors;
	
	private AbstractContainerModel parent;
	
	private LinkedHashMap<StringProperty, PVValueProperty> pvMap;
	

	public AbstractWidgetModel() {
		propertyMap = new HashMap<String, AbstractWidgetProperty>();
		propertyDescriptors = new HashMap<String, IPropertyDescriptor>();
		pvMap = new LinkedHashMap<StringProperty, PVValueProperty>();
		configureBaseProperties();
		configureProperties();	
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
	
	protected void configureBaseProperties() {
		addProperty(new IntegerProperty(PROP_WIDTH, "Width", 
				WidgetPropertyCategory.Position, 100, 1, 10000));
		addProperty(new IntegerProperty(PROP_HEIGHT, "Height", 
				WidgetPropertyCategory.Position, 100, 1, 10000));		
		addProperty(new IntegerProperty(PROP_XPOS, "X", 
				WidgetPropertyCategory.Position, 100));
		addProperty(new IntegerProperty(PROP_YPOS, "Y", 
				WidgetPropertyCategory.Position, 100));			
		addProperty(new ColorProperty(PROP_COLOR_BACKGROUND, "Background Color",
				WidgetPropertyCategory.Display, new RGB(240, 240, 240)));
		addProperty(new ColorProperty(PROP_COLOR_FOREGROUND, "Foreground Color",
				WidgetPropertyCategory.Display, new RGB(192, 192, 192)));
		addProperty(new ColorProperty(PROP_BORDER_COLOR, "Border Color",
				WidgetPropertyCategory.Border, new RGB(0, 128, 255)));
		addProperty(new ComboProperty(PROP_BORDER_STYLE,"Border Style", 
				WidgetPropertyCategory.Border, BorderStyle.stringValues(), 0));
		addProperty(new IntegerProperty(PROP_BORDER_WIDTH, "Border Width", 
				WidgetPropertyCategory.Border, 1, 0, 1000));
		addProperty(new BooleanProperty(PROP_ENABLED, "Enabled", 
				WidgetPropertyCategory.Behavior, true));
		addProperty(new BooleanProperty(PROP_VISIBLE, "Visible", 
				WidgetPropertyCategory.Behavior, true));
		addProperty(new ScriptProperty(PROP_SCRIPTS, "Scripts", 
				WidgetPropertyCategory.Behavior));
		addProperty(new ActionsProperty(PROP_ACTIONS, "Actions", 
				WidgetPropertyCategory.Behavior));
		addProperty(new StringProperty(PROP_TOOLTIP, "Tooltip", WidgetPropertyCategory.Display, "", true));
			
			
		WidgetDescriptor descriptor = WidgetsService.getInstance().getWidgetDescriptor(getTypeID());
		String name;
		name = descriptor == null? getTypeID().substring(getTypeID().lastIndexOf(".")+1) :
			descriptor.getName();
		addProperty(new StringProperty(PROP_NAME, "Name",
				WidgetPropertyCategory.Basic, name)); 	
		addProperty(new UnchangableStringProperty(PROP_WIDGET_TYPE, "Widget Type",
				WidgetPropertyCategory.Basic, name));
		
	}
	
	protected abstract void configureProperties();
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	
	
	public Set<String> getAllPropertyIDs(){
		return new HashSet<String>(propertyMap.keySet());
	}
	
	public RGB getBackgroundColor(){
		return getRGBFromColorProperty(PROP_COLOR_BACKGROUND);
	}
	
	public RGB getBorderColor(){
		return getRGBFromColorProperty(PROP_BORDER_COLOR);
	}
	
	public BorderStyle getBorderStyle(){
		Integer i = (Integer)getCastedPropertyValue(PROP_BORDER_STYLE);
		return BorderStyle.values()[i];
	}

	public int getBorderWidth(){
		return (Integer)getCastedPropertyValue(PROP_BORDER_WIDTH);
	}
	
	public RGB getRGBFromColorProperty(String propID){
		return ((OPIColor)getCastedPropertyValue(propID)).getRGBValue();
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
	
	

	public Object getEditableValue() {
		return this;
	}

	
	
	
	public ActionsInput getActionsInput(){
		return (ActionsInput)getCastedPropertyValue(PROP_ACTIONS);
	}
	
	public RGB getForegroundColor(){
		return getRGBFromColorProperty(PROP_COLOR_FOREGROUND);
	}
	
	public Point getLocation(){
		return new Point(
				((Integer)getCastedPropertyValue(PROP_XPOS)).intValue(),
				((Integer)getCastedPropertyValue(PROP_YPOS)).intValue());
	}
	
	public String getName(){
		return (String)getCastedPropertyValue(PROP_NAME);
	}
	
	public AbstractWidgetProperty getProperty(String prop_id){
		if((prop_id != null && propertyMap.containsKey(prop_id)))
			return propertyMap.get(prop_id);
		return null;
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
	
	public LinkedHashMap<StringProperty, PVValueProperty> getPVMap(){
		return pvMap;
	}
	
	public ScriptsInput getScriptsInput(){
		return (ScriptsInput)getCastedPropertyValue(PROP_SCRIPTS);
	}
	public Dimension getSize(){
		return new Dimension(
				((Integer)getCastedPropertyValue(PROP_WIDTH)).intValue(),
				((Integer)getCastedPropertyValue(PROP_HEIGHT)).intValue());
	}
	
	public String getTooltip(){
		return (String)getCastedPropertyValue(PROP_TOOLTIP);
	}
	
	public String getType(){
		return (String)getCastedPropertyValue(PROP_WIDGET_TYPE);
	}
	
	
	/**
	 * @return the unique typeID of the model.
	 */
	public abstract String getTypeID();
	
	public String getVersion() {
		return VERSION;
	}
	
	public String getWidgetType(){
		return (String)getCastedPropertyValue(PROP_WIDGET_TYPE);
	}	
	
	public Boolean isEnabled(){
		return (Boolean)getCastedPropertyValue(PROP_ENABLED);
	}
	
	public boolean isPropertySet(Object id) {
		return !getProperty((String) id).isDefaultValue();
	}
	
	
	public Boolean isVisible(){
		return (Boolean)getCastedPropertyValue(PROP_VISIBLE);
	}
	
	/**Remove a property from the model.
	 * @param prop_id
	 */
	public synchronized void removeProperty(final String prop_id){
		if(!propertyMap.containsKey(prop_id))
			return;
		AbstractWidgetProperty property = propertyMap.get(prop_id);
		property.removeAllPropertyChangeListeners();
		if(property.isVisibleInPropSheet())
			propertyDescriptors.remove(prop_id);
		propertyMap.remove(prop_id);
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
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, getProperty((String) id).getDefaultValue());
	}
	
	public void setBackgroundColor(RGB color){
		setPropertyValue(PROP_COLOR_BACKGROUND, color);
	}
	
	public void setEnabled(boolean enable){
		setPropertyValue(PROP_ENABLED, enable);
	}
	
	public void setBorderColor(RGB color){
		setPropertyValue(PROP_BORDER_COLOR, color);
	}
	
	public void setBorderStyle(BorderStyle borderStyle){
		int i=0;
		for(BorderStyle bs : BorderStyle.values()){
			if(borderStyle == bs){
				break;
			}
			i++;
		}
		setPropertyValue(PROP_BORDER_STYLE, i);
	}
	
	public void setBorderWidth(int width){
		setPropertyValue(PROP_BORDER_WIDTH, width);
	}
	
	public void setForegroundColor(RGB color){
		setPropertyValue(PROP_COLOR_FOREGROUND, color);
	}
	

	
	public void setLocation(int x, int y){
		setPropertyValue(PROP_XPOS, x);
		setPropertyValue(PROP_YPOS, y);
	}
	
	public void setLocation(Point point){
		setLocation(point.x, point.y);
	}
	
	public void setName(String name){
		setPropertyValue(PROP_NAME, name);
	}
	
	public void setPropertyDescription(String prop_id, String description){
		if(getProperty(prop_id) == null)
			return;
		getProperty(prop_id).setDescription(description);
		if(propertyDescriptors.containsKey(prop_id))			
			propertyDescriptors.put(prop_id, getProperty(prop_id).getPropertyDescriptor());
	}
	
	public void setPropertyValue(Object id, Object value) {
		Assert.isTrue(propertyMap.containsKey(id));
		propertyMap.get(id).setPropertyValue(value);
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
	
	public void setSize(Dimension dimension){
		setSize(dimension.width, dimension.height);
	}

	public void setSize(int width, int height){
		setPropertyValue(PROP_WIDTH, width);
		setPropertyValue(PROP_HEIGHT, height);
	}
	
	public void setTooltip(String tooltip){
		setPropertyValue(PROP_TOOLTIP, tooltip);
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(AbstractContainerModel parent) {
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public AbstractContainerModel getParent() {
		return parent;
	}
	
	/**
	 * @return the nested depth of the widget in the model tree.
	 */
	public int getNestedDepth(){
		//display model
		if(getParent() == null)
			return 0;
		int i=1;
		AbstractContainerModel parent = getParent();
		while(!(parent instanceof DisplayModel)){
			i++;
			parent = parent.getParent();
		}
		return i;
	}
	
	
}
