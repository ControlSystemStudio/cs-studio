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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.engine.model.ArchiveChannel;
import org.csstudio.archive.common.engine.model.EngineModelException;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.domain.desy.epics.typesupport.EpicsIMetaDataTypeSupport;
import org.csstudio.domain.desy.epics.typesupport.EpicsIValueTypeSupport;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.typesupport.AbstractTypeSupport;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.pvmanager.TypeSupport;

import com.google.common.collect.Sets;


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
@SuppressWarnings("unchecked")
public abstract class ArchiveEngineTypeSupport<V extends Serializable> extends AbstractTypeSupport<V> {
    // CHECKSTYLE ON : AbstractClassName

    private static final String[] ADDITIONAL_TYPE_PACKAGES =
        new String[]{
                     "org.csstudio.domain.desy.epics.types",
                     };

    private static final Set<Class<?>> BASIC_TYPES =
        Sets.<Class<?>>newHashSet(Long.class,
                                  Integer.class,
                                  Short.class,
                                  Byte.class,
                                  Double.class,
                                  Float.class,
                                  String.class,
                                  ArrayList.class,
                                  LinkedList.class,
                                  EnumSet.class,
                                  HashSet.class,
                                  TreeSet.class,
                                  Stack.class,
                                  Vector.class);

    private static boolean INSTALLED;

    /**
     * Constructor.
     */
    public ArchiveEngineTypeSupport(@Nonnull final Class<V> type) {
        super(type, ArchiveEngineTypeSupport.class);
    }

    /**
     * Concrete implementation for this kind of type support.
     */
    private static final class ConcreteArchiveEngineTypeSupport<V extends Serializable> extends ArchiveEngineTypeSupport<V> {

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
        protected ArchiveChannel<V, ISystemVariable<V>>
            createChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException {

            ArchiveChannel<V, ISystemVariable<V>> channel;
            try {
                channel = new ArchiveChannel<V, ISystemVariable<V>>(cfg.getName(),
                                                                    cfg.getId(),
                                                                    getType());
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

        for (final Class<?> clazz : BASIC_TYPES) {
            TypeSupport.addTypeSupport(new ConcreteArchiveEngineTypeSupport(clazz));
        }

        INSTALLED = true;
    }

    @Nonnull
    public static <V extends Serializable>
    ArchiveChannel<V, ISystemVariable<V>> createArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException {

        final String dataType = cfg.getDataType();
        final Class<V> typeClass =
            BaseTypeConversionSupport.createBaseTypeClassFromString(dataType,
                                                                    ADDITIONAL_TYPE_PACKAGES);
        final ArchiveEngineTypeSupport<V> support =
            (ArchiveEngineTypeSupport<V>) findTypeSupportForOrThrowTSE(ArchiveEngineTypeSupport.class,
                                                                       typeClass);
        return support.createChannel(cfg);
    }

    @Nonnull
    protected abstract ArchiveChannel<V, ISystemVariable<V>>
    createChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException;
}
