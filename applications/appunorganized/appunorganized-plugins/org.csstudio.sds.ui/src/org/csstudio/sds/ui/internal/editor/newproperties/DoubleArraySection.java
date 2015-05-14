package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.Collections;
import java.util.List;

import org.csstudio.sds.internal.model.DoubleArrayProperty;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * Section implementation for {@link DoubleArrayProperty}.
 *
 * @author Sven Wende
 *
 */
public final class DoubleArraySection extends AbstractTextSection<DoubleArrayProperty, double[]> {

    public DoubleArraySection(String propertyId) {
        super(propertyId);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected  double[] getConvertedValue(String text) {
        // String#split in Java returns a single-element array (instead of an
        // empty array) if the input was the empty string. But we must return
        // an empty array in that case!
        if (text.equals("")) {
            return new double[0];
        }


        String value = text.replaceAll(",", ";");
        String[] strings = value.split(";");

        double[] result = new double[strings.length];
        for (int i=0;i<strings.length;i++) {
            try {
            Double d = Double.valueOf(strings[i].trim());
            if (d.isNaN()) {
                result[i] = 0;
            } else {
                result[i] = d.doubleValue();
            }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRefreshControls(DoubleArrayProperty widgetProperty) {
        if (widgetProperty != null && widgetProperty.getPropertyValue() != null
                && !widgetProperty.getPropertyValue().toString().equals(getTextControl().getText())) {
            double[] propertyValue = widgetProperty.getPropertyValue();
            getTextControl().setText(getStringRepresentation(propertyValue));
        }
    }

    private String getStringRepresentation(double[] array) {
        StringBuffer buffer = new StringBuffer();
        if (array.length>0) {
            buffer.append(array[0]);
            for (int i=1;i<array.length;i++) {
                buffer.append("; ");
                buffer.append(array[i]);
            }
        }
        return buffer.toString();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected List<IContentProposal> getContentProposals(DoubleArrayProperty property, AbstractWidgetModel selectedWidget,
            List<AbstractWidgetModel> selectedWidgets) {
        return Collections.emptyList();
    }

}
