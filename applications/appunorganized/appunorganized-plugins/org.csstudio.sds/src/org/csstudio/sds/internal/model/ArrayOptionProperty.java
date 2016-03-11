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
 * A property, which is able to handle an array with String alternatives. The
 * index of a certain alternative in the array is used as identification.
 *
 * @author Sven Wende
 * @version $Revision: 1.1 $
 *
 */
public final class ArrayOptionProperty extends WidgetProperty {
    /**
     * The option values.
     */
    private String[] _options;

    /**
     * Constructor.
     *
     * @param description
     *            a description
     * @param category
     *            a category
     * @param defaultValue
     *            the default value
     * @param options
     *            The option values
     */
    public ArrayOptionProperty(final String description,
            final WidgetPropertyCategory category, final String[] options,
            final int defaultValue) {
        super(PropertyTypesEnum.ARRAYOPTION, description, category,
                defaultValue, null);
        _options = options;
    }

    /**
     * Return the option values.
     *
     * @return The option values.
     */
    public String[] getOptions() {
        return _options;
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
            try {
                acceptedValue = Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                acceptedValue = null;
            }
        } else {
            acceptedValue = (Integer) value;
        }

        // check range
        if (acceptedValue != null) {
            if (acceptedValue < 0) {
                acceptedValue = 0;
            } else if (acceptedValue >= _options.length) {
                acceptedValue = _options.length;
            }
        }

        return acceptedValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextForTooltip() {
        String result = "-";

        int index = getPropertyValue();

        if(index>=0 && index < _options.length) {
            result = _options[index];
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class[] getCompatibleJavaTypes() {
        return new Class[] { Integer.class };
    }
}
