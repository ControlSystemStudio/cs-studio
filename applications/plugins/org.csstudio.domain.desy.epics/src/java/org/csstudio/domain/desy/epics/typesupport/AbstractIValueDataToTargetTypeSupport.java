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
 * AT HTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.desy.epics.typesupport;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.typesupport.AbstractTypeSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

/**
 * Type support to convert the stupid IValue 'values' data to a target type.
 *
 * @author bknerr
 * @since 18.08.2011
 * @param <T> the target type to convert the IValue.values into
 */
@SuppressWarnings("unused")
public abstract class AbstractIValueDataToTargetTypeSupport<T> extends AbstractTypeSupport<T> {
    /**
     * Constructor.
     */
    protected AbstractIValueDataToTargetTypeSupport(@Nonnull final Class<T> type) {
        super(type, AbstractIValueDataToTargetTypeSupport.class);
    }
    @Nonnull
    protected T fromStringValue(@Nonnull final String val) throws TypeSupportException {
        return throwTSE("String to " + getType() + " not supported.");
    }
    @Nonnull
    protected T fromLongValue(@Nonnull final Long val) throws TypeSupportException {
        return throwTSE("Long to " + getType() + " not supported.");
    }
    @Nonnull
    protected T fromEnumValue(final int index, @CheckForNull final EpicsMetaData meta) throws TypeSupportException {
        return throwTSE("IEnumValue to " + getType() + " not supported.");
    }
    @Nonnull
    protected T fromDoubleValue(@Nonnull final Double val) throws TypeSupportException {
        return throwTSE("Double to " + getType() + " not supported.");
    }
    @Nonnull
    private T throwTSE(@Nonnull final String msg) throws TypeSupportException {
        throw new TypeSupportException("Conversion from " + msg, null);
    }
}
