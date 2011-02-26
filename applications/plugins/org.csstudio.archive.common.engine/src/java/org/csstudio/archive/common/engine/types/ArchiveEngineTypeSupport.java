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
import org.csstudio.archive.common.engine.model.EngineModelException;
import org.csstudio.archive.common.engine.model.MonitoredArchiveChannel;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.domain.desy.epics.types.EpicsEnumTriple;
import org.csstudio.domain.desy.epics.types.EpicsIValueTypeSupport;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.pvmanager.TypeSupport;


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

    /**
     * Constructor.
     * @param type
     * @param typeSupportFamily
     */
    public ArchiveEngineTypeSupport(@Nonnull final Class<V> type) {
        super(type, ArchiveEngineTypeSupport.class);
    }


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
        public ConcreteArchiveEngineTypeSupport(@Nonnull final Class<V> type) {
            super(type);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        protected ArchiveChannel<V, IAlarmSystemVariable<V>>
            createArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException {

            MonitoredArchiveChannel<V, IAlarmSystemVariable<V>> channel;
            try {
                channel = new MonitoredArchiveChannel<V, IAlarmSystemVariable<V>>(cfg.getName(), cfg.getId());
            } catch (final EngineModelException e) {
                throw new TypeSupportException("Channel could not be instantiated.", e);
            }
            return channel;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        protected ArchiveChannel<Collection<V>, IAlarmSystemVariable<Collection<V>>>
            createMultiScalarArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException {

            MonitoredArchiveChannel<Collection<V>, IAlarmSystemVariable<Collection<V>>> channel;
            try {
                channel = new MonitoredArchiveChannel<Collection<V>, IAlarmSystemVariable<Collection<V>>>(cfg.getName(), cfg.getId());
            } catch (final EngineModelException e) {
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

        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Long>(Long.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Integer>(Integer.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Short>(Short.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Byte>(Byte.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Double>(Double.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Float>(Float.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<String>(String.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<EpicsEnumTriple>(EpicsEnumTriple.class));

        INSTALLED = true;
    }

    /**
     * @param channel_config
     * @return
     * @throws TypeSupportException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Nonnull
    public static <V>
    ArchiveChannel<V, IAlarmSystemVariable<V>> toArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException {

        final String dataType = cfg.getDataType();
        Class<V> typeClass = BaseTypeConversionSupport.createTypeClassFromString(dataType,
                                                                   SCALAR_TYPE_PACKAGES);
        boolean scalar = true;
        if (typeClass == null) {
            typeClass = BaseTypeConversionSupport.createTypeClassFromMultiScalarString(dataType,
                                                                                       SCALAR_TYPE_PACKAGES);
            if (typeClass == null) {
                throw new TypeSupportException("Data type " + dataType + " for channel " +
                                               cfg.getName() + " is unknown.", null);
            }
            scalar = false;
        }
        final ArchiveEngineTypeSupport<V> support =
            (ArchiveEngineTypeSupport<V>) findTypeSupportFor(ArchiveEngineTypeSupport.class, typeClass);

        if (scalar) {
            return support.createArchiveChannel(cfg);
        } else { // TODO (bknerr) : can it be supported throughout the service impl?
         // take care, V is here Collection<V>, the correct cast has to be performed by the invoker
            return (ArchiveChannel) support.createMultiScalarArchiveChannel(cfg);
        }

    }

    @Nonnull
    protected abstract ArchiveChannel<V, IAlarmSystemVariable<V>>
    createArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException;

    @Nonnull
    protected abstract ArchiveChannel<Collection<V>, IAlarmSystemVariable<Collection<V>>>
    createMultiScalarArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException;
}
