/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.ui.internal.adapters;

import java.util.Map;
import java.util.Set;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.internal.properties.PropertyDescriptorFactoryService;
import org.csstudio.sds.ui.internal.properties.view.IPropertySource;
import org.csstudio.sds.ui.properties.IPropertyDescriptor;
import org.csstudio.sds.ui.properties.IPropertyDescriptorFactory;
import org.csstudio.sds.ui.properties.PropertyDescriptor;

/**
 * Adapter that enriches {@link AbstractWidgetModel} so that
 * {@link IPropertySource} behaviour is supported. <br>
 *
 * @author Sven Wende
 * @version $Revision: 1.11 $
 *
 */
public final class WidgetPropertySourceAdapter implements IPropertySource {
    /**
     * The encapsulated display widget model.
     */
    private AbstractWidgetModel _widgetModel;

    /**
     * Constructor.
     *
     * @param widgetModel
     *            a display widget model
     */
    public WidgetPropertySourceAdapter(final AbstractWidgetModel widgetModel) {
        _widgetModel = widgetModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getEditableValue() {
        return "Properties of display widget model"; //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        Set<String> visiblePropertyIds = _widgetModel.getVisiblePropertyIds();

        IPropertyDescriptor[] result = new IPropertyDescriptor[visiblePropertyIds
                .size()];

        PropertyDescriptorFactoryService service = PropertyDescriptorFactoryService
                .getInstance();

        int i = 0;

        for (String propertyId : visiblePropertyIds) {
            WidgetProperty widgetProperty = _widgetModel
                    .getPropertyInternal(propertyId);

            IPropertyDescriptor descriptor = null;

            // get a property descriptor for the current property�s type
            if (service.hasPropertyDescriptorFactory(widgetProperty
                    .getPropertyType())) {
                final IPropertyDescriptorFactory factory = service
                        .getPropertyDescriptorFactory(widgetProperty
                                .getPropertyType());

                descriptor = factory.createPropertyDescriptor(propertyId,
                        widgetProperty);

                if (descriptor instanceof PropertyDescriptor) {
                    PropertyDescriptor pDescriptor = (PropertyDescriptor) descriptor;

                    pDescriptor.setCategory(widgetProperty.getCategory()
                            .toString());
                }

            }

            if (descriptor == null) {
                throw new IllegalArgumentException(
                        "Could not create property descriptor for property "
                                + widgetProperty.getDescription() + " of type "
                                + widgetProperty.getPropertyType());
            }
            result[i] = descriptor;

            i++;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getPropertyValue(final Object id) {
        assert id != null;
        Object result = null;

        String propertyId = id.toString();

        if (_widgetModel.hasProperty(propertyId)) {
            result = _widgetModel.getPropertyInternal(propertyId).getPropertyValue();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPropertySet(final Object id) {
        String propertyId = id.toString();

        if (!_widgetModel.hasProperty(propertyId)) {
            return false;
        }

        final Object defaultValue = _widgetModel.getPropertyInternal(propertyId)
                .getDefaultValue();
        final Object currentValue = _widgetModel.getPropertyInternal(propertyId)
                .getPropertyValue();

        final boolean hasDefaultValue = (defaultValue != null);
        final boolean hasCurrentValue = (currentValue != null);

        return (hasDefaultValue && !defaultValue.equals(currentValue))
                || (!hasDefaultValue && hasCurrentValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetPropertyValue(final Object id) {
        String propertyId = id.toString();

        if (isPropertySet(propertyId)) {
            final Object defaultValue = _widgetModel.getPropertyInternal((String) id)
                    .getDefaultValue();
            _widgetModel.setPropertyValue(propertyId, defaultValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValue(final Object id, final Object value) {
        String propertyId = id.toString();

        if (_widgetModel.hasProperty(propertyId)) {
            _widgetModel.setPropertyValue(propertyId, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DynamicsDescriptor getDynamicsDescriptor(final Object id) {
        String propertyId = id.toString();

        if (_widgetModel.hasProperty(propertyId)) {
            return _widgetModel.getDynamicsDescriptor(propertyId);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDynamicsDescriptor(final Object id,
            final DynamicsDescriptor dynamicsDescriptor) {
        String propertyId = id.toString();

        if (_widgetModel.hasProperty(propertyId)) {
            DynamicsDescriptor newDescriptor = dynamicsDescriptor != null ? dynamicsDescriptor
                    .clone()
                    : null;
            _widgetModel.setDynamicsDescriptor(propertyId, newDescriptor);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getAliases() {
        return _widgetModel.getAllInheritedAliases();
    }
}
