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
package org.csstudio.domain.desy.epics.typesupport;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.data.values.IStringValue;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

/**
 * IStringIValue conversion support
 *
 * @author bknerr
 * @since 15.12.2010
 */
final class IStringValueConversionTypeSupport extends
        AbstractIValueConversionTypeSupport<IStringValue> {
    /**
     * Constructor.
     */
    public IStringValueConversionTypeSupport() {
        super(IStringValue.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @Nonnull
    protected Object toData(@Nonnull final IStringValue value,
                            @Nonnull final Class<?> elemClass,
                            @Nonnull final Class<? extends Collection> collClass) throws TypeSupportException {
        final String[] values = value.getValues();
        if (values == null) {
            throw new TypeSupportException("IValue values array is null! Conversion failed.", null);
        }
        final AbstractIValueDataToTargetTypeSupport<?> support = checkForPlausibilityAndGetSupport(elemClass,
                                                                                     collClass,
                                                                                     values.length);

        if (values.length == 1) {
            return support.fromStringValue(values[0]);
        }

        final Collection coll = instantiateCollection(collClass);
        for (final String val : values) {
            coll.add(support.fromStringValue(val));
        }
        return coll;
    }
}
