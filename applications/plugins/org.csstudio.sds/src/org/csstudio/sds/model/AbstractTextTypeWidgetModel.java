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
package org.csstudio.sds.model;

import java.util.List;

import org.csstudio.sds.cursorservice.AbstractCursor;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 11.05.2010
 */
public abstract class AbstractTextTypeWidgetModel extends AbstractWidgetModel {

    /**
     * Type of the displayed text.
     */
    public static final String PROP_TEXT_TYPE = "value_type";

    /**
     * The ID of the precision property.
     */
    public static final String PROP_PRECISION = "precision"; //$NON-NLS-1$

    /**
     * Constructor.
     */
    public AbstractTextTypeWidgetModel() {
        super();
    }

    /**
     * Constructor.
     */
    public AbstractTextTypeWidgetModel(final List<AbstractCursor> cursorDescriptors) {
        super(false, cursorDescriptors);
    }


    /**
     * Returns the type of the text (Double or String).
     *
     * @return The type of the text
     */
    public TextTypeEnum getValueType() {
        TextTypeEnum result = TextTypeEnum.TEXT;

        final int index = getArrayOptionProperty(PROP_TEXT_TYPE);

        if (index >= 0 && index < TextTypeEnum.values().length) {
            result = TextTypeEnum.values()[index];
        }

        return result;
    }

    /**
     * Return the precision.
     *
     * @return The precision.
     */
    public int getPrecision() {
        return getIntegerProperty(PROP_PRECISION);
    }

    public abstract String getStringValueID();

    /**
     * Returns the transparent state of the background.
     *
     * @return True if the background is transparent, false otherwise
     */
    public abstract boolean getTransparent();

    public String getStringValue() {
        return getStringProperty(getStringValueID());
    }
}
