package org.csstudio.sds.model.properties.actions;

import org.csstudio.sds.internal.model.ResourceProperty;
import org.csstudio.sds.internal.model.StringProperty;
import org.csstudio.sds.model.ActionType;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * A {@link AbstractWidgetActionModel}, which opens a Data Browser Configuration file in a view..
 *
 * @author Kai Meyer
 */
public class OpenDataBrowserActionModel extends AbstractWidgetActionModel {
    /**
     * The ID for the <i>resource</i> property.
     */
    public static final String PROP_RESOURCE = "resource";

    /**
     * The ID for the <i>description</i> property.
     */
    public static final String PROP_DESCRIPTION = "description";

    public OpenDataBrowserActionModel() {
        super(ActionType.OPEN_DATA_BROWSER.getTitle(), ActionType.OPEN_DATA_BROWSER);
    }

    @Override
    protected void createProperties() {
        ResourceProperty resource = new ResourceProperty("Config file", WidgetPropertyCategory.BEHAVIOR, new Path(""),
                new String[] { "css-plt", "plt" });
        addProperty(PROP_RESOURCE, resource);
        StringProperty description = new StringProperty("Description",
                WidgetPropertyCategory.BEHAVIOR, "");
        addProperty(PROP_DESCRIPTION, description);
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
     * Returns the description.
     *
     * @return The description
     */
    public String getDescription() {
        return getProperty(PROP_DESCRIPTION).getPropertyValue();
    }

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
