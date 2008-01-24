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
package org.csstudio.sds.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.sds.cursorservice.CursorService;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.csstudio.sds.model.optionEnums.BorderStyleEnum;
import org.csstudio.sds.model.properties.ActionData;
import org.csstudio.sds.model.properties.ActionDataProperty;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.csstudio.sds.model.properties.OptionProperty;
import org.csstudio.sds.model.properties.ParameterStringProperty;
import org.csstudio.sds.model.properties.StringMapProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.csstudio.sds.model.properties.actions.WidgetAction;
import org.csstudio.sds.util.AbstractToolTipConverter;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.RGB;

/**
 * An abstract base class for all widget models.
 * 
 * @author Alexander Will, Sven Wende, Stefan Hofer
 * @version $Revision$
 * 
 */
public abstract class AbstractWidgetModel implements IAdaptable {
	/**
	 * ID for the <i>connection state</i> of the model.
	 */
	public static final String PROP_LIVE = "PROP_LIVE"; //$NON-NLS-1$

	/**
	 * Property ID to use when the list of outgoing connections is modified.
	 */
	public static final String PROP_CONNECTIONS_SOURCE = "connections.source"; //$NON-NLS-1$

	/**
	 * Property ID to use when the list of incoming connections is modified.
	 */
	public static final String PROP_CONNECTIONS_TARGET = "connections.target"; //$NON-NLS-1$

	/**
	 * Property ID for <i>width</i>.
	 */
	public static final String PROP_WIDTH = "width"; //$NON-NLS-1$

	/**
	 * Property ID for <i>height</i>.
	 */
	public static final String PROP_HEIGHT = "height"; //$NON-NLS-1$

	/**
	 * Property ID for the <i>X coordinate</i>.
	 */
	public static final String PROP_POS_X = "position.x"; //$NON-NLS-1$

	/**
	 * Property ID for <i>Y coordinate</i>.
	 */
	public static final String PROP_POS_Y = "position.y"; //$NON-NLS-1$

	/**
	 * The ID of the background color property.
	 */
	public static final String PROP_COLOR_BACKGROUND = "color.background"; //$NON-NLS-1$

	/**
	 * The ID of the foreground color property.
	 */
	public static final String PROP_COLOR_FOREGROUND = "color.foreground"; //$NON-NLS-1$		

	/**
	 * The ID of the border color property.
	 */
	public static final String PROP_BORDER_COLOR = "border.color"; //$NON-NLS-1$

	/**
	 * The ID of the border width property.
	 */
	public static final String PROP_BORDER_WIDTH = "border.width"; //$NON-NLS-1$

	/**
	 * The ID of the border style property.
	 */
	public static final String PROP_BORDER_STYLE = "border.style"; //$NON-NLS-1$

	/**
	 * The ID of the visibility color property.
	 */
	public static final String PROP_VISIBILITY = "visibility"; //$NON-NLS-1$

	/**
	 * The ID for the alias property.
	 */
	public static final String PROP_PERMISSSION_ID = "permission_id"; //$NON-NLS-1$

	/**
	 * The ID for the alias property.
	 */
	public static final String PROP_ENABLED = "enabled"; //$NON-NLS-1$

	/**
	 * The ID for the alias property.
	 */
	public static final String PROP_ALIASES = "aliases"; //$NON-NLS-1$

	/**
	 * The ID for the layer property.
	 */
	public static final String PROP_LAYER = "layer"; //$NON-NLS-1$

	/**
	 * The ID for the name property.
	 */
	public static final String PROP_NAME = "name"; //$NON-NLS-1$

	/**
	 * The ID for the primary PV property.
	 */
	public static final String PROP_PRIMARY_PV = "primary_pv"; //$NON-NLS-1$
	
	/**
	 * The ID of the {@link ActionData} property.
	 */
	public static final String PROP_ACTIONDATA = "actionData"; //$NON-NLS-1$
	
	/**
	 * The ID of the tooltip property.
	 */
	public static final String PROP_TOOLTIP = "tooltip"; //$NON-NLS-1$
	
	/**
	 * The ID of the cursor property.
	 */
	public static final String PROP_CURSOR = "cursor"; //$NON-NLS-1$
	
	/**
	 * The ID of the rotation property.
	 * Is only used if the widget is rotatable.
	 * @see AbstractWidgetModel#isRotatable()
	 */
	public static final String PROP_ROTATION = "rotation"; //$NON-NLS-1$

	/**
	 * A boolean, representing if this Model is alive.
	 */
	private boolean _live;

	/**
	 * Property event handler.
	 */
	private PropertyChangeSupport _propertyChangeSupport;

	/**
	 * This map holds all the properties of the widget model.
	 */
	private Map<String, WidgetProperty> _propertyMap;
	
