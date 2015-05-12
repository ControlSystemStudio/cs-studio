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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.sds.cursorservice.AbstractCursor;
import org.csstudio.sds.cursorservice.CursorService;
import org.csstudio.sds.internal.model.ActionDataProperty;
import org.csstudio.sds.internal.model.ArrayOptionProperty;
import org.csstudio.sds.internal.model.BehaviorProperty;
import org.csstudio.sds.internal.model.BooleanProperty;
import org.csstudio.sds.internal.model.ColorProperty;
import org.csstudio.sds.internal.model.DoubleArrayProperty;
import org.csstudio.sds.internal.model.DoubleProperty;
import org.csstudio.sds.internal.model.FontProperty;
import org.csstudio.sds.internal.model.IntegerProperty;
import org.csstudio.sds.internal.model.OptionProperty;
import org.csstudio.sds.internal.model.PointlistProperty;
import org.csstudio.sds.internal.model.ResourceProperty;
import org.csstudio.sds.internal.model.StringMapProperty;
import org.csstudio.sds.internal.model.StringProperty;
import org.csstudio.sds.internal.model.TooltipProperty;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.FontData;

/**
 * An abstract base class for all widget models.
 *
 * @author Alexander Will, Sven Wende, Stefan Hofer, Kai Meyer, Xihui Chen
 * @version $Revision: 1.130 $
 *
 */
public abstract class AbstractWidgetModel implements IAdaptable {

    /**
     * ID for the <i>connection state</i> of the model.
     */
    public static final String PROP_LIVE = "PROP_LIVE"; //$NON-NLS-1$

    /**
     * Property ID for behavior.
     */
    public static final String PROP_BEHAVIOR = "behavior"; //$NON-NLS-1$

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
     * The ID for the permission id property.
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
     * The ID of the connected property.
     */
    public static final String PROP_CONNECTED = "connected"; //$NON-NLS-1$

    /**
     * The ID of the rotation property. Is only used if the widget is rotatable.
     *
     * @see AbstractWidgetModel#isRotatable()
     */
    public static final String PROP_ROTATION = "rotation"; //$NON-NLS-1$

    public static final String PROP_ACCESS_GRANTED = "PROP_ACCESS_GRANTED"; //$NON-NLS-1$
    public static final String PROP_WRITE_ACCESS_GRANTED = "PROP_WRITE_ACCESS_GRANTED"; //$NON-NLS-1$

    public static final String PROP_CROSSED_OUT = "PROP_CROSSED_OUT";

    public static final String PROP_RHOMBUS = "PROP_RHOMBUS";

    /**
     * A boolean, representing if this Model is alive.
     */
    private boolean _live;

    /**
     * Property event handler.
     */
    private final PropertyChangeSupport _propertyChangeSupport;

    /**
     * This map holds all the properties of the widget model.
     */
    private final Map<String, WidgetProperty> _propertyMap;

    private final List<WidgetProperty> _properties = new ArrayList<WidgetProperty>();

    /**
     * This map holds all the properties of the widget model.
     */
    private final Map<String, WidgetProperty> _tempRemovedPropertyMap;

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

    private static final boolean DEFAULT_ACCESS_GRANTED = true;

    /**
     * The parent model of this model.
     */
    private ContainerModel _parent;

    /**
     * Holds if this widget is rotatable.
     */
    private final boolean _isRotatable;

    /**
     * Holds if this widget crossed out.
     */
    private boolean _crossed;

    private boolean _rhombus;

    private boolean grantAccessCall;

    /**
     * Keeps the internal property state, when {@link #saveState()} and
     * {@link #restoreState()} are used.
     */
    private Map<String, Object> state;

    private Class _javaType;

    private final List<AbstractCursor> _cursorDescriptors;

    public AbstractWidgetModel() {
        this(false);
    }

    /**
     * Standard constructor. Creates a not rotatable widget
     */
    public AbstractWidgetModel(final boolean isRotatable) {
        this(isRotatable, CursorService.getInstance().availableCursors());
    }

