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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * This class defines a property for SDS widget models.
 *
 * @author Alexander Will
 * @version $Revision: 1.15 $
 *
 */
public abstract class WidgetProperty {

    /**
     * ID of the property.
     */
    private String _id;

    private AbstractWidgetModel _widgetModel;

    private Set<String> _hiddenBy;

    /**
     * Indicates, whether this property can be used to display dynamic values
     * from a control system.
     */
    private boolean _dynamicWriteAllowed;

    /**
     * The registered property change listeners.
     */
    private List<IPropertyChangeListener> _changeListeners;

    /**
     * A textual description of this property.
     */
    private String _description;

    private String _longDescription;

    /**
     * The data type of this property.
     */
    private PropertyTypesEnum _propertyType;

    /**
     * Id of the category, this property belongs to.
     */
    private WidgetPropertyCategory _category;

    /**
     * The dynamics descriptor.
     */
    private DynamicsDescriptor _dynamicsDescriptor;

    /**
     * The current value of this property.
     */
    private Object _propertyValue;

    /**
     * The current manual value of this property.
     */
    private Object _manualValue;

    /**
     * The default value of this property.
     */
    private Object _defaultValue;

    /**
     * Standard constructor.
     *
     * @param description
     *            A textual description of this property.
     * @param category
     *            the category
     * @param propertyType
     *            the data type of this property.
     * @param defaultValue
     *            the current value of this property.
     * @param dynamicsDescriptor
     *            the dynamics descriptor
     */
    public WidgetProperty(final PropertyTypesEnum propertyType,
            final String description, String longDescription, final WidgetPropertyCategory category,
            final Object defaultValue,
            final DynamicsDescriptor dynamicsDescriptor) {
            assert propertyType != null;
            assert description != null;
            assert category != null;
            assert defaultValue != null;

            _propertyType = propertyType;
            _description = description;
            _longDescription = longDescription;
            _category = category;
            _defaultValue = defaultValue;
            _propertyValue = defaultValue;
            _dynamicsDescriptor = dynamicsDescriptor;
            _changeListeners = new CopyOnWriteArrayList<IPropertyChangeListener>();
            _dynamicWriteAllowed = true;
            _hiddenBy = new HashSet<String>();

    }
    public WidgetProperty(final PropertyTypesEnum propertyType,
            final String description, final WidgetPropertyCategory category,
            final Object defaultValue,
            final DynamicsDescriptor dynamicsDescriptor) {
        this(propertyType, description, null, category, defaultValue, dynamicsDescriptor);
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public AbstractWidgetModel getWidgetModel() {
        return _widgetModel;
    }

    public void setWidgetModel(AbstractWidgetModel widgetModel) {
        _widgetModel = widgetModel;
    }


    public void hide(String masterId) {
        _hiddenBy.add(masterId);
    }

    public void show(String masterId) {
        _hiddenBy.remove(masterId);
    }

    public boolean isVisible() {
        return _hiddenBy.isEmpty();
    }

    /**
     * Returns a text representation for tooltips. Should be overridden by
     * subclasses to implement customized tooltip texts.
     *
     * @return a text representation for tooltips
     */
    public String getTextForTooltip() {
        Object v = getPropertyValue();
        return v != null ? v.toString() : "-";
    }

    /**
     * Returns all Java types to which the value of this {@link WidgetProperty}
     * is compatible to.
     *
     * @return an array which contains all Java types, the property value is
     *         compatible too
     */
    @SuppressWarnings("unchecked")
    public abstract Class[] getCompatibleJavaTypes();

    /**
     * Subclasses should check the specified value and should as far as possible
     * convert it to a value, which can be applied as property value.
     *
     * If a conversion is not possible, this method should return null to
     * indicate, that the specified value is incompatible.
     *
     * @param value
     *            the value to check
     * @return the original value, a converted value or null, if the original
     *         value is incompatible
     */
    public abstract Object checkValue(Object value);

    /**
     * Return the current value of this property.
     *
     * @return The current value of this property.
     */
    @SuppressWarnings("unchecked")
    public final <E> E getPropertyValue() {
        return (E) _propertyValue;
    }

    /**
     * Set the current value of this property.
     *
     * @param requestedNewValue
     *            The current value of this property.
     */
    public final void setPropertyValue(final Object requestedNewValue) {
        // do conversion check
        Object newValue = checkValue(requestedNewValue);

        if (newValue != null) {
            // apply value
            Object oldValue = _propertyValue;
            _propertyValue = newValue;
            firePropertyChangeEvent(oldValue, newValue);
        }
    }

    /**
     * Set the manual value of this property.
     *
     * @param manualValue
     *            The current value of this property.
     */
    public final void setManualValue(final Object manualValue) {
        if (_dynamicWriteAllowed) {
            _manualValue = manualValue;
            fireManualValueChanged();
        } else {
            throw new IllegalStateException(
                    "This method should not be called on a property, which is not enabled for writing dynamic values back to the control system."); //$NON-NLS-1$
        }
    }


    /**
     * set the textual description of this property.
     */
    public final void setDescription(String description) {
        this._description = description;
    }

    /**
     * Return the textual description of this property.
     *
     * @return The textual description of this property.
     */
    public final String getDescription() {
        return _description;
    }

    public String getLongDescription() {
        return _longDescription;
    }

    /**
     * Return the data type of this property.
     *
     * @return The data type of this property.
     */
    public final PropertyTypesEnum getPropertyType() {
        return _propertyType;
    }

    /**
     * Return the default value of this property.
     *
     * @return The default value of this property.
     */
    public final Object getDefaultValue() {
        return _defaultValue;
    }

    /**
     * Gets the dynamics descriptor.
     *
     * @return The dynamics descriptor.
     */
    public final DynamicsDescriptor getDynamicsDescriptor() {
        return _dynamicsDescriptor;
    }

    /**
     * Sets the dynamics descriptor.
     *
     * @param dynamicsDescriptor
     *            The dynamics descriptor.
     */
    public final void setDynamicsDescriptor(
            final DynamicsDescriptor dynamicsDescriptor) {
        _dynamicsDescriptor = dynamicsDescriptor;
        fireDynamicsDescriptorChanged();

    }

    /**
     * Returns the category for this property.
     *
     * @return the category for this property
     */
    public final WidgetPropertyCategory getCategory() {
        return _category;
    }

    /**
     * Add a property change listener.
     *
     * @param listener
     *            A property change listener.
     */
    public final synchronized void addPropertyChangeListener(
            final IPropertyChangeListener listener) {
        _changeListeners.add(listener);
    }

    /**
     * Remove a property change listener.
     *
     * @param listener
     *            A property change listener.
     */
    public final synchronized void removePropertyChangeListener(
            final IPropertyChangeListener listener) {
        _changeListeners.remove(listener);
    }

    /**
     * Remove all property change listeners.
     */
    public final synchronized void removePropertyChangeListeners() {
        for (IPropertyChangeListener l : _changeListeners) {
            removePropertyChangeListener(l);
        }
    }

    /**
     * Notify all registered property change listeners.
     *
     * @param oldValue
     *            The old value of the property.
     * @param newValue
     *            The new value of the property.
     */
    public final synchronized void firePropertyChangeEvent(
            final Object oldValue, final Object newValue) {

        boolean changed = false;

        if (newValue != null) {
            if (oldValue == null || !newValue.equals(oldValue)) {
                changed = true;
            }
        } else {
            if (oldValue != null) {
                changed = true;
            }
        }

        // only fire, if the value really changed
        if (changed) {
            for (IPropertyChangeListener listener : _changeListeners) {
                listener.propertyValueChanged(oldValue, newValue);
            }
        }
    }

    /**
     * Notify all registered property change listeners, that the dynamics
     * descriptor of this property has changed.
     */
    protected final synchronized void fireDynamicsDescriptorChanged() {
        for (IPropertyChangeListener listener : _changeListeners) {
            listener.dynamicsDescriptorChanged(_dynamicsDescriptor);
        }
    }

    /**
     * Notify all registered property change listeners, that the manual value of
     * this property has changed.
     */
    protected final synchronized void fireManualValueChanged() {
        for (IPropertyChangeListener listener : _changeListeners) {
            listener.propertyManualValueChanged(_id, _manualValue);
        }
    }

}
