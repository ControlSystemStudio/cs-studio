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
package org.csstudio.sds.internal.model;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * A property, which is able to handle Integer values.
 *
 * @author Sven Wende
 * @version $Revision: 1.1 $
 *
 */
public final class IntegerProperty extends WidgetProperty {

    /**
     * Lower border for the property value.
     */
    private int _min;

    /**
     * Upper border for the property value.
     */
    private int _max;

    /**
     * Constructor.
     *
     * @param shortDescription
     *            a short escription
     * @param longDescription
     *            a long description
     * @param category
     *            a category
     * @param defaultValue
     *            the default value
     */
    public IntegerProperty(final String shortDescription, String longDescription, final WidgetPropertyCategory category, final int defaultValue, int min, int max) {
        super(PropertyTypesEnum.INTEGER, shortDescription, longDescription, category, defaultValue, null);
        assert min<=max;
        _min = min;
        _max = max;
    }

    /**
     * Constructor.
     *
     * @param description
     *            a description
     * @param category
     *            a category
     * @param defaultValue
     *            the default value
     */
    public IntegerProperty(final String description, final WidgetPropertyCategory category, final int defaultValue) {
        this(description, null, category, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Constructor.
     *
     * @param description
     *            a description
     * @param category
     *            a category
     * @param defaultValue
     *            the default value
     * @param minValue
     *            the lower border for the property value
     * @param maxValue
     *            the upper border for the property value
     */
    public IntegerProperty(final String description, final WidgetPropertyCategory category, final int defaultValue, final int minValue,
            final int maxValue) {
        this(description, null, category, defaultValue, minValue, maxValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object checkValue(final Object value) {
        assert value != null : "value!=null"; //$NON-NLS-1$

        Integer acceptedValue = null;

        // check type
        if (!(value instanceof Integer)) {
            if (value instanceof Number) {
                acceptedValue = ((Number) value).intValue();
            } else {
                try {
                    acceptedValue = Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    acceptedValue = null;
                }
            }
        } else {
            acceptedValue = (Integer) value;
        }

        // check borders
        if (acceptedValue != null) {
            if (acceptedValue > _max) {
                acceptedValue = _max;
            } else if (acceptedValue < _min) {
                acceptedValue = _min;
            }
        }

        return acceptedValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class[] getCompatibleJavaTypes() {
        return new Class[] { Number.class };
    }
}
