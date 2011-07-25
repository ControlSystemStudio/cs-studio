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
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.epics.typesupport.EpicsIMetaDataTypeSupport;
import org.csstudio.domain.desy.epics.typesupport.EpicsIValueTypeSupport;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.typesupport.AbstractTypeSupport;
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
public abstract class ArchiveEngineTypeSupport<V> extends AbstractTypeSupport<V> {
    // CHECKSTYLE ON : AbstractClassName

    private static final String[] SCALAR_TYPE_PACKAGES =
        new String[]{"java.lang", "org.csstudio.domain.desy.epics.types"};

    private static boolean INSTALLED;

    /**
     * Constructor.
     * @param type
     * @param typeSupportFamily
     */
    public ArchiveEngineTypeSupport(@Nonnull final Class<V> type) {
        super(type, ArchiveEngineTypeSupport.class);
    }

    /**
     * Concrete implementation for this kind of type support.
     */
    private static final class ConcreteArchiveEngineTypeSupport<V> extends ArchiveEngineTypeSupport<V> {

        private final Class<V> _typeClass;

        /**
         * Constructor.
         */
        public ConcreteArchiveEngineTypeSupport(@Nonnull final Class<V> type) {
            super(type);
            _typeClass = type;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        protected ArchiveChannel<V, ISystemVariable<V>>
            createArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException {

            ArchiveChannel<V, ISystemVariable<V>> channel;
            try {
                channel = new ArchiveChannel<V, ISystemVariable<V>>(cfg.getName(),
                                                                    cfg.getId(),
                                                                    _typeClass);
            } catch (final EngineModelException e) {
                throw new TypeSupportException("Channel could not be instantiated.", e);
            }
            return channel;
        }
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        @Nonnull
        protected ArchiveChannel<Collection<V>, ISystemVariable<Collection<V>>>
            createMultiScalarArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException {

            ArchiveChannel<Collection<V>, ISystemVariable<Collection<V>>> channel;
            try {
                // FIXME (bknerr) : find solution for collection values - multiscalar wrapper?
                channel = new ArchiveChannel<Collection<V>, ISystemVariable<Collection<V>>>(cfg.getName(),
                                                                                            cfg.getId(),
                                                                                            (Class<Collection<V>>) _typeClass);
            } catch (final EngineModelException e) {
                throw new TypeSupportException("Channel could not be instantiated.", e);
            }
            return channel;
        }

    }


    @SuppressWarnings("rawtypes")
    public static void install() {
        if (INSTALLED) {
            return;
        }
        EpicsIValueTypeSupport.install();
        EpicsIMetaDataTypeSupport.install();

        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Long>(Long.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Integer>(Integer.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Short>(Short.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Byte>(Byte.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Double>(Double.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Float>(Float.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<String>(String.class));
        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<EpicsEnum>(EpicsEnum.class));

        TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport<Collection>(Collection.class));

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
    ArchiveChannel<V, ISystemVariable<V>> toArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException {

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
            (ArchiveEngineTypeSupport<V>) findTypeSupportForOrThrowTSE(ArchiveEngineTypeSupport.class,
                                                                       typeClass);

        if (scalar) {
            return support.createArchiveChannel(cfg);
        }
        // take care, V is here Collection<V>, the correct cast has to be performed by the invoker
        return (ArchiveChannel) support.createMultiScalarArchiveChannel(cfg);

    }

    @Nonnull
    protected abstract ArchiveChannel<V, ISystemVariable<V>>
    createArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException;

    @Nonnull
    protected abstract ArchiveChannel<Collection<V>, ISystemVariable<Collection<V>>>
    createMultiScalarArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException;
}
