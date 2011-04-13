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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IEnumeratedValue;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.CssValueType;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

/**
 * IEnumeratedValue conversion support.
 *
 * @author bknerr
 * @since 15.12.2010
 */
final class IEnumeratedValueConversionTypeSupport extends
        AbstractIValueConversionTypeSupport<IEnumeratedValue> {

    public IEnumeratedValueConversionTypeSupport() {
        super(IEnumeratedValue.class);
    }

    /**
     * {@inheritDoc}
     *
     * Kay's enumerated values have to have only a single value element corresponding to the set
     * enumerated value string. We want to archive the string, which yields the information, not the
     * index which doesn't speak for itself or might be changed in the system later on.
     *
     * CHECKSTYLE OFF: CyclomaticComplexity (accepted here as we'll get rid of I*Values anyway)
     */
    @Override
    @Nonnull
    protected EpicsSystemVariable<EpicsEnum> convertToSystemVariable(@Nonnull final String name,
                                                                     @Nonnull final IEnumeratedValue value,
                                                                     @Nullable final EpicsMetaData metaData) throws TypeSupportException {
        // This is a nice example for what happens when physicists 'design' programs.
        final int[] values = value.getValues();
        if (values == null || values.length <= 0) {
            throw new TypeSupportException("EnumeratedValue conversion failed, since IEnumeratedValue hasn't any values!", null);
        }

        final int index = values[0];
        final EpicsEnum enumState;
        /**
         * Take care. It might happen that we receive value updates (including the very first one for a 'successful' connection)
         * where the IEnumeratedValues do not contain data for the possible states (IEnumeratedMetaData is empty).
         * That leads to the situation with the values[0]=X field (typically X=0) being the only info
         * and the possible states are all unknown.
         * Hence, the only thing to do is creating an 'unknown' epics enum with the fields  (X,UNKNOWN,null).
         *
         * And the WTF/minute count is even higher than it already was...
         */
        if (metaData == null || metaData.getStates().isEmpty()) {
            final IEnumeratedMetaData iMetaData = value.getMetaData();

            String state = EpicsEnum.UNKNOWN_STATE;
            if (iMetaData != null) {
                final String[] states = iMetaData.getStates();
                if (states.length != 0 && index >=0 && index < states.length) {
                    state = states[index];
                }
            }
            enumState = EpicsEnum.create(index, state, null);
        } else {
            enumState = metaData.getState(index);
        }

        final EpicsAlarm alarm = EpicsIValueTypeSupport.toEpicsAlarm(value.getSeverity(), value.getStatus());
        final TimeInstant timestamp = BaseTypeConversionSupport.toTimeInstant(value.getTime());

        return new EpicsSystemVariable<EpicsEnum>(name,
                                                  new CssValueType<EpicsEnum>(enumState),
                                                  ControlSystem.EPICS_DEFAULT,
                                                  timestamp,
                                                  alarm);
    }

}
