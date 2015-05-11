/*
        * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
        * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
        *
        * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
        * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
        NOT LIMITED
        * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
        AND
        * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
        BE LIABLE
        * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
        CONTRACT,
        * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
        SOFTWARE OR
        * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
        DEFECTIVE
        * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
        REPAIR OR
        * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
        OF THIS LICENSE.
        * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
        DISCLAIMER.
        * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
        ENHANCEMENTS,
        * OR MODIFICATIONS.
        * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
        MODIFICATION,
        * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
        DISTRIBUTION OF THIS
        * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
        MAY FIND A COPY
        * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
        */
package org.csstudio.sds.components.ui.internal.editparts;

import static org.csstudio.sds.model.AbstractTextTypeWidgetModel.PROP_PRECISION;
import static org.csstudio.sds.model.AbstractTextTypeWidgetModel.PROP_TEXT_TYPE;
import static org.csstudio.sds.model.AbstractWidgetModel.PROP_ALIASES;
import static org.csstudio.sds.model.AbstractWidgetModel.PROP_PRIMARY_PV;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.csstudio.sds.model.AbstractTextTypeWidgetModel;
import org.csstudio.sds.model.TextTypeEnum;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.figures.ITextFigure;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.eclipse.draw2d.IFigure;

/**
 * Give two Properties for format a String representation.
 *
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.6 $
 * @since 11.05.2010
 */
public abstract class AbstractTextTypeWidgetEditPart extends AbstractWidgetEditPart {

    /**
     * The actual figure will be surrounded with a small frame that can be used
     * to drag the figure around (even if the cell editor is activated).
     */
    protected static final int FRAME_WIDTH = 1;

    /**
     * The input field will be slightly brighter than the actual figure so it
     * can be easily recognized.
     */
    protected static final int INPUT_FIELD_BRIGHTNESS = 10;

