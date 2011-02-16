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
package org.csstudio.domain.desy.types;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.time.TimeInstant;

/**
 * Abstract class for an alarm bearing css value type of base type <T>
 *
 * @author bknerr
 * @since 07.12.2010
 * @param <T> the basic value type of the datum/data
 */
public class TimedCssAlarmValueType<T> implements ITimedCssAlarmValueType<T> {

    private final T _data;
    private final IAlarm _alarm;
    private final TimeInstant _timestamp;

    /**
     * Constructor.
     */
    public TimedCssAlarmValueType(@Nonnull final T data,
                                     @Nullable final IAlarm alarm,
                                     @Nonnull final TimeInstant timestamp) {
        _data = data;
        _alarm = alarm;
        _timestamp = timestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public T getValueData() {
        return _data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public TimeInstant getTimestamp() {
        return _timestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IAlarm getAlarm() {
        return _alarm;
    }

}