    /**
     * Standard constructor.
     *
     * @param isRotatable
     *            true if this widget is rotatable
     */
    public AbstractWidgetModel(final boolean isRotatable, final List<AbstractCursor> cursorDescriptors) {
        _cursorDescriptors = cursorDescriptors;
        _propertyChangeSupport = new PropertyChangeSupport(this);
        _propertyMap = new LinkedHashMap<String, WidgetProperty>();
        _tempRemovedPropertyMap = new HashMap<String, WidgetProperty>();
        _isRotatable = isRotatable;
        setJavaType(Double.class);
        configureBaseProperties(_isRotatable);
        configureProperties();
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
     *
     * @param isRotatable
     *            If the widget is rotateable, than a rotation-property is added
     */
    private void configureBaseProperties(final boolean isRotatable) {
        // .. behavior
        addBehaviorProperty(PROP_BEHAVIOR, "Behavior", WidgetPropertyCategory.CONNECTION, "");
        // primary pv
        addStringProperty(PROP_PRIMARY_PV, "Primary PV", WidgetPropertyCategory.CONNECTION, "", false);
        // Name
        addStringProperty(PROP_NAME, "Name", WidgetPropertyCategory.BEHAVIOR, this.getDefaultName(), false);
        // .. Aliases
        addStringMapProperty(PROP_ALIASES, "Alias", WidgetPropertyCategory.CONNECTION, new HashMap<String, String>(), false);
        // .. Actions
        addActionDataProperty(PROP_ACTIONDATA, "Action Data", WidgetPropertyCategory.ACTIONS, new ActionData());
        // positions
        addIntegerProperty(PROP_POS_X, "X-Coordinate", WidgetPropertyCategory.POSITION, DEFAULT_X, false);
        addIntegerProperty(PROP_POS_Y, "Y-Coordinate", WidgetPropertyCategory.POSITION, DEFAULT_Y, false);
        addIntegerProperty(PROP_WIDTH, "Width", WidgetPropertyCategory.POSITION, DEFAULT_WIDTH, 1, Integer.MAX_VALUE, false);
        addIntegerProperty(PROP_HEIGHT, "Height", WidgetPropertyCategory.POSITION, DEFAULT_HEIGHT, 1, Integer.MAX_VALUE, false);
        // colors
        addColorProperty(PROP_COLOR_FOREGROUND, "Foreground Color", WidgetPropertyCategory.FORMAT, "#C86464", false);
        addColorProperty(PROP_COLOR_BACKGROUND, "Background Color", WidgetPropertyCategory.FORMAT, "#F0F0F0", false);
        // visibility
        addBooleanProperty(PROP_VISIBILITY, "Visibility", WidgetPropertyCategory.BEHAVIOR, true, false);

        addArrayOptionProperty(PROP_BORDER_STYLE, "Border Style", WidgetPropertyCategory.BORDER, BorderStyleEnum.getDisplayNames(),
                BorderStyleEnum.LINE.getIndex(), false);
        addIntegerProperty(PROP_BORDER_WIDTH, "Border Width", WidgetPropertyCategory.BORDER, 0, 0, Integer.MAX_VALUE, false);
        addColorProperty(PROP_BORDER_COLOR, "Border Color", WidgetPropertyCategory.BORDER, "#640000", false);

        addBooleanProperty(PROP_ENABLED, "Enabled", WidgetPropertyCategory.BEHAVIOR, DEFAULT_ENABLED, false);
        addStringProperty(PROP_PERMISSSION_ID, "Permission ID", WidgetPropertyCategory.BEHAVIOR, "", false);
        // Cursor
        final List<AbstractCursor> cursorDescriptors = getCursorDescriptors();
        addOptionProperty(PROP_CURSOR, "Cursor", WidgetPropertyCategory.BEHAVIOR,
                          cursorDescriptors.toArray(new IOption[cursorDescriptors.size()]), "cursor.default", false);
        // Rotation
        if (isRotatable) {
            addDoubleProperty(PROP_ROTATION, "Rotation Angle", WidgetPropertyCategory.DISPLAY, 0, 0, 360, false);
        }
        addTooltipProperty(PROP_TOOLTIP, "ToolTip", WidgetPropertyCategory.DISPLAY, this.getDefaultToolTip());
        addBooleanProperty(PROP_CROSSED_OUT, "Crossed Out", WidgetPropertyCategory.BEHAVIOR, false, false);
        addBooleanProperty(PROP_RHOMBUS, "Rhombus Style", WidgetPropertyCategory.BEHAVIOR, false, false);
        // .. some internal properties
        addStringProperty(PROP_LAYER, "Layer", WidgetPropertyCategory.POSITION, DEFAULT_LAYER, false);
        addBooleanProperty(PROP_ACCESS_GRANTED, "Access granted", WidgetPropertyCategory.BEHAVIOR, DEFAULT_ACCESS_GRANTED, false);
        addBooleanProperty(PROP_WRITE_ACCESS_GRANTED, "Write Access granted", WidgetPropertyCategory.BEHAVIOR, DEFAULT_ACCESS_GRANTED, false);
        // .. hide internal properties
        hideProperty(PROP_LAYER, getTypeID());
        hideProperty(PROP_ACCESS_GRANTED, getTypeID());
        hideProperty(PROP_WRITE_ACCESS_GRANTED, getTypeID());
    }

    /**
     * @return
     */
    private List<AbstractCursor> getCursorDescriptors() {
        return _cursorDescriptors;
    }

    /**
     * Returns the default tooltip for this model.
     *
     * @return the tooltip text
     */
    protected String getDefaultToolTip() {
        return this.createTooltipParameter(PROP_ALIASES);
    }

    /**
     * Returns the given parameter encapsulated by '${' and '}$'.
     *
     * @param propertyName
     *            The name of the parameter
     *
     */
    protected final String createTooltipParameter(final String propertyName) {
        return "${" + propertyName + "}";
    }

    /**
     * Hides the specified property. Visibility may depend on other properties
     * which is indicated by the master id. A property is only visible (in the
     * property view), if its master references are empty. This method adds the
     * specified master id from the internal reference list.
     *
     * @param propertyID
     *            the property id
     * @param masterId
     *            the id of the master
     */
    public final void hideProperty(final String propertyID, final String masterId) {
        final WidgetProperty property = getPropertyInternal(propertyID);
        if (property != null) {
            property.hide(masterId);
        }
    }

    /**
     * Shows the specified property. Visibility may depend on other properties
     * which is indicated by the master id. A property is only visible (in the
     * property view), if its master references are empty. This method removes
     * the specified master id from the internal reference list.
     *
     * @param propertyID
     *            the property id
     * @param masterId
     *            the id of the master
     */
    public final void showProperty(final String propertyID, final String masterId) {
        final WidgetProperty property = getPropertyInternal(propertyID);
        if (property != null) {
            property.show(masterId);
        }
    }

    /**
     * Sets the connection state of this model.
     *
     * @param isLive
     *            true if this model is connected to a control system, false
     *            otherwise
     */
    public void setLive(final boolean isLive) {
        if (_live != isLive) {
            _live = isLive;
            firePropertyChangeEvent(PROP_LIVE, !_live, _live);
        }
    }

    /**
     * Returns true if this model is connected to a control system.
     *
     * @return true if this model is connected to a control system
     */
    public boolean isLive() {
        return _live;
    }

    /**
     * Returns true if this widget can be rotated.
     *
     * @return true if this widget can be rotated
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
        return getIntegerProperty(PROP_HEIGHT);
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
     *
     * @return The {@link ActionData}
     */
    public ActionData getActionData() {
        final ActionData result = new ActionData();
        final ActionData ownData = getActionDataProperty(PROP_ACTIONDATA);
        for (final AbstractWidgetActionModel action : ownData.getWidgetActions()) {
            result.addAction(action);
        }
        if (this.getParent() != null) {
            final ActionData parentData = this.getParent().getActionData();
            for (final AbstractWidgetActionModel action : parentData.getWidgetActions()) {
                result.addAction(action);
            }
        }
        return result;
    }

    public final boolean isConnectedToControlSystem() {
        return getBooleanProperty(PROP_CONNECTED);
    }

    /**
     * Returns the permission id.
     *
     * @return String The permission id
     */
    public final String getPermissionID() {
        return getStringProperty(PROP_PERMISSSION_ID);
    }

    public final boolean isEnable() {
        return getBooleanProperty(PROP_ENABLED);
    }

    public final boolean isWriteAccessAllowed() {
        return getBooleanProperty(PROP_WRITE_ACCESS_GRANTED);
    }

    public final boolean isAccessGranted() {
        return getBooleanProperty(PROP_ACCESS_GRANTED);
    }

    /**
     * Returns the enable state independent of any parent states.
     *
     * @return boolean the enable state independent of any parent states
     */
    public final boolean isAccesible() {
        return getBooleanProperty(PROP_ACCESS_GRANTED) && isEnable() && (isWriteAccessAllowed() || !hasOutputChannel());
    }

    /**
     * Returns true if the widget and all of its recursive parents are enabled.
     *
     * @return boolean true if the widget and all of its recursive parents are
     *         enabled
     */
    public final boolean isEnabledRecursive() {
        final boolean result = isAccesible() && (getParent() != null ? getParent().isAccesible() : true);
        return result;
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

    public void grantAccess(final boolean permission) {
        grantAccessCall = true;
        setPropertyValue(PROP_ACCESS_GRANTED, permission);
        grantAccessCall = false;
    }

    /**
     * Returns the name of the layer.
     *
     * @return String The name of the layer
     */
    public final String getLayer() {
        return getStringProperty(PROP_LAYER);
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

    public void setCursorId(final String cursorId) {
        assert cursorId != null;
//        AbstractCursor findCursor = CursorService.getInstance().findCursor(cursorId);
//        int index = CursorService.getInstance().availableCursors().indexOf(findCursor);
//        setPropertyValue(PROP_CURSOR, index);
        setPropertyValue(PROP_CURSOR, cursorId);
    }

    public final String getCursorId() {
        return getOptionProperty(PROP_CURSOR);
//        List<AbstractCursor> cursorDescriptors = CursorService.getInstance().availableCursors();
//        AbstractCursor abstractCursor = cursorDescriptors.get(getArrayOptionProperty(PROP_CURSOR));
//        return abstractCursor.getIdentifier();
    }

    /**
     * Returns the rotation angle for this widget. Returns 0 if this widget is
     * not rotatable
     *
     * @return The rotation angle
     */
    public final double getRotationAngle() {
        if (this.isRotatable()) {
            return getDoubleProperty(PROP_ROTATION);
        }
        return 0.0;
    }

    /**
     * Sets the rotation angle for this widget, only when this widget is
     * rotatable.
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

    public List<WidgetProperty> getProperties() {
        return _properties;
    }

    /**
     * Returns the IDs of all visible properties.
     *
     * @return the IDs of all visible properties
     */
    public final synchronized Set<String> getVisiblePropertyIds() {
        final Set<String> result = new LinkedHashSet<String>();

        for (final WidgetProperty property : _properties) {
            if (property.isVisible()) {
                result.add(property.getId());
            }
        }
        return result;
    }

    /**
     * Returns the property object for the specified property identifier.
     * Important: This method should not be called outside the SDS core. This
     * method might not exists in future releases. Please use the typed
     * get-methods (like {@link #getStringProperty(String)}) to access property
     * values.
     *
     * @param name
     *            the property identifier
     *
     * @return the property for the specified identifier
     */
    public final synchronized WidgetProperty getPropertyInternal(final String name) {
        assert name != null;
        assert hasProperty(name);
        return _propertyMap.get(name);
    }

    public final synchronized String getStringProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if (property != null && property instanceof StringProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No String property [" + propertyId + "] registered.");
    }

    public final synchronized ActionData getActionDataProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if (property != null && property instanceof ActionDataProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No ActionData property [" + propertyId + "] registered.");
    }

    public final synchronized int getIntegerProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if (property != null && property instanceof IntegerProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No Integer property [" + propertyId + "] registered.");
    }

    public synchronized double getDoubleProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if ( property != null && property instanceof DoubleProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No Double property [" + propertyId + "] registered.");
    }

    public final synchronized String getTooltipProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if ( property != null && property instanceof TooltipProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No Tooltip property [" + propertyId + "] registered.");
    }

    public synchronized boolean getBooleanProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if ( property != null && property instanceof BooleanProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No Boolean property [" + propertyId + "] registered.");
    }

    public final synchronized int getArrayOptionProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if ( property != null && property instanceof ArrayOptionProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No ArrayOption property [" + propertyId
                + "] registered.");
    }

    public final synchronized Map<String, String> getStringMapProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if ( property != null && property instanceof StringMapProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No StringMap property [" + propertyId + "] registered.");
    }

    public final synchronized String getOptionProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if ( property != null && property instanceof OptionProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No Option property [" + propertyId + "] registered.");
    }

    public final synchronized IPath getResourceProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if ( property != null && property instanceof ResourceProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No Resource property [" + propertyId + "] registered.");
    }

    public final synchronized PointList getPointlistProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if ( property != null && property instanceof PointlistProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No Tooltip property [" + propertyId + "] registered.");
    }

    public final synchronized double[] getDoubleArrayProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if ( property != null && property instanceof DoubleArrayProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No Tooltip property [" + propertyId + "] registered.");
    }

    public final synchronized String getBehaviorProperty(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if ( property != null && property instanceof BehaviorProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No Tooltip property [" + propertyId + "] registered.");
    }

    public final void addArrayOptionProperty(final String id, final String description,
                                             final WidgetPropertyCategory category,
                                             final String[] options, final int defaultValue,
                                             final boolean before, final String... relative) {
        final ArrayOptionProperty property = new ArrayOptionProperty(description, category, options, defaultValue);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    public final void addStringMapProperty(final String id, final String description,
                                           final WidgetPropertyCategory category,
                                           final Map<String, String> defaultValue,
                                           final boolean before, final String... relative) {
        final StringMapProperty property = new StringMapProperty(description, category, defaultValue);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    public final void addOptionProperty(final String id, final String description,
                                        final WidgetPropertyCategory category,
                                        final IOption[] options, final String defaultValue,
                                        final boolean before, final String... relative) {
        final OptionProperty property = new OptionProperty(description, category, options, defaultValue);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    public final void addResourceProperty(final String id, final String description,
                                          final WidgetPropertyCategory category,
                                          final IPath defaultValue, final String[] fileExtensions,
                                          final boolean before, final String... relative) {
        final ResourceProperty property = new ResourceProperty(description, category, defaultValue, fileExtensions);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    public final void addPointlistProperty(final String id, final String description,
                                           final WidgetPropertyCategory category,
                                           final PointList defaultValue,
                                           final boolean before, final String... relative) {
        final PointlistProperty property = new PointlistProperty(description, category, defaultValue);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    public final void addDoubleArrayProperty(final String id, final String description,
                                             final WidgetPropertyCategory category,
                                             final double[] defaultValue,
                                             final boolean before, final String... relative) {
        final DoubleArrayProperty property = new DoubleArrayProperty(description, category, defaultValue);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    private void addBehaviorProperty(final String id, final String description, final WidgetPropertyCategory category, final String defaultValue, final String... after) {
        final BehaviorProperty property = new BehaviorProperty(description, category, defaultValue);
        property.setId(id);
        doAddProperty(property, false, after);
    }

    private void addTooltipProperty(final String id, final String description, final WidgetPropertyCategory category, final String defaultValue, final String... after) {
        final TooltipProperty property = new TooltipProperty(description, category, defaultValue, this);
        property.setId(id);
        doAddProperty(property, false, after);
    }

    private void addActionDataProperty(final String id, final String description, final WidgetPropertyCategory category, final ActionData defaultValue,
            final String... after) {
        final ActionDataProperty property = new ActionDataProperty(description, category, defaultValue);
        property.setId(id);
        doAddProperty(property, false, after);
    }

    public final void addBooleanProperty(final String id, final String description,
                                         final WidgetPropertyCategory category,
                                         final boolean defaultValue,
                                         final boolean before, final String... relative) {
        final BooleanProperty property = new BooleanProperty(description, category, defaultValue);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    public final void addIntegerProperty(final String id, final String description,
                                         final WidgetPropertyCategory category,
                                         final int defaultValue, final int min, final int max,
                                         final boolean before, final String... relative) {
        final IntegerProperty property = new IntegerProperty(description, category, defaultValue, min, max);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    public final void addIntegerProperty(final String id, final String description,
                                         final WidgetPropertyCategory category,
                                         final int defaultValue,
                                         final boolean before, final String... relative) {
        addIntegerProperty(id, description, category, defaultValue, -Integer.MAX_VALUE, Integer.MAX_VALUE, before, relative);
    }

    public final void addDoubleProperty(final String id, final String description,
                                        final String longDescription,
                                        final WidgetPropertyCategory category,
                                        final double defaultValue,
                                        final double min, final double max,
                                        final boolean before, final String... relative) {
        final DoubleProperty property = new DoubleProperty(description, longDescription, category, defaultValue, min, max);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    public final void addDoubleProperty(final String id, final String description,
                                        final WidgetPropertyCategory category,
                                        final double defaultValue, final double min,
                                        final double max,
                                        final boolean before, final String... relative) {
        addDoubleProperty(id, description, null, category, defaultValue, min, max, before, relative);
    }

    public final void addDoubleProperty(final String id, final String description, final WidgetPropertyCategory category, final double defaultValue, final boolean before, final String... relative) {
        addDoubleProperty(id, description, null, category, defaultValue, -Double.MAX_VALUE, Double.MAX_VALUE, before, relative);
    }

    public final void addStringProperty(final String id, final String description, final WidgetPropertyCategory category, final String defaultValue, final boolean before, final String... relative) {
        final StringProperty property = new StringProperty(description, category, defaultValue);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    public final void addColorProperty(final String id, final String description, final WidgetPropertyCategory category, final String defaultValue, final boolean before, final String... relative) {
        final ColorProperty property = new ColorProperty(description, category, defaultValue);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    public final void addFontProperty(final String id, final String description,
                                      final WidgetPropertyCategory category,
                                      final String defaultValue, final boolean before,
                                      final String... relative) {
        final FontProperty property = new FontProperty(description, category, defaultValue);
        property.setId(id);
        doAddProperty(property, before, relative);
    }

    private void doAddProperty(final WidgetProperty property, final boolean before, final String... relative) {
        // .. determine insertation index

        int index = _properties.size();

        if ( relative != null && relative.length > 0) {
            int pos = 0;
            if (!before) {
              pos=1;
            }
            for (final String pId : relative) {
                final WidgetProperty p = _propertyMap.get(pId);

                if ( p != null && _properties.contains(p)) {
                    index = _properties.indexOf(p) + pos;
                }
            }
        }
        property.setWidgetModel(this);
        _propertyMap.put(property.getId(), property);
        _properties.add(index, property);

        assert property.getWidgetModel() == this;
        assert property.getId() != null;
        assert _propertyMap.containsKey(property.getId());
        assert _propertyMap.get(property.getId()) == property;
        assert _properties.contains(property);
    }

    public String getColor(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if (property != null && property instanceof ColorProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No color property [" + propertyId + "] registered.");
    }

    public final boolean isCrossedOut() {
        return getBooleanProperty(PROP_CROSSED_OUT);
    }
    /**
     * Set is this model corssed out or not.
     *
     * @param crossed
     *            true if this model crossed out,<br>
     *            false otherwise
     */
    public void setCrossedOut(final boolean crossed) {
        if (_crossed != crossed) {
            _crossed = crossed;
            firePropertyChangeEvent(PROP_CROSSED_OUT, !_crossed, _crossed);
        }
    }
    public final boolean isRhombus() {
        return getBooleanProperty(PROP_RHOMBUS);
    }
    /**
     * Set is this model corssed out or not.
     *
     * @param rhombus
     *            true if this model crossed out,<br>
     *            false otherwise
     */
    public void setRhombsus(final boolean rhombus) {
        if (_rhombus != rhombus) {
            _rhombus = rhombus;
            firePropertyChangeEvent(PROP_RHOMBUS, !_rhombus, _rhombus);
        }
    }

    public void setColor(final String propertyId, final String hexOrVariable) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if (property != null && property instanceof ColorProperty) {
            property.setPropertyValue(hexOrVariable);
        } else {
            throw new IllegalArgumentException("No color property [" + propertyId + "] registered.");
        }
    }

    public final String getFont(final String propertyId) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if (property != null && property instanceof FontProperty) {
            return property.getPropertyValue();
        }
        throw new IllegalArgumentException("No font property [" + propertyId + "] registered.");
    }

    public final void setFont(final String propertyId, final FontData font) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if (property != null && property instanceof FontProperty) {
            property.setPropertyValue(font);
        } else {
            throw new IllegalArgumentException("No font property [" + propertyId + "] registered.");
        }
    }

    /**
     * Return the widget model property with the given ID.
     *
     * @param name
     *            The ID of the property.
     * @return The widget model property with the given ID.
     */
    public final synchronized WidgetProperty getTempRemovedProperty(final String name) {
        assert name != null;
        assert hasTempRemovedProperty(name);

        return _tempRemovedPropertyMap.get(name);
    }

    /**
     * Return whether the widget model has a property with the given ID.
     *
     * @param name
     *            The ID of the property.
     * @return True, if the widget model has a property with the given ID.
     */
    public final synchronized boolean hasTempRemovedProperty(final String name) {
        boolean result = false;
        if (_propertyMap.containsKey(name)) {
            result = true;
        }

        return result;
    }

    /**
     * Set the value of a widget property.
     *
     * @param propertyID
     *            the property id
     * @param value
     *            the value
     */
    public synchronized void setPropertyValue(final String propertyID, final Object value) {
        final WidgetProperty property = _propertyMap.get(propertyID);
        if (property != null) {
            if (!PROP_ACCESS_GRANTED.equals(propertyID) || grantAccessCall) {
                // ignore setting of PROP_ACCESS_GRANTED if the property was
                // loaded from storage
                property.setPropertyValue(value);
            }
        }
    }

    /**
     * Set the manual value of a widget property.
     *
     * @param propertyID
     *            the property id
     * @param manualValue
     *            the value
     */
    public synchronized void setPropertyManualValue(final String propertyID, final Object manualValue) {
        final WidgetProperty property = _propertyMap.get(propertyID);
        if (property != null) {
            if (!PROP_ACCESS_GRANTED.equals(propertyID) || grantAccessCall) {
                // ignore setting of PROP_ACCESS_GRANTED if the property was
                // loaded from storage
                property.setManualValue(manualValue);
            }
        }
    }

    /**
     * Set the description of an widget property.
     *
     * @param propertyID
     *            The ID of the property.
     * @param description
     *            The new description of the property.
     */
    public synchronized void setPropertyDescription(final String propertyID, final String description) {
        final WidgetProperty property = _propertyMap.get(propertyID);

        if (property != null) {
            property.setDescription(description);
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
    public final synchronized void addAlias(final String name, final String value) {
        assert name != null;
        assert value != null;

        final Map<String, String> aliases = new HashMap<String, String>();
        for (final String key : getAliases().keySet()) {
            aliases.put(key, getAliases().get(key));
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
        if (this.getAliases().remove(name) != null) {
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
    public Map<String, String> getAliases() {
        return getStringMapProperty(PROP_ALIASES);
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

        final Map<String, String> result = new HashMap<String, String>();

        if (getParent() != null) {
            // get aliases from parent
            final Map<String, String> inheritedAliases = getParent().getAllInheritedAliases();

            // put them into the result list first
            result.putAll(inheritedAliases);

            // put our own aliases at last
            result.putAll(getAliases());
        } else {
            final Map<String, String> myAliases = getAliases();

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
    public final synchronized void setDynamicsDescriptor(final String propertyID, final DynamicsDescriptor dynamicsDescriptor) {
        final WidgetProperty p = _propertyMap.get(propertyID);

        if (p != null) {
            p.setDynamicsDescriptor(dynamicsDescriptor);

            // important, send a change event for the corresponding property
            // together with the current value of the property - do never send
            // the dynamics descriptor itself as value, because it is not
            // compatible to the expected data type for that property
            final Object propertyValue = getPropertyInternal(propertyID).getPropertyValue();
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
    public final synchronized DynamicsDescriptor getDynamicsDescriptor(final String propertyID) {
        DynamicsDescriptor result = null;

        final WidgetProperty p = _propertyMap.get(propertyID);

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
        return getIntegerProperty(PROP_WIDTH);
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
        return getIntegerProperty(PROP_POS_X);
    }

    /**
     * Convenience getter to access the <i>X coordinate</i> property relative to
     * the given ancestor.
     *
     * @param ancestor
     *            An ancestor of this widget
     * @return The value of the <i>X coordinate</i> property.
     */
    public final synchronized int getXForAncestor(final ContainerModel ancestor) {
        assert isAncestorReachable(ancestor) : "Ancestor (" + ancestor + ") is not reachable from " + this;
        int result = getIntegerProperty(PROP_POS_X);
        ContainerModel parent = this.getParent();
        while (parent != null && !parent.equals(ancestor)) {
            result = result + parent.getX();
            parent = parent.getParent();
        }
        return result;
    }

    /**
     * Convenience getter to access the <i>Y coordinate</i> property relative to
     * the given ancestor.
     *
     * @param ancestor
     *            An ancestor of this widget
     * @return The value of the <i>Y coordinate</i> property.
     */
    public final synchronized int getYForAncestor(final ContainerModel ancestor) {
        assert isAncestorReachable(ancestor) : "Ancestor (" + ancestor + ") is not reachable from " + this;
        int result = getIntegerProperty(PROP_POS_Y);
        ContainerModel parent = this.getParent();
        while (parent != null && !parent.equals(ancestor)) {
            result = result + parent.getY();
            parent = parent.getParent();
        }
        return result;
    }

    /**
     * Determines if the given {@link ContainerModel} is an ancestor of this
     * model.
     *
     * @param ancestor
     *            The probably ancestor
     * @return true, if the given {@link ContainerModel} is an ancestor of this
     *         model, false otherwise
     */
    private boolean isAncestorReachable(final ContainerModel ancestor) {
        ContainerModel parent = this.getParent();
        while (parent != null) {
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
        return getIntegerProperty(PROP_POS_Y);
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
        return getStringProperty(PROP_NAME);
    }

    /**
     * Convenience getter to access the <i>name</i> property.
     *
     * @return The value of the <i>name</i> property.
     */
    public final synchronized String getToolTipText() {
        return getTooltipProperty(PROP_TOOLTIP);
    }

    /**
     * Returns the primary process variable address.
     *
     * @return the primary process variable address
     */
    public final synchronized String getPrimaryPV() {
        return getStringProperty(PROP_PRIMARY_PV);
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
     * Sets the behaviour id.
     *
     * @param behaviourId the behaviour id
     */
    public synchronized void setBehavior(final String behaviourId) {
        setPropertyValue(PROP_BEHAVIOR, behaviourId);
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
     * Configure the properties of this widget model.
     */
    protected abstract void configureProperties();

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
        return getBooleanProperty(PROP_VISIBILITY);
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
    @Override
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
        return getIntegerProperty(PROP_BORDER_WIDTH);
    }

    /**
     * Gets the width of the border.
     *
     * @return the border width
     */
    public final String getBorderColor() {
        return getColor(PROP_BORDER_COLOR);
    }

    /**
     * Gets the style of the border.
     *
     * @return the border style
     */
    public final int getBorderStyle() {
        return getArrayOptionProperty(PROP_BORDER_STYLE);
    }

    /**
     * Add a property change listener.
     *
     * @param listener
     *            The property change listener that is to be added.
     */
    public final void addPropertyChangeListener(final PropertyChangeListener listener) {
        _propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public final void addPropertyChangeListener(final String propertyId, final IPropertyChangeListener listener) {
        final WidgetProperty property = _propertyMap.get(propertyId);

        if (property != null) {
            property.addPropertyChangeListener(listener);
        }
    }

    /**
     * Remove a property change listener.
     *
     * @param listener
     *            The property change listener that is to be removed.
     */
    public final void removePropertyChangeListener(final PropertyChangeListener listener) {
        _propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void saveState() {
        state = new HashMap<String, Object>();
        for (final String key : _propertyMap.keySet()) {
            state.put(key, _propertyMap.get(key).getPropertyValue());
        }
    }

    public void restoreState() {
        for (final String key : state.keySet()) {
            setPropertyValue(key, state.get(key));
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
    protected final void firePropertyChangeEvent(final String propertyName, final Object oldValue, final Object newValue) {
        final PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        _propertyChangeSupport.firePropertyChange(evt);
    }

    public DisplayModel getRoot() {
        DisplayModel root = null;

        ContainerModel container = getParent();

        while (root == null) {
            if (container instanceof DisplayModel) {
                root = (DisplayModel) container;
            } else {
                container = container.getParent();
            }
        }

        return root;
    }

    public ContainerModel getParent() {
        return _parent;
    }

    public void setParent(final ContainerModel parent) {
        _parent = parent;
    }

    public IProcessVariableAddress getMainPvAdress() {

        final String raw = getPrimaryPV();

        if (raw != null && raw.length() > 0) {
            final Map<String, String> aliases = getAllInheritedAliases();

            try {
                final String channelName = ChannelReferenceValidationUtil.createCanonicalName(raw, aliases);

                final IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(channelName);
                return pv;

            } catch (final ChannelReferenceValidationException e) {
                return null;
            }
        }

        return null;
    }

    public List<IProcessVariableAddress> getAllPvAdresses() {
        final Set<IProcessVariableAddress> result = new HashSet<IProcessVariableAddress>();

        final Map<String, String> aliases = getAllInheritedAliases();

        // add the main pv
        final IProcessVariableAddress mainPv = getMainPvAdress();
        if (mainPv != null) {
            result.add(mainPv);
        }

        // collect pvs from dynamic descriptors
        for (final WidgetProperty wp : _propertyMap.values()) {
            final DynamicsDescriptor dd = wp.getDynamicsDescriptor();

            if (dd != null) {
                for (final ParameterDescriptor pd : dd.getInputChannels()) {

                    try {
                        final String channelName = ChannelReferenceValidationUtil.createCanonicalName(pd.getChannel(), aliases);

                        final IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(channelName);

                        if (pv != null) {
                            result.add(pv);
                        }
                    } catch (final ChannelReferenceValidationException e) {
                           // we do nothing
                    }

                }
            }
        }

        return new ArrayList<IProcessVariableAddress>(result);
    }

    public Set<IProcessVariableAddress> getPvAdressesWithWriteAccess() {
        final Set<IProcessVariableAddress> result = new HashSet<IProcessVariableAddress>();

        final Map<String, String> aliases = getAllInheritedAliases();

        // collect pvs from dynamic descriptors
        for (final WidgetProperty wp : _propertyMap.values()) {
            final DynamicsDescriptor dd = wp.getDynamicsDescriptor();

            if (dd != null && dd.getOutputChannel() != null) {
                try {
                    final String channelName = ChannelReferenceValidationUtil.createCanonicalName(dd.getOutputChannel().getChannel(), aliases);

                    final IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(channelName);

                    if (pv != null) {
                        result.add(pv);
                    }
                } catch (final ChannelReferenceValidationException e) {
                    // we do nothing
                }
            }
        }
        return result;
    }

    private boolean hasOutputChannel() {
        for (final WidgetProperty property : _propertyMap.values()) {
            final DynamicsDescriptor dynamicsDescriptor = property.getDynamicsDescriptor();

            // a dynamics descriptor must not exist
            if (dynamicsDescriptor != null) {
                // check,if an output channel exists
                if (dynamicsDescriptor.getOutputChannel() != null) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param javaType the javaType to set
     */
    public void setJavaType(final Class javaType) {
        _javaType = javaType;
    }

    /**
     * @return the javaType
     */
    public Class getJavaType() {
        return _javaType;
    }

}
