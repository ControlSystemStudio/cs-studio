package org.csstudio.sds.ui.internal.editor.newproperties;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.eclipse.ui.views.properties.tabbed.ISection;

/**
 * Factory that creates sections for property types.
 *
 * @author Sven Wende
 */
public interface ISectionFactory {
    /**
     * Creates a section for the specified property type.
     *
     * @param propertyId
     *            the property id
     *
     * @param type
     *            the property type
     *
     * @return a section for the specified property type or an appropriate
     *         fallback section, never null
     */
    ISection createSection(String propertyId, PropertyTypesEnum type);
}