    private final NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);


    /**
     *
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {

        final IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                                        final Object newValue,
                                        final IFigure refreshableFigure) {
                final ITextFigure labelFigure = (ITextFigure) refreshableFigure;
                labelFigure.setTextValue(determineLabel(null));
                return true;
            }
        };
        setPropertyChangeHandler(PROP_TEXT_TYPE, handle);

        // precision
        final IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                                        final Object newValue,
                                        final IFigure refreshableFigure) {
                final ITextFigure labelFigure = (ITextFigure) refreshableFigure;
                labelFigure.setTextValue(determineLabel(PROP_PRECISION));
                return true;
            }
        };
        setPropertyChangeHandler(PROP_PRECISION, handler);

        // aliases
        final IWidgetPropertyChangeHandler aliasHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                                        final Object newValue,
                                        final IFigure refreshableFigure) {
                final ITextFigure labelFigure = (ITextFigure) refreshableFigure;
                labelFigure.setTextValue(determineLabel(PROP_ALIASES));
                return true;
            }
        };
        setPropertyChangeHandler(PROP_ALIASES, aliasHandler);
        // primary pv
        final IWidgetPropertyChangeHandler pvHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                                        final Object newValue,
                                        final IFigure refreshableFigure) {
                final ITextFigure labelFigure = (ITextFigure) refreshableFigure;
                labelFigure.setTextValue(determineLabel(PROP_ALIASES));
                return true;
            }
        };
        setPropertyChangeHandler(PROP_PRIMARY_PV, pvHandler);

    }

    /**
     * Format a String on the base of the properties PROP_TEXT_TYPE and PROP_PRECISION.
     *
     * @param updatedPropertyId the Property that was updated
     * @return the new string value
     */
    protected String determineLabel(final String updatedPropertyId) {
        final AbstractTextTypeWidgetModel model = (AbstractTextTypeWidgetModel) getCastedModel();

        final TextTypeEnum type = model.getValueType();
        final String text = model.getStringValue();
        String toprint = "none";

        switch (type) {
            case TEXT:
                toprint = handleText(updatedPropertyId, model, text, toprint);
                break;
            case DOUBLE:
                toprint = handleDouble(updatedPropertyId, model, text, toprint);
                break;
            case ALIAS:
                toprint = handleAlias(updatedPropertyId, model, toprint);
                break;
            case HEX:
                toprint = handleHex(updatedPropertyId, model, text, toprint);
                break;
            case EXP:
                toprint = handleExp(updatedPropertyId, model, text, toprint);
                break;
            default:
                toprint = "unknown value type";
        }
        return toprint;
    }

    /**
     * @param updatedPropertyId
     * @param model
     * @param text
     * @param toprint
     * @return
     */
    protected String handleText(final String updatedPropertyId,
                              final AbstractTextTypeWidgetModel model,
                              final String text,
                              final String toprint) {
        if ( updatedPropertyId == null
                || updatedPropertyId.equals(model.getStringValueID())) {
            return text;
        }
        return toprint;
    }

    /**
     * @param updatedPropertyId
     * @param model
     * @param text
     * @param toprint
     * @return
     */
    private String handleHex(final String updatedPropertyId,
                             final AbstractTextTypeWidgetModel model,
                             final String text,
                             final String toprint) {
        if ( updatedPropertyId == null
                || updatedPropertyId.equals(model.getStringValueID())) {
            try {
                final long l = Long.parseLong(text);
                return Long.toHexString(l);
            } catch (final Exception e1) {
                try {
                    final double d = Double.parseDouble(text);
                    return Double.toHexString(d);
                } catch (final Exception e2) {
                    return text;
                }
            }
        }
        return toprint;
    }

    /**
     * @param updatedPropertyId
     * @param model
     * @param text
     * @param toprint
     * @return
     */
    private static String handleExp(final String updatedPropertyId,
                             final AbstractTextTypeWidgetModel model,
                             final String text,
                             final String toprint) {
        if ( updatedPropertyId == null
                || updatedPropertyId.equals(model.getStringValueID())
                || updatedPropertyId.equals(AbstractTextTypeWidgetModel.PROP_PRECISION)) {
            try {
                String pattern = "0.";
                for (int i = 0; i < model.getPrecision(); i++) {
                    if (i == 0) {
                        pattern = pattern.concat("0");
                    } else {
                        pattern = pattern.concat("#");
                    }
                }
                pattern = pattern.concat("E00");
                final DecimalFormat expFormat = new DecimalFormat(pattern);
                final double d = Double.parseDouble(text);
                return expFormat.format(d);
            } catch (final Exception e) {
                return text;
            }
        }
        return toprint;
    }

    /**
     * @param updatedPropertyId
     * @param model
     * @param toprint
     * @return
     */
    private static String handleAlias(final String updatedPropertyId,
                               final AbstractTextTypeWidgetModel model,
                               final String toprint) {
        if ( updatedPropertyId == null
                || updatedPropertyId.equals(AbstractTextTypeWidgetModel.PROP_ALIASES)
                || updatedPropertyId.equals(AbstractTextTypeWidgetModel.PROP_PRIMARY_PV)) {
            try {
                return ChannelReferenceValidationUtil.createCanonicalName(model
                        .getPrimaryPV(), model.getAllInheritedAliases());
            } catch (final ChannelReferenceValidationException e) {
                return model.getPrimaryPV();
            }
        }
        return toprint;
    }

    /**
     * @param updatedPropertyId
     * @param model
     * @param text
     * @param toprint
     * @return
     */
    private String handleDouble(final String updatedPropertyId,
                                final AbstractTextTypeWidgetModel model,
                                final String text,
                                final String toprint) {
        if ( updatedPropertyId == null
                || updatedPropertyId.equals(model.getStringValueID())
                || updatedPropertyId.equals(AbstractTextTypeWidgetModel.PROP_PRECISION)) {
            try {
                final double d = Double.parseDouble(text);
                final int indexOf = text.indexOf('.');
                final int min = model.getPrecision();
                // (hrickens) [04.05.2011]: show precision number of zeros
//                if(indexOf>=0) {
//                    int i = text.length()-indexOf-1;
//                    if(i<min) {
//                        min = i;
//                    }
//                }
                numberFormat.setMinimumFractionDigits(min);
                numberFormat.setMaximumFractionDigits(model.getPrecision());
                return numberFormat.format(d);
            } catch (final Exception e) {
                return text;
            }
        }
        return toprint;
    }
}
