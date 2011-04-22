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
package org.csstudio.archive.common.requesttype;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.requesttype.internal.AbstractArchiveRequestTypeParameter;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Abstract class handling the set of request types with access to the mutable type objects.
 *
 * @author bknerr
 * @since 26.01.2011
 */
public abstract class AbstractArchiveRequestType implements IArchiveRequestType {

    private final String _id;
    private final String _desc;
    private final Map<String, IArchiveRequestTypeParameter<?>> _paramMap;

    /**
     * Constructor.
     */
    public AbstractArchiveRequestType(@Nonnull final String id,
                                      @Nonnull final String desc) {

        _id = id;
        _desc = desc;
        _paramMap = Collections.emptyMap();
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("rawtypes")
    public AbstractArchiveRequestType(@Nonnull final String id,
                                      @Nonnull final String desc,
                                      @Nonnull final IArchiveRequestTypeParameter<?>... params) {
        _id = id;
        _desc = desc;
        if (params.length == 0) {
            _paramMap = Collections.emptyMap();
        } else {
            _paramMap = Maps.newHashMap();
            for (final IArchiveRequestTypeParameter param : params) {
                _paramMap.put(param.getName(), (IArchiveRequestTypeParameter<?>) param.deepCopy());

            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getDescription() {
        return _desc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getTypeIdentifier() {
        return _id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ImmutableSet<IArchiveRequestTypeParameter<?>> getParameters() {
        return ImmutableSet.<IArchiveRequestTypeParameter<?>>builder().addAll(_paramMap.values()).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public void setParameter(@Nonnull final String id, @Nonnull final Object newValue) throws RequestTypeParameterException {

        @SuppressWarnings("unchecked")
        final IArchiveRequestTypeParameter<Object> param = (IArchiveRequestTypeParameter<Object>) getParameterById(id);
        setParameterValue(newValue, param);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public void setParameter(@Nonnull final String id, @Nonnull final String newStrValue) throws RequestTypeParameterException {

        @SuppressWarnings("unchecked")
        final IArchiveRequestTypeParameter<Object> param = (IArchiveRequestTypeParameter<Object>) getParameterById(id);
        final Object newValue = param.toValue(newStrValue);
        setParameterValue(newValue, param);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <T> IArchiveRequestTypeParameter<T> getParameter(@Nonnull final String id,
                                                            @Nonnull final Class<T> clazz) throws RequestTypeParameterException {
        @SuppressWarnings("unchecked")
        final AbstractArchiveRequestTypeParameter<T> param =
            (AbstractArchiveRequestTypeParameter<T>) getParameterById(id);
        if (param.getValueType() != clazz) {
            throw new RequestTypeParameterException("Parameter with id " + id +
                                                    " either does not exist or does not have the correct class type",
                                                    null);
        }
        return param.deepCopy();
    }

    private <T> void setParameterValue(@Nonnull final T newValue,
                                       @Nonnull final IArchiveRequestTypeParameter<T> param) throws RequestTypeParameterException {
        final T oldValue = param.getValue();
        if (newValue.equals(oldValue)) {
            return; // identity
        }
        param.setValue(newValue);
    }

    /**
     * Returns the parameter with the given id, if it is present in the map.
     *
     * @param id
     * @return
     * @throws RequestTypeParameterException
     */
    @Nonnull
    private IArchiveRequestTypeParameter<?> getParameterById(@Nonnull final String id) throws RequestTypeParameterException {
        final IArchiveRequestTypeParameter<?> param = _paramMap.get(id);
        if (param == null) {
            throw new RequestTypeParameterException("Parameter with identifying name " + id + " unknown.", null);
        }
        return param;
    }
}