	/**
	 * This map holds all the names of the invisible properties of the widget model.
	 */
	private Set<String> _invisiblePropertyNames;

	/**
	 * Stores outgoing connections.
	 */
	private List<ConnectionElement> _sourceConnections = new ArrayList<ConnectionElement>();

	/**
	 * Stores incoming connections.
	 */
	private List<ConnectionElement> _targetConnections = new ArrayList<ConnectionElement>();

	/**
	 * The initial <i>height</i> value for all widget models.
	 */
	private static final int DEFAULT_HEIGHT = 10;

	/**
	 * The initial <i>width</i> value for all widget models.
	 */
	private static final int DEFAULT_WIDTH = 10;

	/**
	 * The initial <i>Y coordinate</i> value for all widget models.
	 */
	private static final int DEFAULT_Y = 10;

	/**
	 * The initial <i>X coordinate</i> value for all widget models.
	 */
	private static final int DEFAULT_X = 10;

	/**
	 * The initial <i>enabled</i> value for all widget models.
	 */
	private static final boolean DEFAULT_ENABLED = true;

	/**
	 * The initial <i>layer</i> value for all widget models.
	 */
	private static final String DEFAULT_LAYER = "";

	/**
	 * The parent model of this model.
	 */
	private ContainerModel _parent;

	/**
	 * Holds if this widget is rotatable.
	 */
	private boolean _isRotatable;

	/**
	 * Return a property id for a {@link DoubleProperty} that can be used for
	 * simulation purposes.
	 * 
	 * @return a property id for a {@link DoubleProperty} that can be used for
	 *         simulation purposes.
	 * @deprecated Will be removed soon!
	 */
	@Deprecated
	public String getDoubleTestProperty() {
		return null;
	}

	/**
	 * Return a property id for a {@link DoubleArrayProperty} that can be used
	 * for simulation purposes.
	 * 
	 * @return a property id for a {@link DoubleArrayProperty} that can be used
	 *         for simulation purposes.
	 * @deprecated Will be removed soon!
	 */
	@Deprecated
	public String getDoubleSeqTestProperty() {
		return null;
	}

	/**
	 * Return a property id for a {@link ColorProperty} that can be used for
	 * simulation purposes.
	 * 
	 * @return a property id for a {@link ColorProperty} that can be used for
	 *         simulation purposes.
	 * @deprecated Will be removed soon!
	 */
	@Deprecated
	public String getColorTestProperty() {
		return null;
	}
	
	/**
	 * Standard constructor.
	 * Creates a not rotatable widget
	 */
	public AbstractWidgetModel() {
		this(false);
	}

	/**
	 * Standard constructor.
	 * @param isRotatable true if this widget is rotatable
	 */
	public AbstractWidgetModel(final boolean isRotatable) {
		_propertyChangeSupport = new PropertyChangeSupport(this);
		_propertyMap = new HashMap<String, WidgetProperty>();
		_invisiblePropertyNames = new HashSet<String>();
		_isRotatable = isRotatable;

		configureBaseProperties(_isRotatable);
		configureProperties();
		configureToolTipProperty();
		
		markBasePropertiesAsInvisible();
		markPropertiesAsInvisible();
	}

