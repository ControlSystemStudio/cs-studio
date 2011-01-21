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
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.mysqlimpl.adapter.ByteArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.mysqlimpl.adapter.CollectionTypeConversionSupport;
import org.csstudio.archive.common.service.mysqlimpl.adapter.DoubleArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.mysqlimpl.adapter.EnumArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.mysqlimpl.adapter.FloatArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.mysqlimpl.adapter.IntegerArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.mysqlimpl.adapter.LongArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.mysqlimpl.adapter.StringArchiveTypeConversionSupport;
import org.csstudio.domain.desy.epics.types.EpicsCssValueTypeSupport;
import org.csstudio.domain.desy.epics.types.EpicsEnumTriple;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.domain.desy.types.TypeSupport;
import org.csstudio.domain.desy.types.TypeSupportException;


/**
 * Converts a channel configuration with data type information into correctly typed archive channel
 * object. 
 * 
 * @author bknerr
 * @since 21.01.2011
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class is accessed statically, hence the name should be short and descriptive!
 */
public abstract class ArchiveEngineTypeSupport<T> extends TypeSupport<T> {
    // CHECKSTYLE ON : AbstractClassName
    
    private static boolean INSTALLED = false;
    
    public static void install() {
        if (INSTALLED) {
            return;
        }
        TypeSupport.addTypeSupport(Double.class, new ArchiveEngineTypeSupport() {

            @Override
            protected ArchiveChannel createArchiveChannel(IArchiveChannel cfg) throws TypeSupportException {
                
                
                ArchiveChannel<Double> channel = new ArchiveChannel<Double>(cfg.getName(),
                                                                            cfg.getGroupId());
                return channel;
            }

            @Override
            protected ArchiveChannel createMultiScalarArchiveChannel(IArchiveChannel cfg) throws TypeSupportException {
                // TODO Auto-generated method stub
                return null;
            }
            
        });

        INSTALLED = true;
    }    

    /**
     * @param channel_config
     * @return
     * @throws TypeSupportException 
     */
    @SuppressWarnings("unchecked")
    public static <T> ArchiveChannel<T> toArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException {
        String dataType = cfg.getDataType();
        Class<T> typeClass = TypeSupport.createTypeClassFromString(dataType,
                                                                   "java.util",
                                                                   "org.csstudio.domain.desy.epics.types");
        boolean scalar = true;
        if (typeClass == null) {
            typeClass = TypeSupport.createTypeClassFromMultiScalarString(dataType, 
                                                                         "java.lang",
                                                                         "org.csstudio.domain.desy.epics.types");
            if (typeClass == null) {
                throw new TypeSupportException("Data type " + dataType + " for channel " + 
                                               cfg.getName() + " is unknown.", null);
            }
            scalar = false;
        }
        final ArchiveEngineTypeSupport<T> support =
            (ArchiveEngineTypeSupport<T>) cachedTypeSupportFor(ArchiveEngineTypeSupport.class,
                                                               typeClass);
            
        if (scalar) {
            return support.createArchiveChannel(cfg);
        } else {
         // take care, T is here Collection<T>, the correct cast has to be performed by the invoker
            return (ArchiveChannel<T>) support.createMultiScalarArchiveChannel(cfg); 
        }
        
        
    }
    
    @Nonnull
    protected abstract ArchiveChannel<T> createArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException;
    @Nonnull
    protected abstract ArchiveChannel<Collection<T>> createMultiScalarArchiveChannel(@Nonnull final IArchiveChannel cfg) throws TypeSupportException;

    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public final Class<? extends TypeSupport<T>> getTypeSupportFamily() {
        return (Class<? extends TypeSupport<T>>) ArchiveEngineTypeSupport.class;
    }
}
