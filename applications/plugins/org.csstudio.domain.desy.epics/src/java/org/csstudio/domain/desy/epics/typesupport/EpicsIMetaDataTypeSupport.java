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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.typesupport.AbstractTypeSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.pvmanager.TypeSupport;

/**
 * Meta data support.
 *
 * @author bknerr
 * @since Mar 4, 2011
 * @param <T> the concrete type of the meta data.
 *
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!

 */
public abstract class EpicsIMetaDataTypeSupport<T> extends AbstractTypeSupport<T> {
 // CHECKSTYLE ON : AbstractClassName
    private static boolean INSTALLED;

    /**
     * Constructor for a new EpicsIMetaData support.
     *
     * @param type the supported type
     */
    public EpicsIMetaDataTypeSupport(@Nonnull final Class<T> type) {
        super(type, EpicsIMetaDataTypeSupport.class);
    }

    // CHECKSTYLE OFF : MethodLength
    public static void install() {
        if (INSTALLED) {
            return;
        }
        TypeSupport.addTypeSupport(new AbstractNumberIMetaDataTypeSupport<Byte>(Byte.class) {
            @Override
            @Nonnull
            protected Byte toNumber(final double d) {
                return Byte.valueOf(Double.valueOf(d).byteValue());
            }
        });
        TypeSupport.addTypeSupport(new AbstractNumberIMetaDataTypeSupport<Double>(Double.class) {
            @Override
            @Nonnull
            protected Double toNumber(final double d) {
                return Double.valueOf(d);
            }
        });
        TypeSupport.addTypeSupport(new AbstractNumberIMetaDataTypeSupport<Float>(Float.class) {
            @Override
            @Nonnull
            protected Float toNumber(final double d) {
                return Float.valueOf(Double.valueOf(d).floatValue());
            }
        });
        TypeSupport.addTypeSupport(new AbstractNumberIMetaDataTypeSupport<Integer>(Integer.class) {
            @Override
            @Nonnull
            protected Integer toNumber(final double d) {
                return Integer.valueOf(Double.valueOf(d).intValue());
            }
        });
        TypeSupport.addTypeSupport(new AbstractNumberIMetaDataTypeSupport<Long>(Long.class) {
            @Override
            @Nonnull
            protected Long toNumber(final double d) {
                return Long.valueOf(Double.valueOf(d).longValue());
            }
        });
        TypeSupport.addTypeSupport(new AbstractNumberIMetaDataTypeSupport<Short>(Short.class) {
            @Override
            @Nonnull
            protected Short toNumber(final double d) {
                return Short.valueOf(Double.valueOf(d).shortValue());
            }
        });

        TypeSupport.addTypeSupport(new EpicsEnumConversionSupport());
        TypeSupport.addTypeSupport(new CollectionConversionSupport());

        INSTALLED = true;
    }
    // CHECKSTYLE ON : MethodLength


    @SuppressWarnings("unchecked")
    @CheckForNull
    public static <T extends IMetaData>
    EpicsMetaData toMetaData(@Nonnull final T meta,
                             @Nonnull final Class<?> valueClazz) throws TypeSupportException {

        final EpicsIMetaDataTypeSupport<T> support =
            (EpicsIMetaDataTypeSupport<T>) findTypeSupportForOrThrowTSE(EpicsIMetaDataTypeSupport.class,
                                                                        valueClazz);
        return support.convertToMetaData(meta);
    }

    @Nonnull
    protected abstract EpicsMetaData convertToMetaData(@Nonnull final IMetaData data) throws TypeSupportException;


    @Nonnull
    protected INumericMetaData checkAndConvertToNumeric(@Nonnull final IMetaData meta,
                                                        @Nonnull final Class<?> typeClass) throws TypeSupportException {
        if (!(meta instanceof INumericMetaData)) {
            throw new TypeSupportException("Metadata is not of numeric type. Conversion failed for type " +
                                           typeClass.getName(), null);
        }
        return (INumericMetaData) meta;
    }
    @Nonnull
    protected IEnumeratedMetaData checkAndConvertToEnumerated(@Nonnull final IMetaData meta,
                                                              @Nonnull final Class<?> typeClass) throws TypeSupportException {
        if (!(meta instanceof IEnumeratedMetaData)) {
            throw new TypeSupportException("Metadata is not of enumerated type. Conversion failed for type " +
                                           typeClass.getName(), null);
        }
        return (IEnumeratedMetaData) meta;
    }
}
