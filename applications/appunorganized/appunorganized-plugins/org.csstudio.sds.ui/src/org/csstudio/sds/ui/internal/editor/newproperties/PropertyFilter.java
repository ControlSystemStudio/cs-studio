package org.csstudio.sds.ui.internal.editor.newproperties;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.IFilter;

/**
 * Filter implementation that selects {@link EditPart} instances which control
 * an {@link AbstractWidgetModel} that has a certain {@link WidgetProperty}
 * which is identified by its id.
 *
 * Can be
 *
 * @author Sven Wende
 *
 */
public class PropertyFilter implements IFilter {

    private String propertyId;

    public PropertyFilter() {
    }

    public PropertyFilter(String propertyId) {
        this.propertyId = propertyId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean select(Object o) {
        boolean result = false;

        if (o instanceof EditPart) {
            EditPart ep = (EditPart) o;

            if (ep.getModel() instanceof AbstractWidgetModel) {
                AbstractWidgetModel widget = (AbstractWidgetModel) ep.getModel();

                result = widget.hasProperty(propertyId);
            }
        }

        return result;
    }

}