	/**
	 * Returns a default name for this model.
	 * 
	 * @return String The default name
	 */
	protected final String getDefaultName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Initialize the basic geometric properties such as <i>width</i>,
	 * <i>height</i>, <i>X coordinate</i>, <i>Y coordinate</i>.
	 * @param isRotatable If the widget is rotatable, than a rotation-property is added
	 */
	private void configureBaseProperties(final boolean isRotatable) {
		// positions
		addProperty(PROP_POS_X, new IntegerProperty("X-Coordinate",
				WidgetPropertyCategory.Position, DEFAULT_X));
		addProperty(PROP_POS_Y, new IntegerProperty("Y-Coordinate",
				WidgetPropertyCategory.Position, DEFAULT_Y));
		addProperty(PROP_WIDTH, new IntegerProperty("Width",
				WidgetPropertyCategory.Position, DEFAULT_WIDTH, 1,
				Integer.MAX_VALUE));
		addProperty(PROP_HEIGHT, new IntegerProperty("Height",
				WidgetPropertyCategory.Position, DEFAULT_HEIGHT, 1,
				Integer.MAX_VALUE));
		addProperty(PROP_LAYER, new StringProperty("Layer",
				WidgetPropertyCategory.Position, DEFAULT_LAYER));
		// colors
		addProperty(PROP_COLOR_BACKGROUND, new ColorProperty(
				"Background Color", WidgetPropertyCategory.Display, new RGB(
						240, 240, 240)));
		addProperty(PROP_COLOR_FOREGROUND, new ColorProperty(
				"Foreground Color", WidgetPropertyCategory.Display, new RGB(
						200, 100, 100)));
		// visibility
		addProperty(PROP_VISIBILITY, new BooleanProperty("Visibility",
				WidgetPropertyCategory.Behaviour, true));

		addProperty(PROP_BORDER_COLOR, new ColorProperty("Border Color",
				WidgetPropertyCategory.Border, new RGB(100, 0, 0)));

		addProperty(PROP_BORDER_WIDTH, new IntegerProperty("Border Width",
				WidgetPropertyCategory.Border, 0, 0, Integer.MAX_VALUE));

//		addProperty(PROP_BORDER_STYLE, new OptionProperty("Border Style",
//				WidgetPropertyCategory.Border, new String[] {"None", "Line Style",
//						"Labeled Style", "Raised Style","Lowered Style", "Striated Style",
//						"Shape Style" }, 0));
		addProperty(PROP_BORDER_STYLE, new OptionProperty("Border Style",
				WidgetPropertyCategory.Border, BorderStyleEnum.getDisplayNames(), BorderStyleEnum.LINE.getIndex()));
		// Property for Aliases
		addProperty(PROP_ALIASES,
				new StringMapProperty("Alias",
						WidgetPropertyCategory.Behaviour,
						new HashMap<String, String>()));
		addProperty(PROP_ENABLED, new BooleanProperty("Enabled",
				WidgetPropertyCategory.Behaviour, DEFAULT_ENABLED));
		addProperty(PROP_PERMISSSION_ID, new StringProperty("Permission ID",
				WidgetPropertyCategory.Behaviour, ""));
		// Name
		addProperty(PROP_NAME, new StringProperty("Name",
				WidgetPropertyCategory.Display, this.getDefaultName()));
		// Primary PV
		addProperty(PROP_PRIMARY_PV, new StringProperty("Primary PV",
				WidgetPropertyCategory.Behaviour, ""));
		// ActionData
		addProperty(PROP_ACTIONDATA, new ActionDataProperty("Action Data",
				WidgetPropertyCategory.Behaviour, new ActionData()));
		// Cursor
		addProperty(PROP_CURSOR, new OptionProperty("Cursor",
				WidgetPropertyCategory.Display, CursorService.getInstance().getDisplayNames(), CursorService.getInstance().getDefaultCursorIndex()));
		
		if (isRotatable) {
			addProperty(PROP_ROTATION, new DoubleProperty("Rotation Angle", WidgetPropertyCategory.Display, 0, 0, 360));
		}
	}
		
	/**
	 * Configures the tooltip-property.
	 */
	private void configureToolTipProperty() {
		addProperty(PROP_TOOLTIP, new ParameterStringProperty("ToolTip",
				WidgetPropertyCategory.Display, this.getDefaultToolTip()));
	}
	
	/**
	 * Returns the default tooltip for this model.
	 * @return the tooltip text
	 */
	protected String getDefaultToolTip() {
		return this.createParameter(PROP_NAME);
	}
	
	/**
	 * Returns the given parameter encapsulated by '${' and '}$'. 
	 * @param propertyName The name of the parameter
	 * 
	 * @deprecated 2008.01.08 : swende: Inperformant. Besser nur den SEPARATOR verwenden
	 * @return the created parameter
	 */
	protected final String createParameter(final String propertyName) {
		return AbstractToolTipConverter.START_SEPARATOR+propertyName+AbstractToolTipConverter.END_SEPARATOR;
	}
	
	/**
	 * Marks the the property with the given ID as invisible.
	 * @param propertyID The ID of the property
	 */
	protected final void markPropertyAsInvisible(final String propertyID) {
		_invisiblePropertyNames.add(propertyID);
	}
	
	/**
	 * Marks the the property with the given ID as visible.
	 * @param propertyID The ID of the property
	 */
	protected final void markPropertyAsVisible(final String propertyID) {
		_invisiblePropertyNames.remove(propertyID);
	}

	/**
	 * Sets the connection state of this model.
	 * 
	 * @param isLive
	 *            The new connection state
	 */
	public void setLive(final boolean isLive) {
		if (_live != isLive) {
			_live = isLive;
			firePropertyChangeEvent(PROP_LIVE, !_live, _live);
		}
	}

	/**
	 * Returns the connection state of this model.
	 * 
	 * @return true, if the model is connected to the control system, false
	 *         otherwise
	 */
	public boolean isLive() {
		return _live;
	}
	
	/**
	 * Returns if this widget is rotatable or not.
	 * @return true id this widget is rotatable, false otherwise
	 */
	public boolean isRotatable() {
		return _isRotatable;
	}

	/**
	 * Convenience getter to access the <i>height</i> property.
	 * 
	 * @return The value of the <i>height</i> property.
	 */
	public final synchronized int getHeight() {
		return getCastedPropertyValue(PROP_HEIGHT);
	}

