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
package org.csstudio.archive.common.requesttype.internal;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.requesttype.IArchiveRequestTypeParameter;
import org.csstudio.archive.common.requesttype.RequestTypeParameterException;

/**
 * An immutable parameter specifying an archive request type.
 *
 * Mandatory to implement toString() in case its value type doesn't feature already a reasonable
 * 'inHouse' toString implementation like Double or Integer
 *
 * @author bknerr
 * @since 19.01.2011
 * @param <T> the type of the value.
 *
 */
public abstract class AbstractArchiveRequestTypeParameter<T> implements IArchiveRequestTypeParameter<T> {

    private final String _name;

    private T _value;

    /**
     * Constructor.
     */
    public AbstractArchiveRequestTypeParameter(@Nonnull final String name,
                                               @Nonnull final T value) {
        _name = name;
        _value = value;
    }

    @Override
    @Nonnull
    public String getName() {
        return _name;
    }

    @Override
    @Nonnull
    public T getValue() {
        return _value;
    }

    /**
     * Set the value field to the new value.
     * In case the object is not an instance of the required type an exception is thrown.
     * @param newValue
     * @throws RequestTypeParameterException
     */
    @Override
    public void setValue(@Nonnull final T newValue) throws RequestTypeParameterException {
        final Class<?> clazz = newValue.getClass();
        if (getValueType() != clazz) {
            throw new RequestTypeParameterException("New value object's class type " + clazz.getName() +
                                                    " does not match the required type " + getValueType().getName(), null);
        }
        _value = getValueType().cast(newValue);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public Class<T> getValueType() {
        return (Class<T>) _value.getClass();
    }

}
