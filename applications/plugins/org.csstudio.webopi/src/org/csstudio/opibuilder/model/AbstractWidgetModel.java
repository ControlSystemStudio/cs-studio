/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.opibuilder.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.ActionsProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.RulesProperty;
import org.csstudio.opibuilder.properties.ScriptProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.UnchangableStringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.script.RulesInput;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**The abstract base model for display and all widgets .
 *@author Alexander Will, Sven Wende, Stefan Hofer (similar class of SDS)
 * @author Xihui Chen
 */
/**
 * @author Xihui Chen
 *
 */
public abstract class AbstractWidgetModel implements IAdaptable{
	
	public static final String VERSION = "1.0";
	
	/**
	 * The Name of the widget.
	 */
	public static final String PROP_NAME = "name";//$NON-NLS-1$
	
	/**
	 * Scripts attached to the widget.
	 */
	public static final String PROP_SCRIPTS = "scripts";//$NON-NLS-1$
	
	/**
	 * Rules attached to the widget.
	 */
	public static final String PROP_RULES = "rules";//$NON-NLS-1$
	
	/**
	 * X position of the widget.
	 */
	public static final String PROP_XPOS = "x";//$NON-NLS-1$
	
	/**
	 * Y position of the widget.
	 */
	public static final String PROP_YPOS = "y";//$NON-NLS-1$
	
	/**
	 * Width of the widget.
	 */
	public static final String PROP_WIDTH = "width";//$NON-NLS-1$
	
	/**
	 * Height of the widget.
	 */
	public static final String PROP_HEIGHT = "height";//$NON-NLS-1$
	
	/**
	 * Background color.
	 */
	public static final String PROP_COLOR_BACKGROUND = "background_color";//$NON-NLS-1$
	
	/**
	 * Foreground color.
	 */
	public static final String PROP_COLOR_FOREGROUND = "foreground_color";//$NON-NLS-1$
	
	/**
	 * Foreground color.
	 */
	public static final String PROP_FONT = "font";//$NON-NLS-1$
	
	/**
	 * Visibility of the widget.
	 */
	public static final String PROP_VISIBLE = "visible";//$NON-NLS-1$
	
	/**
	 * Enable status. Only effective for control widgets which will make control widget
	 * uncontrollable if this is false.
	 */
	public static final String PROP_ENABLED = "enabled";//$NON-NLS-1$
	
	/**
	 * Actions attached to the widget, which can be accessed on runtime
	 * via context menu <code>Actions</code>.
	 */
	public static final String PROP_ACTIONS = "actions";//$NON-NLS-1$
	
	/**
	 * Tooltip of the widget, which will show up when mouse hover on the widget.
	 * Macros are allowed and can be updated. 
	 * The property macro $(pv_value) could be used to show the PV value which 
	 * has timestamp, value, severity and status.
	 */
	public static final String PROP_TOOLTIP = "tooltip"; //$NON-NLS-1$
	
	/**
	 * Color of border.
	 */
	public static final String PROP_BORDER_COLOR = "border_color"; //$NON-NLS-1$

	/**
	 * Width of border.
	 */
	public static final String PROP_BORDER_WIDTH = "border_width"; //$NON-NLS-1$
	/**
	 * Style of border.
	 */
	public static final String PROP_BORDER_STYLE = "border_style"; //$NON-NLS-1$	
	
	/**
	 *The type of the widget. This is the only property that cannot be edited.
	 *The name and type of the selected widget will also be displayed on the status bar. 
	 */
	public static final String PROP_WIDGET_TYPE= "widget_type"; //$NON-NLS-1$
	
	private Map<String, AbstractWidgetProperty> propertyMap;
	
	/**
	 * The map contains properties which are allowed to change during running.
	 */
	private List<AbstractWidgetProperty> runtimePropertyList;
	
	
	private AbstractContainerModel parent;
	
	private LinkedHashMap<StringProperty, PVValueProperty> pvMap;
	
	private ExecutionMode executionMode;
	

	public AbstractWidgetModel() {
		propertyMap = new HashMap<String, AbstractWidgetProperty>();
		pvMap = new LinkedHashMap<StringProperty, PVValueProperty>();
		configureBaseProperties();
		configureProperties();	
	}
	
	/**Add a property to the widget.
	 * @param property the property to be added.
	 */
	public void addProperty(final AbstractWidgetProperty property){
		Assert.isNotNull(property);
		property.setWidgetModel(this);
		propertyMap.put(property.getPropertyID(), property);
	}
	
	/**Add a property to the widget with the option to set it running changeable.
	 * @param property the property to be added.
	 * @param runtimeChangeable true if this property is changeable during running. false otherwise.
	 */
	public void addProperty(final AbstractWidgetProperty property, final boolean runtimeChangeable){
		addProperty(property);
		if(runtimeChangeable){
			if(runtimePropertyList == null)
				runtimePropertyList = new ArrayList<AbstractWidgetProperty>();
			runtimePropertyList.add(property);
		}
	}
	
