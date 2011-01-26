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
package org.csstudio.archive.common.engine.types;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.engine.model.ArchiveChannel;
import org.csstudio.archive.common.engine.model.MonitoredArchiveChannel;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.domain.desy.epics.types.EpicsEnumTriple;
import org.csstudio.domain.desy.epics.types.EpicsIValueTypeSupport;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.domain.desy.types.TypeSupport;
import org.csstudio.domain.desy.types.TypeSupportException;


/**
 * Converts a channel configuration with data type information into correctly typed archive channel
 * object.
 *
 * @author bknerr
 * @since 21.01.2011
 * @param <V> the type parameter for this support's discriminator
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class is accessed statically, hence the name should be short and descriptive!
 */
public abstract class ArchiveEngineTypeSupport<V> extends TypeSupport<V> {
    // CHECKSTYLE ON : AbstractClassName

    private static final String[] SCALAR_TYPE_PACKAGES =
        new String[]{"java.lang",
                     "org.csstudio.domain.desy.epics.types"};
    private static final String[] MULTI_SCALAR_TYPE_PACKAGES =
        new String[]{"java.util",
                     "org.csstudio.domain.desy.epics.types"};

    /**
     * Concrete implementation for this kind of type support.
     */
    private static final class ConcreteArchiveEngineTypeSupport<V> extends ArchiveEngineTypeSupport<V> {
        /**
         * Constructor.
         */
        public ConcreteArchiveEngineTypeSupport() {
            // Empty
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected ArchiveChannel<V, ICssAlarmValueType<V>> createArchiveChannel(final IArchiveChannel cfg) throws TypeSupportException {
            MonitoredArchiveChannel<V, ICssAlarmValueType<V>> channel;
            try {
                channel = new MonitoredArchiveChannel<V, ICssAlarmValueType<V>>(cfg.getName(), cfg.getId());
            } catch (final Exception e) {
                throw new TypeSupportException("Channel could not be instantiated.", e);
            }
            return channel;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected ArchiveChannel<Collection<V>, ICssAlarmValueType<Collection<V>>> createMultiScalarArchiveChannel(final IArchiveChannel cfg) throws TypeSupportException {
            MonitoredArchiveChannel<Collection<V>, ICssAlarmValueType<Collection<V>>> channel;
            try {
                channel = new MonitoredArchiveChannel<Collection<V>, ICssAlarmValueType<Collection<V>>>(cfg.getName(), cfg.getId());
            } catch (final Exception e) {
                throw new TypeSupportException("Channel could not be instantiated.", e);
            }
            return channel;
        }

    }

    private static boolean INSTALLED = false;

    public static void install() {
        if (INSTALLED) {
            return;
        }
        EpicsIValueTypeSupport.install();

        TypeSupport.addTypeSupport(Long.class, new ConcreteArchiveEngineTypeSupport<Long>());
        TypeSupport.addTypeSupport(Integer.class, new ConcreteArchiveEngineTypeSupport<Integer>());
        TypeSupport.addTypeSupport(Short.class, new ConcreteArchiveEngineTypeSupport<Short>());
        TypeSupport.addTypeSupport(Byte.class, new ConcreteArchiveEngineTypeSupport<Byte>());
        TypeSupport.addTypeSupport(Double.class, new ConcreteArchiveEngineTypeSupport<Double>());
        TypeSupport.addTypeSupport(Float.class, new ConcreteArchiveEngineTypeSupport<Float>());
        TypeSupport.addTypeSupport(String.class, new ConcreteArchiveEngineTypeSupport<String>());
        TypeSupport.addTypeSupport(EpicsEnumTriple.class, new ConcreteArchiveEngineTypeSupport<EpicsEnumTriple>());

        INSTALLED = true;
    }

    /**
     * @param channel_config
     * @return
     * @throws TypeSupportException
     */
    public static <V> ArchiveChannel<V, ICssAlarmValueType<V>>
    toArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException {

        final String dataType = cfg.getDataType();
        Class<V> typeClass = TypeSupport.createTypeClassFromString(dataType,
                                                                   SCALAR_TYPE_PACKAGES);
        boolean scalar = true;
        if (typeClass == null) {
            typeClass = TypeSupport.createTypeClassFromMultiScalarString(dataType,
                                                                         MULTI_SCALAR_TYPE_PACKAGES);
            if (typeClass == null) {
                throw new TypeSupportException("Data type " + dataType + " for channel " +
                                               cfg.getName() + " is unknown.", null);
            }
            scalar = false;
        }
        final ArchiveEngineTypeSupport<V> support =
            (ArchiveEngineTypeSupport<V>) cachedTypeSupportFor(ArchiveEngineTypeSupport.class,
                                                               typeClass);

        if (scalar) {
            return support.createArchiveChannel(cfg);
        }
//        else {
//         // take care, V is here Collection<V>, the correct cast has to be performed by the invoker
//            return (ArchiveChannel<V, ICssAlarmValueType<V>>) support.createMultiScalarArchiveChannel(cfg);
//        }
        return null;

    }

    @Nonnull
    protected abstract ArchiveChannel<V, ICssAlarmValueType<V>>
    createArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException;

    @Nonnull
    protected abstract ArchiveChannel<Collection<V>, ICssAlarmValueType<Collection<V>>>
    createMultiScalarArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException;


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public final Class<? extends TypeSupport<V>> getTypeSupportFamily() {
        return (Class<? extends TypeSupport<V>>) ArchiveEngineTypeSupport.class;
    }
}
