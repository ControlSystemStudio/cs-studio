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

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Ints;

/**
 * System variable conversion support for {@link EpicsEnum}.
 *
 * @author bknerr
 * @since 11.05.2011
 */
final class EpicsEnumSystemVariableSupport extends EpicsSystemVariableSupport<EpicsEnum> {
    /**
     * Constructor.
     */
    public EpicsEnumSystemVariableSupport() {
        super(EpicsEnum.class);
    }

    @Override
    @Nonnull
    protected IValue convertEpicsSystemVariableToIValue(@Nonnull final EpicsSystemVariable<EpicsEnum> sysVar) {
        return ValueFactory.createEnumeratedValue(BaseTypeConversionSupport.toTimestamp(sysVar.getTimestamp()),
                                                  EpicsIValueTypeSupport.toSeverity(sysVar.getAlarm().getSeverity()),
                                                  sysVar.getAlarm().getStatus().toString(),
                                                  null,
                                                  null,
                                                  new int[] {sysVar.getData().getRaw().intValue()});
    }

    @Override
    @Nonnull
    protected IValue convertCollectionToIValue(@Nonnull final Collection<EpicsEnum> data,
                                               @Nonnull final EpicsAlarm alarm,
                                               @Nonnull final TimeInstant timestamp) {
        final Collection<Integer> ints =
            Collections2.transform(data,
                                   new Function<EpicsEnum, Integer> () {
                @Override
                @Nonnull
                public Integer apply(@Nonnull final EpicsEnum from) {
                    if (from.isRaw()) {
                        return from.getRaw();
                    }
                    if (from.isState()) {
                        return from.getStateIndex();
                    }
                    throw new IllegalStateException(EpicsEnum.class.getName() + " is neither raw nor state instance.");
                }
            });
        final Collection<String> states =
            Collections2.transform(data,
                                   new Function<EpicsEnum, String> () {
                @Override
                @Nonnull
                public String apply(@Nonnull final EpicsEnum from) {
                    if (from.isRaw()) {
                        return from.getRaw().toString();
                    }
                    if (from.isState()) {
                        return from.getState();
                    }
                    throw new IllegalStateException(EpicsEnum.class.getName() + " is neither raw nor state instance.");
                }
            });
        return ValueFactory.createEnumeratedValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                  EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                  alarm.getStatus().toString(),
                                                  ValueFactory.createEnumeratedMetaData(states.toArray(new String[]{})),
                                                  null,
                                                  Ints.toArray(ints));
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Nonnull
//    protected EpicsSystemVariable<Collection<EpicsEnum>> createCollectionEpicsVariable(@Nonnull final String name,
//                                                                                       @Nonnull final Class<?> typeClass,
//                                                                                       @Nonnull final Collection<EpicsEnum> values,
//                                                                                       @Nonnull final ControlSystem system,
//                                                                                       @Nonnull final TimeInstant timestamp) throws TypeSupportException {
//        try {
//            @SuppressWarnings("unchecked")
//            final Collection<EpicsEnum> newCollection = (Collection<EpicsEnum>) typeClass.newInstance();
//            for (final EpicsEnum epicsEnum : values) {
//                newCollection.add(epicsEnum);
//            }
//            return new EpicsSystemVariable<Collection<EpicsEnum>>(name, newCollection, system, timestamp, EpicsAlarm.UNKNOWN);
//        } catch (final InstantiationException e) {
//            throw new TypeSupportException("Collection type could not be instantiated from Class<?> object.", e);
//        } catch (final IllegalAccessException e) {
//            throw new TypeSupportException("Collection type could not be instantiated from Class<?> object.", e);
//        }
//    }
}