	public List<AbstractWidgetProperty> getRuntimePropertyList() {
		return runtimePropertyList;
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
	
	private void checkPropertyExist(Object propID) {
		if(!propertyMap.containsKey(propID))
			throw new NonExistPropertyException(getName(), propID.toString());
	}
	
	protected void configureBaseProperties() {
		addProperty(new IntegerProperty(PROP_WIDTH, "Width", 
				WidgetPropertyCategory.Position, 100, 1, 10000));
		addProperty(new IntegerProperty(PROP_HEIGHT, "Height", 
				WidgetPropertyCategory.Position, 100, 1, 10000));		
		addProperty(new IntegerProperty(PROP_XPOS, "X", 
				WidgetPropertyCategory.Position, 0));
		addProperty(new IntegerProperty(PROP_YPOS, "Y", 
				WidgetPropertyCategory.Position, 0));			
		addProperty(new ColorProperty(PROP_COLOR_BACKGROUND, "Background Color",
				WidgetPropertyCategory.Display, new RGB(240, 240, 240)));
		addProperty(new ColorProperty(PROP_COLOR_FOREGROUND, "Foreground Color",
				WidgetPropertyCategory.Display, new RGB(192, 192, 192)));
		addProperty(new FontProperty(PROP_FONT, "Font", 
				WidgetPropertyCategory.Display, MediaService.DEFAULT_FONT));
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
		addProperty(new RulesProperty(PROP_RULES, "Rules", WidgetPropertyCategory.Behavior));	
			
		WidgetDescriptor descriptor = WidgetsService.getInstance().getWidgetDescriptor(getTypeID());
		String name;
		name = descriptor == null? getTypeID().substring(getTypeID().lastIndexOf(".")+1) :
			descriptor.getName();
		addProperty(new StringProperty(PROP_NAME, "Name",
				WidgetPropertyCategory.Basic, name)); 	
		addProperty(new UnchangableStringProperty(PROP_WIDGET_TYPE, "Widget Type",
				WidgetPropertyCategory.Basic, name));
		
	}
	
	/**
	 * Configure the properties of the widget. Subclass should add 
	 * new properties in this method. 
	 */
	protected abstract void configureProperties();
	
	@SuppressWarnings("rawtypes")
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
	
	public Color getSWTColorFromColorProperty(String propID){
		return ((OPIColor)getCastedPropertyValue(propID)).getSWTColor();
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
		checkPropertyExist(propertyName);
		return (TYPE) getProperty(propertyName).getPropertyValue();
	}
	
	

	public Object getEditableValue() {
		return this;
	}

	
	
	
	public ActionsInput getActionsInput(){
		return (ActionsInput)getCastedPropertyValue(PROP_ACTIONS);
	}
	
	public Rectangle getBounds(){
		return new Rectangle(getLocation(), getSize());
	}
	
	public OPIFont getFont(){
		return (OPIFont)getPropertyValue(PROP_FONT);
	}
	
	public RGB getForegroundColor(){
		return getRGBFromColorProperty(PROP_COLOR_FOREGROUND);
	}
	
	public Point getLocation(){
		return new Point(
				((Integer)getCastedPropertyValue(PROP_XPOS)).intValue(),
				((Integer)getCastedPropertyValue(PROP_YPOS)).intValue());
	}
	
	public int getX(){
		return ((Integer)getCastedPropertyValue(PROP_XPOS)).intValue();
	}
	public int getY(){
		return ((Integer)getCastedPropertyValue(PROP_YPOS)).intValue();
	}
	
	public String getName(){
		return (String)getCastedPropertyValue(PROP_NAME);
	}
	
	public AbstractWidgetProperty getProperty(String prop_id){
		if((prop_id != null && propertyMap.containsKey(prop_id)))
			return propertyMap.get(prop_id);
		return null;
	}
	
	
	
	public Object getPropertyValue(Object id) {
		checkPropertyExist(id);
		return propertyMap.get(id).getPropertyValue();
	}
	
	public Object getRawPropertyValue(Object id) {
		checkPropertyExist(id);
		return propertyMap.get(id).getRawPropertyValue();
	}
	
	public LinkedHashMap<StringProperty, PVValueProperty> getPVMap(){
		return pvMap;
	}
	
	public RulesInput getRulesInput() {
		return (RulesInput)getPropertyValue(PROP_RULES);
	}
	
	public ScriptsInput getScriptsInput(){
		return (ScriptsInput)getCastedPropertyValue(PROP_SCRIPTS);
	}
	public Dimension getSize(){
		return new Dimension(
				((Integer)getCastedPropertyValue(PROP_WIDTH)).intValue(),
				((Integer)getCastedPropertyValue(PROP_HEIGHT)).intValue());
	}
	
	public int getWidth(){
		return ((Integer)getCastedPropertyValue(PROP_WIDTH)).intValue();
	}
	
	public int getHeight(){
		return ((Integer)getCastedPropertyValue(PROP_HEIGHT)).intValue();
	}
	
	public String getTooltip(){
		return (String)getCastedPropertyValue(PROP_TOOLTIP);
	}
	
	public String getRawTooltip(){
		return (String)getRawPropertyValue(PROP_TOOLTIP);
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
	
	public void setBounds(Rectangle bounds){
		setLocation(bounds.getLocation());
		setSize(bounds.getSize());
	}
	
	public void setBounds(int x, int y, int width, int height){
		setLocation(x, y);
		setSize(width, height);
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
	}
	
	public void setPropertyValue(Object id, Object value) {
		checkPropertyExist(id);		
		propertyMap.get(id).setPropertyValue(value);
		
	}
	
	public void setPropertyValue(Object id, Object value, boolean forceFire) {
		checkPropertyExist(id);
		propertyMap.get(id).setPropertyValue(value, forceFire);
	}
	
	public void setPropertyVisible(final String prop_id, final boolean visible){
		checkPropertyExist(prop_id);
		AbstractWidgetProperty property = propertyMap.get(prop_id);				
	}
	
	public void setSize(Dimension dimension){
		setSize(dimension.width, dimension.height);
	}

	public void setSize(int width, int height){
		setPropertyValue(PROP_WIDTH, width);
		setPropertyValue(PROP_HEIGHT, height);
	}
	
	public void setWidth(int width){
		setPropertyValue(PROP_WIDTH, width);
	}
	
	public void setHeight(int height){
		setPropertyValue(PROP_HEIGHT, height);
	}
	
	public void setX(int x){
		setPropertyValue(PROP_XPOS, x);
	}
	
	public void setY(int y){
		setPropertyValue(PROP_YPOS, y);
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
	 * @return the root display model for this widget. null if its parent is not set yet.
	 */
	public DisplayModel getRootDisplayModel(){
		AbstractContainerModel parent = getParent();
		if(parent == null){
			if(this instanceof DisplayModel)
				return (DisplayModel)this;
			else 
				return null;
		}
		while(!(parent instanceof DisplayModel)){			
			parent = parent.getParent();
		}
		return (DisplayModel) parent;
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

	/**
	 * @return the index of the widget in its parent's children list
	 */
	public int getIndex(){
		if(getParent() == null)
			return 0;
		return getParent().getChildren().indexOf(this);
	}
	
	/**
	 * @param executionMode the executionMode to set
	 */
	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}

	/**
	 * @return the executionMode
	 */
	public ExecutionMode getExecutionMode() {
		return executionMode;
	}	
	
	/**
	 * Flip the widget figure horizontally.
	 */
	public void flipHorizontally(){
	}
	
	/**
	 * Flip the widget figure horizontally.
	 * @param centerX the center X coordinate
	 */
	public void flipHorizontally(int centerX){
		setX(2*centerX-getX()-getWidth());
	}
	
	/**
	 * Flip the widget figure vertically.
	 */
	public void flipVertically(){
	}
	
	/**
	 * Flip the widget figure horizontally.
	 * @param centerY the center Y coordinate
	 */
	public void flipVertically(int centerY){
		setY(2*centerY - getY() - getHeight());
	}
	
	/**
	 * Rotate the widget figure 90 degree.
	 * @param clockwise true if rotate clockwise. false if counterclockwise.
	 */
	public void rotate90(boolean clockwise){
		int x = getX();
		int y = getY();
		int h = getHeight();
		int w = getWidth();
		
		int newX, newY, newH, newW;		

		newX = x+w/2-h/2;
		newY = y+h/2-w/2;
		newH = w;
		newW = h;
		
		setLocation(newX, newY);
		setSize(newW, newH);
		
	}	
	
	/**
	 * Rotate the widget figure 90 degree.
	 * @param clockwise true if rotate clockwise. false if counterclockwise.
	 */
	public void rotate90(boolean clockwise, Point center){
		//Point shiftedPoint = moveCoordinateToCenter(getLocation(), center);
		int x = getX() - center.x;
		int y = center.y - getY();
		int h = getHeight();
		int w = getWidth();
		
		int newX, newY, newH, newW;		
		if(clockwise){
			newX = y-h;
			newY = -x;
		}else{
			newX = -y ;
			newY = x+w;
		}
	//	Point rotatedPoint = recoverFromCenterCoordinate(new Point(newX, newY), center);
		newX = newX + center.x;
		newY = center.y - newY;
		newH = w;
		newW = h;
		
		setLocation(newX, newY);
		setSize(newW, newH);		
	}		

}