	/**
	 * Convenience setter to access the <i>height</i> property.
	 * 
	 * @param height
	 *            The value for the <i>height</i> property.
	 */
	public synchronized void setHeight(final int height) {
		setPropertyValue(PROP_HEIGHT, height);
	}
	
	/**
	 * Returns the {@link ActionData} of this widget and its parents.
	 * @return The {@link ActionData}
	 */
	public ActionData getActionData() {
		ActionData result = new ActionData();
		ActionData ownData = this.getOwnActionData();
		for (WidgetAction action : ownData.getWidgetActions()) {
			result.addAction(action);
		}
		if (this.getParent()!=null) {
			ActionData parentData = this.getParent().getActionData();
			for (WidgetAction action : parentData.getWidgetActions()) {
				result.addAction(action);
			}	
		}
		return result;
	}
	
	/**
	 * Returns only the {@link ActionData} of this widget.
	 * @return The {@link ActionData}
	 */
	public ActionData getOwnActionData() {
		return (ActionData) getProperty(PROP_ACTIONDATA).getPropertyValue();
	}

	/**
	 * Gets the background color.
	 * 
	 * @return the background color
	 */
	public final RGB getBackgroundColor() {
		return (RGB) getProperty(PROP_COLOR_BACKGROUND).getPropertyValue();
	}

	/**
	 * Sets the background color.
	 * 
	 * @param rgb
	 *            the background color
	 */
	public final void setBackgroundColor(final RGB rgb) {
		setPropertyValue(PROP_COLOR_BACKGROUND, rgb);
	}

	/**
	 * Gets the foreground color.
	 * 
	 * @return the foreground color
	 */
	public final RGB getForegroundColor() {
		return (RGB) getProperty(PROP_COLOR_FOREGROUND).getPropertyValue();
	}

	/**
	 * Sets the foreground color.
	 * 
	 * @param rgb
	 *            the foreground color
	 */
	public final void setForegroundColor(final RGB rgb) {
		setPropertyValue(PROP_COLOR_FOREGROUND, rgb);
	}

	/**
	 * Returns the permission id.
	 * 
	 * @return String The permission id
	 */
	public final String getPermissionID() {
		return (String) getProperty(PROP_PERMISSSION_ID).getPropertyValue();
	}

	/**
	 * Returns the enable state.
	 * 
	 * @return boolean The enable state
	 */
	public final boolean isEnabled() {
		return (Boolean) getProperty(PROP_ENABLED).getPropertyValue();
	}

	/**
	 * Sets the enable state of this model.
	 * 
	 * @param enabled
	 *            The new enable state
	 */
	public final void setEnabled(final boolean enabled) {
		setPropertyValue(PROP_ENABLED, enabled);
	}

	/**
	 * Returns the name of the layer.
	 * 
	 * @return String The name of the layer
	 */
	public final String getLayer() {
		return (String) getProperty(PROP_LAYER).getPropertyValue();
	}

	/**
	 * Sets the index of the layer.
	 * 
	 * @param layer
	 *            The index of the layer
	 */
	public final void setLayer(final String layer) {
		setPropertyValue(PROP_LAYER, layer);
	}
	
	/**
	 * Returns the cursor for this widget.
	 * 
	 * @return The cursor
	 */
	public final int getCursor() {
		return (Integer) getProperty(PROP_CURSOR).getPropertyValue();
	}

	/**
	 * Sets the {@link Cursor}.
	 * 
	 * @param cursor
	 *            The {@link Cursor}
	 */
	public final void setCursor(final Cursor cursor) {
		setPropertyValue(PROP_CURSOR, cursor);
	}
	
	/**
	 * Returns the rotation angle for this widget.
	 * Returns 0 if this widget is not rotatable
	 * 
	 * @return The rotation angle
	 */
	public final double getRotationAngle() {
		if (this.isRotatable()) {
			return (Double) getProperty(PROP_ROTATION).getPropertyValue();
		}
		return 0.0;
	}

	/**
	 * Sets the rotation angle for this widget, only when this widget is rotatable.
	 * 
	 * @param angle
	 *            The angle
	 */
	public final void setRotationAngle(final double angle) {
		if (this.isRotatable()) {
			setPropertyValue(PROP_ROTATION, angle);
		}
	}

	/**
	 * Return whether the widget model has a property with the given ID.
	 * 
	 * @param name
	 *            The ID of the property.
	 * @return True, if the widget model has a property with the given ID.
	 */
	public final synchronized boolean hasProperty(final String name) {
		boolean result = false;
		if (_propertyMap.containsKey(name)) {
			result = true;
		}

		return result;
	}

