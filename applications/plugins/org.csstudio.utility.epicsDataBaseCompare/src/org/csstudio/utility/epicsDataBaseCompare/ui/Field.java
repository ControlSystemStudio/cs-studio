/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.epicsDataBaseCompare.ui;

import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author hrickens
 * @since 06.09.2011
 */
public class Field implements Comparable<Field>{

    private final String _field;
    private final String _value;
    private final EpicsRecord _parent;

    /**
     * Constructor.
     */
    public Field(@Nonnull final EpicsRecord parent, @Nonnull final String field, @Nonnull final String value) {
        _parent = parent;
        _field = field.trim();
        _value = checkValueIsNumberAndFormatNumber(value);
    }

    @Nonnull
    private String checkValueIsNumberAndFormatNumber(@Nonnull final String value) {
        final String tmp = value;
        final boolean matches = tmp.matches("[0-9.]+");
        if(matches) {
            final Double valueOf = Double.valueOf(tmp);
            final NumberFormat nFormatter = NumberFormat.getInstance(Locale.US);
            nFormatter.setGroupingUsed(false);
            nFormatter.setMaximumFractionDigits(12);
            nFormatter.setMinimumFractionDigits(1);
            return nFormatter.format(valueOf);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@Nullable final Field arg0) {
        if(arg0!=null) {
            int compareTo = getField().compareTo(arg0.getField());
            if(compareTo==0) {
                compareTo = getValue().compareTo(arg0.getValue());
            }
            return compareTo;
        }
        return -1;
    }

    @Nonnull
    public String getField() {
        return _field;
    }

    @Nonnull
    public String getValue() {
        return _value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String toString() {
        return String.format("%4s : %s,", _field,_value);
    }

    @Nonnull
    public EpicsRecord getParent() {
        return _parent;
    }

}
