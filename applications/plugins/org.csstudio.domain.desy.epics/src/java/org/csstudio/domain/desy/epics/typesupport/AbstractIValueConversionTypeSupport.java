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
package org.csstudio.domain.desy.epics.typesupport;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.csstudio.platform.logging.CentralLogger;
import org.epics.pvmanager.TypeSupport;


/**
 * Type Conversion Support for IValue types. Be careful, due to the IValue design there isn't any
 * type safety.
 *
 * @author bknerr
 * @since 02.12.2010
 * @param <R> the basic type of the value(s) of the system variable
 * @param <T> the type of the system variable
 */
public abstract class AbstractIValueConversionTypeSupport<T extends IValue>
    extends EpicsIValueTypeSupport<T> {

    static final Logger LOG =
        CentralLogger.getInstance().getLogger(AbstractIValueConversionTypeSupport.class);

    private static boolean INSTALLED = false;

    /**
     * Constructor.
     */
    AbstractIValueConversionTypeSupport(@Nonnull final Class<T> clazz) {
        super(clazz);
    }

    public static void install() {
        if (INSTALLED) {
            return;
        }
        TypeSupport.addTypeSupport(new IDoubleValueConversionTypeSupport());
        TypeSupport.addTypeSupport(new IEnumeratedValueConversionTypeSupport());
        TypeSupport.addTypeSupport(new ILongValueConversionTypeSupport());
        TypeSupport.addTypeSupport(new IStringValueConversionTypeSupport());

        INSTALLED = true;
    }

    @Nonnull
    protected abstract EpicsSystemVariable<?> convertToSystemVariable(@Nonnull final String name,
                                                                      @Nonnull final T value,
                                                                      @Nullable final EpicsMetaData metaData) throws TypeSupportException;
}