	/**
	 * Return a set with all the property IDs of the widget model.
	 * 
	 * @return A set with all the property IDs of the widget model.
	 * 
	 * @deprecated 2008.01.08: swende: Inperformant. Besser auf _propertyMap direkt zugreifen.
	 */
	public final synchronized Set<String> getPropertyNames() {
		return new HashSet<String>(_propertyMap.keySet());
	}
	
	/**
	 * Returns a set with the property IDs of the widget model, which are visible.
	 * @return A set with the property IDs of the widget model, which are visible
	 */
	public final synchronized Set<String> getVisiblePropertyNames() {
		Set<String> result = new HashSet<String>(_propertyMap.keySet());
		result.removeAll(_invisiblePropertyNames);
		return result;
	}

	/**
	 * Returns the number of properties.
	 * 
	 * @return the number of properties
	 */
	public final synchronized int getPropertyCount() {
		return _propertyMap.keySet().size();
	}
	
	/**
	 * Returns the number of properties, which are visible.
	 * 
	 * @return the number of properties, which are visible
	 */
	public final synchronized int getVisiblePropertyCount() {
		return this.getVisiblePropertyNames().size();
	}

	/**
	 * Return the widget model property with the given ID.
	 * 
	 * @param name
	 *            The ID of the property.
	 * @return The widget model property with the given ID.
	 */
	public final synchronized WidgetProperty getProperty(final String name) {
		assert name != null;
		assert hasProperty(name);

		return _propertyMap.get(name);
	}

	/**
	 * Add a property to the widget model.
	 * 
	 * @param id
	 *            ID of the property.
	 * @param property
	 *            the property
	 */
	protected final void addProperty(final String id,
			final WidgetProperty property) {
		_propertyMap.put(id, property);
	}

	/**
	 * Set the value of an widget property.
	 * 
	 * @param propertyID
	 *            The ID of the property.
	 * @param value
	 *            The new value of the property.
	 */
	public synchronized void setPropertyValue(final String propertyID,
			final Object value) {
		WidgetProperty property = _propertyMap.get(propertyID);

		if (property != null) {
			property.setPropertyValue(value);
		}
	}

	/**
	 * Add an alias.
	 * 
	 * @param name
	 *            The name of the alias
	 * @param value
	 *            The value of the alias
	 */
	public final synchronized void addAlias(final String name,
			final String value) {
		assert name != null;
		assert value != null;
		
		Map<String, String> aliases = new HashMap<String, String>();
		for (String key : this.getAliases().keySet()) {
			aliases.put(key, this.getAliases().get(key));
		}
		aliases.put(name, value);
		this.setPropertyValue(PROP_ALIASES, aliases);
	}

	/**
	 * Remove the given alias descriptor from the widget model's internal alias
	 * descriptors set.
	 * 
	 * @param name
	 *            The name of the alias that is to be removed.
	 */
	public final synchronized void removeAlias(final String name) {
		if (this.getAliases().remove(name)!=null) {
			firePropertyChangeEvent(PROP_ALIASES, null, this.getAliases());
		}
	}

	/**
	 * Sets the given Map as Aliases.
	 * 
	 * @param map
	 *            A Map, which keys and values are Strings
	 */
	public final void setAliases(final Map<String, String> map) {
		this.setPropertyValue(PROP_ALIASES, map);
	}

	/**
	 * Returns the Map for the Aliases. The keys and the values are Strings
	 * 
	 * @return Map The Map of Aliases
	 */
	@SuppressWarnings("unchecked")
	public final Map<String, String> getAliases() {
		return (Map<String, String>) getProperty(PROP_ALIASES).getPropertyValue();
	}

	/**
	 * Recursive method, which returns all aliases that can be used within this
	 * widget model. The method relies on the parent-relationship to other
	 * (container) widgets.
	 * 
	 * Due to the recursive mechanism, aliases of parents are overridden by
	 * aliases of their children. This way aliases can be re-defined by child
	 * models.
	 * 
	 * @return a complete map of all alias replacements (key=alias name e.g.
	 *         "channel", value = replacement string e.g. "cryo/pump3")
	 */
	public final Map<String, String> getAllInheritedAliases() {
		
		Map<String, String> result = new HashMap<String, String>();

		if (getParent() != null) {
			// get aliases from parent
			Map<String, String> inheritedAliases = getParent()
					.getAllInheritedAliases();

			// put them into the result list first
			result.putAll(inheritedAliases);

			// put our own aliases at last
			result.putAll(getAliases());
		} else {
			Map<String, String> myAliases = getAliases();

			if (myAliases != null) {
				result.putAll(myAliases);
			}
		}

		return result;
	}

	/**
	 * Sets the specified alias.
	 * 
	 * @param name
	 *            the alias ID
	 * @param value
	 *            the alias value
	 */
	public final void setAliasValue(final String name, final String value) {
		assert name != null;
		assert value != null;
		this.getAliases().put(name, value);
	}

