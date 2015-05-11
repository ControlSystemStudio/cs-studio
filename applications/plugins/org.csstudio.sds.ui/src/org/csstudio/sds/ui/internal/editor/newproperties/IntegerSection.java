package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.Collections;
import java.util.List;

import org.csstudio.sds.internal.model.DoubleProperty;
import org.csstudio.sds.internal.model.IntegerProperty;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * Section implementation for {@link DoubleProperty}.
 *
 * @author Sven Wende
 *
 */
public final class IntegerSection extends AbstractTextSection<IntegerProperty, Integer> {

    public IntegerSection(String propertyId) {
        super(propertyId);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected Integer getConvertedValue(String text) {
        return text!=null?Integer.parseInt(text):null;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRefreshControls(IntegerProperty widgetProperty) {
        if (widgetProperty != null && widgetProperty.getPropertyValue() != null
                && !widgetProperty.getPropertyValue().toString().equals(getTextControl().getText())) {
            getTextControl().setText(widgetProperty.getPropertyValue().toString());
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected List<IContentProposal> getContentProposals(IntegerProperty property, AbstractWidgetModel selectedWidget,
            List<AbstractWidgetModel> selectedWidgets) {
        return Collections.emptyList();
    }

}
