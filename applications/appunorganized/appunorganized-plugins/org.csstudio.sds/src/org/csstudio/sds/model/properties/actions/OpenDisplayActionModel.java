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
package org.csstudio.sds.model.properties.actions;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.internal.model.BooleanProperty;
import org.csstudio.sds.internal.model.OptionProperty;
import org.csstudio.sds.internal.model.ResourceProperty;
import org.csstudio.sds.internal.model.StringMapProperty;
import org.csstudio.sds.internal.model.StringProperty;
import org.csstudio.sds.model.ActionType;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * A {@link AbstractWidgetActionModel}, which opens a display in a shell or in a view..
 *
 * @author Kai Meyer
 */
public final class OpenDisplayActionModel extends AbstractWidgetActionModel {
    /**
     * The ID for the <i>resource</i> property.
     */
    public static final String PROP_RESOURCE = "resource";

    /**
     * The ID for the <i>aliases</i> property.
     */
    public static final String PROP_ALIASES = "aliases";

    /**
     * The ID for the <i>description</i> property.
     */
    public static final String PROP_DESCRIPTION = "description";

    /**
     * The ID for the <i>target</i> property.
     */
    public static final String PROP_TARGET = "target";

    /**
     * The ID for the <i>close</i> property.
     */
    public static final String PROP_CLOSE = "close";

    /**
     * Constructor.
     *
     * @param type
     *            The type for the {@link AbstractWidgetActionModel}
     */
    public OpenDisplayActionModel() {
        super(ActionType.OPEN_DISPLAY.getTitle(), ActionType.OPEN_DISPLAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createProperties() {
        ResourceProperty resource = new ResourceProperty("Display",
                WidgetPropertyCategory.BEHAVIOR, new Path(""),
                new String[] { "css-sds" });
        addProperty(PROP_RESOURCE, resource);
        StringMapProperty aliases = new StringMapProperty("Aliases",
                WidgetPropertyCategory.BEHAVIOR, new HashMap<String, String>());
        addProperty(PROP_ALIASES, aliases);
        StringProperty description = new StringProperty("Description",
                WidgetPropertyCategory.BEHAVIOR, "");
        addProperty(PROP_DESCRIPTION, description);
        OptionProperty target = new OptionProperty("Target",
                WidgetPropertyCategory.BEHAVIOR, OpenDisplayActionTarget.values(), OpenDisplayActionTarget.SHELL
                        .getIdentifier());
        addProperty(PROP_TARGET, target);
        BooleanProperty close = new BooleanProperty("Close",
                WidgetPropertyCategory.BEHAVIOR, false);
        addProperty(PROP_CLOSE, close);
    }

    /**
     * Returns the {@link IPath} to the display.
     *
     * @return The {@link IPath} to the display
     */
    public IPath getResource() {
        return getProperty(PROP_RESOURCE).getPropertyValue();
    }

    /**
     * Returns the new aliases for the display.
     *
     * @return The new aliases for the display
     */
    public Map<String, String> getAliases() {
        return getProperty(PROP_ALIASES).getPropertyValue();
    }

    /**
     * Returns the description.
     *
     * @return The description
     */
    public String getDescription() {
        return getProperty(PROP_DESCRIPTION).getPropertyValue();
    }

    /**
     * Returns the description.
     *
     * @return The description
     */
    public OpenDisplayActionTarget getTarget() {
        OpenDisplayActionTarget result = OpenDisplayActionTarget.SHELL;

        String id = getProperty(PROP_TARGET).getPropertyValue();
        id = id.toUpperCase();
        try {
            result = OpenDisplayActionTarget.valueOf(id);
        } catch (Exception e) {
            // ignore
        }

        return result;
    }

    /**
     * Returns the description.
     *
     * @return The description
     */
    public Boolean getClose() {
        return getProperty(PROP_CLOSE).getPropertyValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getActionLabel() {
        if (getDescription() == null || getDescription().trim().length() == 0) {
            StringBuffer buffer = new StringBuffer(this.getType().getTitle());
            buffer.append(" ");
            if (this.getResource().lastSegment() == null) {
                buffer.append("unspecified");
            } else {
                buffer.append(this.getResource().lastSegment());
            }
            return buffer.toString();
        }
        return getDescription();
    }

}
