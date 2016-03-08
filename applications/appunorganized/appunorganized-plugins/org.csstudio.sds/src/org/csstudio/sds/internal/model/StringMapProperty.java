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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A property, which is able to handle a map values.
 *
 * @author Kai Meyer
 * @version $Revision: 1.2 $
 *
 */
public class StringMapProperty extends WidgetProperty {

    private static final Logger LOG = LoggerFactory.getLogger(StringMapProperty.class);

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
    public StringMapProperty(final String description, final WidgetPropertyCategory category, final Map<String, String> defaultValue) {
        super(PropertyTypesEnum.MAP, description, category, defaultValue, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object checkValue(final Object value) {
        assert value != null : "value!=null"; //$NON-NLS-1$

        Object acceptedValue = value;

        if (!(value instanceof Map)) {
            acceptedValue = null;
        }

        return acceptedValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class[] getCompatibleJavaTypes() {
        return new Class[] { Map.class };
    }

    @Override
    public String getTextForTooltip() {
        Map<String, String> aliases = getPropertyValue();
        StringBuilder tooltip = new StringBuilder("{");

        // An explicit iterator is used here so the hasNext
        // method can
        // be called at the end to conditionally append a comma
        // to the
        // resulting tool tip string.
        for (Iterator<Map.Entry<String, String>> i = aliases.entrySet().iterator(); i.hasNext();) {
            Entry<String, String> alias = i.next();
            tooltip.append(alias.getKey()).append("=");
            String value = alias.getValue();
            String resolvedValue = "";
            try {
                resolvedValue = ChannelReferenceValidationUtil.createCanonicalName(value, getWidgetModel().getAllInheritedAliases());
            } catch (ChannelReferenceValidationException e) {
                LOG.debug("Aliases in \"" + value + "\" cannot be resolved, error message is: " + e.getMessage());
                resolvedValue = alias.getValue();
            }
            tooltip.append(resolvedValue);
            if (i.hasNext()) {
                tooltip.append(", ");
            }
        }

        tooltip.append("}");
        return tooltip.toString();
    }
}
