package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.Collections;
import java.util.List;

import org.csstudio.sds.internal.model.DoubleProperty;
import org.csstudio.sds.internal.model.IntegerProperty;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * Section implementation for {@link IntegerProperty}.
 *
 * @author Sven Wende
 *
 */
public final class DoubleSection extends AbstractTextSection<DoubleProperty, Double> {

    public DoubleSection(String propertyId) {
        super(propertyId);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected Double getConvertedValue(String text) {
        return text != null ? Double.parseDouble(text) : null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRefreshControls(DoubleProperty widgetProperty) {
        if (widgetProperty != null && widgetProperty.getPropertyValue() != null
                && !widgetProperty.getPropertyValue().toString().equals(getTextControl().getText())) {
            getTextControl().setText(widgetProperty.getPropertyValue().toString());
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected List<IContentProposal> getContentProposals(DoubleProperty property, AbstractWidgetModel selectedWidget,
            List<AbstractWidgetModel> selectedWidgets) {
        return Collections.emptyList();
    }

}