	/**
	 * Set the dynamics descriptor of an widget property.
	 * 
	 * @param propertyID
	 *            The ID of the property.
	 * @param dynamicsDescriptor
	 *            The dynamics descriptor.
	 */
	public final synchronized void setDynamicsDescriptor(
			final String propertyID, final DynamicsDescriptor dynamicsDescriptor) {
		WidgetProperty p = _propertyMap.get(propertyID);

		if (p != null) {
			p.setDynamicsDescriptor(dynamicsDescriptor);

			// important, send a change event for the corresponding property
			// together with the current value of the property - do never send
			// the dynamics descriptor itself as value, because it is not
			// compatible to the expected data type for that property
			Object propertyValue = getProperty(propertyID).getPropertyValue();
			firePropertyChangeEvent(propertyID, propertyValue, propertyValue);
		}
	}

	/**
	 * Return the dynamics descriptor of the widget property with the given ID.
	 * 
	 * @param propertyID
	 *            The ID of the property.
	 * @return The dynamics descriptor of the widget property with the given ID.
	 */
	public final synchronized DynamicsDescriptor getDynamicsDescriptor(
			final String propertyID) {
		DynamicsDescriptor result = null;

		WidgetProperty p = _propertyMap.get(propertyID);

		if (p != null) {
			result = p.getDynamicsDescriptor();
		}

		return result;
	}

	/**
	 * Convenience getter to access the <i>width</i> property.
	 * 
	 * @return The value of the <i>width</i> property.
	 */
	public final synchronized int getWidth() {
		return getCastedPropertyValue(PROP_WIDTH);
	}

	/**
	 * Convenience setter to access the <i>width</i> property.
	 * 
	 * @param width
	 *            The value for the <i>width</i> property.
	 */
	public synchronized void setWidth(final int width) {
		setPropertyValue(PROP_WIDTH, width);
	}

	/**
	 * Convenience getter to access the <i>X coordinate</i> property.
	 * 
	 * @return The value of the <i>X coordinate</i> property.
	 */
	public final synchronized int getX() {
		return getCastedPropertyValue(PROP_POS_X);
	}
	
	/**
	 * Convenience getter to access the <i>X coordinate</i> property relative to the given ancestor.
	 * @param ancestor An ancestor of this widget
	 * @return The value of the <i>X coordinate</i> property.
	 */
	public final synchronized int getXForAncestor(final ContainerModel ancestor) {
		assert isAncestorReachable(ancestor) : "Ancestor ("+ancestor+") is not reachable from "+this;
		int result = getCastedPropertyValue(PROP_POS_X);
		ContainerModel parent = this.getParent();
		while (parent!=null && !parent.equals(ancestor)) {
			result = result + parent.getX();
			parent = parent.getParent();
		}
		return result;
	}
	
	/**
	 * Convenience getter to access the <i>Y coordinate</i> property relative to the given ancestor.
	 * @param ancestor An ancestor of this widget
	 * @return The value of the <i>Y coordinate</i> property.
	 */
	public final synchronized int getYForAncestor(final ContainerModel ancestor) {
		assert isAncestorReachable(ancestor) : "Ancestor ("+ancestor+") is not reachable from "+this;
		int result = getCastedPropertyValue(PROP_POS_Y);
		ContainerModel parent = this.getParent();
		while (parent!=null && !parent.equals(ancestor)) {
			result = result + parent.getY();
			parent = parent.getParent();
		}
		return result;
	}
	
