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
package org.csstudio.domain.desy.epics.types;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.AbstractTypeSupport;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IValue;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 15.12.2010
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!
 */
public abstract class EpicsTypeSupport<T> extends AbstractTypeSupport<T> {
// CHECKSTYLE ON : AbstractClassName

    /**
     * Tries to convert the given IValue type and its accompanying parms to the css value type.
     * @param value the value to be converted
     * @param alarm the value's alarm state
     * @param timestamp the value's timestamp
     * @return the conversion result
     * @throws TypeSupportException when conversion failed.
     * @param <R>
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    @CheckForNull
    public static <R extends ICssAlarmValueType<?>, T extends IValue>
        R toCssType(@Nonnull final T value,
                    @Nullable final IAlarm alarm,
                    @Nonnull final TimeInstant timestamp) throws TypeSupportException {

        final Class<T> typeClass = (Class<T>) value.getClass();
        final AbstractIValueConversionTypeSupport<R, T> support =
            (AbstractIValueConversionTypeSupport<R, T>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        return support.convertToCssType(value, alarm, timestamp);
    }
}
