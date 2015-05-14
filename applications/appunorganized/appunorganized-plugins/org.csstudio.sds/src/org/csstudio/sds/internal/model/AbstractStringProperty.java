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

import java.text.NumberFormat;
import java.util.Locale;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * A property, which is able to handle String values.
 *
 * @author Sven Wende
 * @version $Revision: 1.2 $
 *
 */
public abstract class AbstractStringProperty extends WidgetProperty {

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
    public AbstractStringProperty(final PropertyTypesEnum type, final String description,
            final WidgetPropertyCategory category, final String defaultValue) {
        super(type, description, category, defaultValue,
                null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object checkValue(final Object value) {
        Object acceptedValue = value;

        if(value != null && value instanceof Number) {
            final NumberFormat instance = NumberFormat.getInstance(Locale.US);
            instance.setGroupingUsed(false);
            instance.setMinimumFractionDigits(0);
            instance.setMaximumFractionDigits(100);
            acceptedValue = instance.format(value);
        }else if (value != null && !(value instanceof String)) {
            acceptedValue = value.toString();
        }

        return acceptedValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class[] getCompatibleJavaTypes() {
        return new Class[]{Object.class, String.class};
    }

}