	/**
	 * Determines if the given {@link ContainerModel} is an ancestor of this model.
	 * @param ancestor The probably ancestor
	 * @return true, if the given {@link ContainerModel} is an ancestor of this model, false otherwise
	 */
	private boolean isAncestorReachable(final ContainerModel ancestor) {
		ContainerModel parent = this.getParent();
		while (parent!=null) {
			if (parent.equals(ancestor)) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}

	/**
	 * Convenience setter to access the <i>X coordinate</i> property.
	 * 
	 * @param x
	 *            The value for the <i>X coordinate</i> property.
	 */
	public synchronized void setX(final int x) {
		setPropertyValue(PROP_POS_X, x);
	}

	/**
	 * Convenience getter to access the <i>Y coordinate</i> property.
	 * 
	 * @return The value of the <i>Y coordinate</i> property.
	 */
	public final synchronized int getY() {
		return getCastedPropertyValue(PROP_POS_Y);
	}

	/**
	 * Convenience setter to access the <i>Y coordinate</i> property.
	 * 
	 * @param y
	 *            The value for the <i>Y coordinate</i> property.
	 */
	public synchronized void setY(final int y) {
		setPropertyValue(PROP_POS_Y, y);
	}

	/**
	 * Convenience getter to access the <i>name</i> property.
	 * 
	 * @return The value of the <i>name</i> property.
	 */
	public final synchronized String getName() {
		return getCastedPropertyValue(PROP_NAME);
	}
	
	/**
	 * Convenience getter to access the <i>name</i> property.
	 * 
	 * @return The value of the <i>name</i> property.
	 */
	public final synchronized String getToolTipText() {
		return getCastedPropertyValue(PROP_TOOLTIP);
	}

	/**
	 * Returns the primary process variable address.
	 * 
	 * @return the primary process variable address
	 */
	public final synchronized String getPrimaryPV() {
		return getCastedPropertyValue(PROP_PRIMARY_PV);
	}

	/**
	 * Sets the primary process variable address.
	 * 
	 * @param rawName
	 *            the raw name of the primary process variable address (can
	 *            contain aliases)
	 */
	public synchronized void setPrimarPv(final String rawName) {
		setPropertyValue(PROP_PRIMARY_PV, rawName);
	}

	/**
	 * Add an incoming or outgoing connection to this variable. This method
	 * should stay package protected and will be called from the Connection
	 * class only.
	 * 
	 * @param conn
	 *            a non-null connection instance
	 */
	final synchronized void addConnection(final ConnectionElement conn) {
		if ((conn == null) || (conn.getSourceModel() == conn.getTargetModel())) {
			throw new IllegalArgumentException();
		}
		if (conn.getSourceModel() == this) {
			_sourceConnections.add(conn);
			firePropertyChangeEvent(PROP_CONNECTIONS_SOURCE, null, conn);
		} else if (conn.getTargetModel() == this) {
			_targetConnections.add(conn);
			firePropertyChangeEvent(PROP_CONNECTIONS_TARGET, null, conn);
		}
	}

	/**
	 * Remove an incoming or outgoing connection from this variable. This method
	 * should stay package protected and will be called from the Connection
	 * class only.
	 * 
	 * @param conn
	 *            a non-null connection instance
	 */
	protected final synchronized void removeConnection(
			final ConnectionElement conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		if (conn.getSourceModel() == this) {
			_sourceConnections.remove(conn);
			firePropertyChangeEvent(PROP_CONNECTIONS_SOURCE, null, conn);
		} else if (conn.getTargetModel() == this) {
			_targetConnections.remove(conn);
			firePropertyChangeEvent(PROP_CONNECTIONS_TARGET, null, conn);
		}
	}

	/**
	 * Returns a list of outgoing connections.
	 * 
	 * @return a non-null List instance, the list may be empty
	 */
	public final synchronized List<ConnectionElement> getSourceConnections() {
		return new ArrayList<ConnectionElement>(_sourceConnections);
	}

	/**
	 * Returns a list of incoming connections.
	 * 
	 * @return a non-null List instance, the list may be empty
	 */
	public final synchronized List<ConnectionElement> getTargetConnections() {
		return new ArrayList<ConnectionElement>(_targetConnections);
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
	private <TYPE> TYPE getCastedPropertyValue(final String propertyName) {
		return (TYPE) getProperty(propertyName).getPropertyValue();
	}

	/**
	 * Convenience setter for the size of this widget model.
	 * 
	 * @param width
	 *            The width.
	 * @param height
	 *            The height.
	 */
	public synchronized void setSize(final int width, final int height) {
		setWidth(width);
		setHeight(height);
	}

	/**
	 * Convenience setter for the location of this widget model.
	 * 
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 */
	public synchronized void setLocation(final int x, final int y) {
		setX(x);
		setY(y);
	}

	/**
	 * Configure the properties of this widget model. Use
	 * {@link #addProperty(String, WidgetProperty)} to add a property to the
	 * model. Any property type in package org.csstudio.sds.model.properties can
	 * be used for the configuration of you own models.
	 * 
	 * A typical implementation looks like this:
	 * 
	 * <pre>
	 * configureProperties() {
	 * 	addProperty(PROP_FILL, new DoubleProperty(Messages.FillLevelProperty,
	 * 			WidgetPropertyCategory.Behaviour, DEFAULT_FILL, 0.0, 100.0));
	 * }
	 * </pre>
	 */
	protected abstract void configureProperties();

	/**
	 * Is called to mark several basic {@link WidgetProperty}s as invisible.
	 * Invisible Properties are not shown in the PropertyView
	 */
	protected final void markBasePropertiesAsInvisible() {
		markPropertyAsInvisible(PROP_LAYER);
	}
	
	/**
	 * Is called to mark several {@link WidgetProperty}s as invisible.
	 * Invisible Properties are not shown in the PropertyView
	 */
	protected void markPropertiesAsInvisible() {
	}

	/**
	 * Return the type ID of this widget model.
	 * 
	 * @return The type ID of this widget model.
	 */
	public abstract String getTypeID();

	/**
	 * Returns true, if this widget is visible. Note: This setting will only
	 * apply, when a display is connected mode.
	 * 
	 * @return true, if this widget is visible, false otherwise
	 */
	public final synchronized boolean isVisible() {
		return getCastedPropertyValue(PROP_VISIBILITY);
	}

	/**
	 * Sets the visibility state of this widget.
	 * 
	 * @param visible
	 *            true, if this widget should be visible during connected mode
	 *            or false otherwise
	 */
	public final synchronized void setVisible(final boolean visible) {
		setPropertyValue(PROP_VISIBILITY, visible);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public final Object getAdapter(final Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * Gets the width of the border.
	 * 
	 * @return the border width
	 */
	public final int getBorderWidth() {
		return (Integer) getProperty(PROP_BORDER_WIDTH).getPropertyValue();
	}

	/**
	 * Gets the color of the border.
	 * 
	 * @return the border color
	 */
	public final RGB getBorderColor() {
		return (RGB) getProperty(PROP_BORDER_COLOR).getPropertyValue();
	}

	/**
	 * Gets the style of the border.
	 * 
	 * @return the border style
	 */
	public final int getBorderStyle() {
		return (Integer) getProperty(PROP_BORDER_STYLE).getPropertyValue();
	}

	/**
	 * Add a property change listener.
	 * 
	 * @param listener
	 *            The property change listener that is to be added.
	 */
	public final void addPropertyChangeListener(
			final PropertyChangeListener listener) {
		_propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Remove a property change listener.
	 * 
	 * @param listener
	 *            The property change listener that is to be removed.
	 */
	public final void removePropertyChangeListener(
			final PropertyChangeListener listener) {
		_propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public StateMemento getStateMemento() {
		Map<String, Object> currentPropertyValues = new HashMap<String, Object>();

		for (String key : _propertyMap.keySet()) {
			currentPropertyValues.put(key, _propertyMap.get(key)
					.getPropertyValue());
		}

		return new StateMemento(currentPropertyValues);
	}

	public void restoreState(StateMemento memento) {
		assert memento != null;

		for (String key : memento.getPropertyValues().keySet()) {
			setPropertyValue(key, memento.getPropertyValues().get(key));
		}
	}

	/**
	 * Notify all registered property change listeners.
	 * 
	 * @param propertyName
	 *            ID of the property that has changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 */
	protected final void firePropertyChangeEvent(final String propertyName,
			final Object oldValue, final Object newValue) {
		PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName,
				oldValue, newValue);
		_propertyChangeSupport.firePropertyChange(evt);
	}
	
	/**
	 * Notify all registered property change listeners.
	 * 
	 * @param propertyName
	 *            ID of the property that has changed.
	 * @param newValue
	 *            The new value of the property.
	 * @param customization
	 *            The custom value of the property.
	 */
	protected final void fireCustomPropertyChangeEvent(final String propertyName, final Object newValue, final Object customization) {
		CustomPropertyChangeEvent evt = new CustomPropertyChangeEvent(this, propertyName,
				null, newValue, customization);
		_propertyChangeSupport.firePropertyChange(evt);
	}

	public ContainerModel getParent() {
		return _parent;
	}

	public void setParent(ContainerModel parent) {
		_parent = parent;
	}

	public IProcessVariableAddress getMainPvAdress() {
		IProcessVariableAddress pv = null;

		Map<String, String> aliases = getAllInheritedAliases();

		try {
			String channelName = ChannelReferenceValidationUtil
					.createCanonicalName(getPrimaryPV(), aliases);

			pv = ProcessVariableAdressFactory.getInstance()
					.createProcessVariableAdress(channelName);

		} catch (ChannelReferenceValidationException e) {
		}

		return pv;
	}

	public List<IProcessVariableAddress> getAllPvAdresses() {
		Set<IProcessVariableAddress> result = new HashSet<IProcessVariableAddress>();

		Map<String, String> aliases = getAllInheritedAliases();

		// add the main pv
		IProcessVariableAddress mainPv = getMainPvAdress();
		if (mainPv != null) {
			result.add(mainPv);
		}
		
		// collect pvs from dynamic descriptors
		for (WidgetProperty wp : _propertyMap.values()) {
			DynamicsDescriptor dd = wp.getDynamicsDescriptor();

			if (dd != null) {
				for (ParameterDescriptor pd : dd.getInputChannels()) {

					try {
						String channelName = ChannelReferenceValidationUtil
								.createCanonicalName(pd.getChannel(), aliases);

						IProcessVariableAddress pv = ProcessVariableAdressFactory
								.getInstance().createProcessVariableAdress(
										channelName);

						if (pv != null) {
							result.add(pv);
						}
					} catch (ChannelReferenceValidationException e) {
					}

				}
			}
		}

		return new ArrayList<IProcessVariableAddress>(result);
	}

}
