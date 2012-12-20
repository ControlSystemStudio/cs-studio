/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.typesupport.TypeSupportException;



/**
 * Type Conversion Support for IValue types. Be careful, due to the IValue design there isn't any
 * type safety.
 *
 * @author bknerr
 * @since 02.12.2010
 * @param <T> the type of the system variable
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractIValueConversionTypeSupport<T extends IValue>
    extends EpicsIValueTypeSupport<T> {

    /**
     * Constructor.
     */
    AbstractIValueConversionTypeSupport(@Nonnull final Class<T> clazz) {
        super(clazz);
    }

    @Override
    @Nonnull
    protected Object toData(@Nonnull final T value,
                            @Nonnull final Class<?> elemClass,
                            @CheckForNull final Class<? extends Collection> collClass,
                            @CheckForNull final EpicsMetaData meta) throws TypeSupportException {
        return toData(value, elemClass, collClass);
    }

    @Nonnull
    protected abstract Object toData(@Nonnull final T value,
                                     @Nonnull final Class<?> elemClass,
                                     @CheckForNull final Class<? extends Collection> collClass) throws TypeSupportException;

    @Nonnull
    protected Collection instantiateCollection(@Nonnull final Class<? extends Collection> collClass) throws TypeSupportException {
        final Collection coll;
        try {
            coll = collClass.newInstance();
        } catch (final InstantiationException e) {
            throw new TypeSupportException("Target collection " + collClass.getName() +  " could not be instantiated. Conversion failed.", e);
        } catch (final IllegalAccessException e) {
            throw new TypeSupportException("Target collection " + collClass.getName() +  " could not be instantiated. Conversion failed.", e);
        }
        return coll;
    }

    @Nonnull
    protected AbstractIValueDataToTargetTypeSupport<?> checkForPlausibilityAndGetSupport(@Nonnull final Class<?> elemClass,
                                                                                         @CheckForNull final Class<? extends Collection> collClass,
                                                                                         final int valuesLength) throws TypeSupportException {
        checkForPlausibility(collClass, valuesLength);
        final AbstractIValueDataToTargetTypeSupport<?> support =
            (AbstractIValueDataToTargetTypeSupport<?>) findTypeSupportFor(AbstractIValueDataToTargetTypeSupport.class, elemClass);
        return support;
    }

    protected void checkForPlausibility(@CheckForNull final Class<? extends Collection> collClass,
                                        final int valuesLength) throws TypeSupportException {
        if (valuesLength > 1 && collClass == null) {
            throw new TypeSupportException("More than one value in IXXXValue but target collection type not specified. Conversion failed.", null);
        }
    }
}
