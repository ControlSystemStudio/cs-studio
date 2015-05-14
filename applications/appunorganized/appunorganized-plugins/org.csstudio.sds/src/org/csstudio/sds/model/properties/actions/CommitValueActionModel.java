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
 package org.csstudio.sds.model.properties.actions;

import org.csstudio.sds.internal.model.StringProperty;
import org.csstudio.sds.model.ActionType;
import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * A {@link AbstractWidgetActionModel}, which commits a value.
 * @author Kai Meyer
 */
public final class CommitValueActionModel extends AbstractWidgetActionModel {
    /**
     * The ID for the <i>value</i> property.
     */
    public static final String PROP_VALUE = "value";
    /**
     * The ID for the <i>description</i> property.
     */
    public static final String PROP_DESCRIPTION = "description";

    /**
     * Constructor.
     */
    public CommitValueActionModel() {
        super("SEND", ActionType.COMMIT_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createProperties() {
        addProperty(PROP_VALUE, new StringProperty("Value", WidgetPropertyCategory.BEHAVIOR, ""));
        addProperty(PROP_DESCRIPTION, new StringProperty("Description", WidgetPropertyCategory.BEHAVIOR, ""));
    }

    /**
     * Returns the value to commit.
     * @return The value
     */
    public String getValue() {
        return getProperty(PROP_VALUE).getPropertyValue();
    }

    /**
     * Returns the description.
     * @return The description
     */
    public String getDescription() {
        return getProperty(PROP_DESCRIPTION).getPropertyValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getActionLabel() {
        if (getDescription()==null || getDescription().trim().length()==0) {
            StringBuffer buffer = new StringBuffer(getName());
            buffer.append(" '");
            if (getValue()==null || getValue().trim().length()==0) {
                buffer.append("unspecified");
            } else {
                buffer.append(getValue());
            }
            buffer.append("'");
            return buffer.toString();
        }
        return getDescription();
    }

}
