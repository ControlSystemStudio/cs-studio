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
package org.csstudio.archive.common.service.mysqlimpl.requesttypes;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.requesttype.AbstractArchiveRequestType;
import org.csstudio.archive.common.requesttype.IArchiveRequestType;
import org.csstudio.archive.common.requesttype.IArchiveRequestTypeParameter;
import org.csstudio.archive.common.requesttype.RequestTypeParameterException;

import com.google.common.collect.ImmutableSet;

/**
 * Archive request abstraction for optimized MySQL implementation.
 *
 * @author bknerr
 * @since 05.01.2011
 */
public enum DesyArchiveRequestType implements IArchiveRequestType {
    RAW("Raw values."),
    AVG_PER_MINUTE("Averaged over the time period of one minute."),
    AVG_PER_HOUR("Averaged over the time period of one hour.");

    static {
        AVG_PER_MINUTE._nextLowerOrderRequestType = RAW;
        AVG_PER_HOUR._nextLowerOrderRequestType = AVG_PER_MINUTE;
    }

    private final IArchiveRequestType _delegate;
    private DesyArchiveRequestType _nextLowerOrderRequestType;

    /**
     * Constructor.
     */
    private DesyArchiveRequestType(@Nonnull final String desc) {
        // Unfortunately enums cannot extend an abstract class, hence the delegator pattern.
        _delegate = new AbstractArchiveRequestType(name(), desc) {
            // EMPTY
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getDescription() {
        return _delegate.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getTypeIdentifier() {
        return _delegate.getTypeIdentifier();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ImmutableSet<IArchiveRequestTypeParameter<?>> getParameters() {
        return _delegate.getParameters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParameter(@Nonnull final String id, @Nonnull final Object newValue) throws RequestTypeParameterException {
        _delegate.setParameter(id, newValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParameter(@Nonnull final String id, @Nonnull final String newValue) throws RequestTypeParameterException {
        _delegate.setParameter(id, newValue);
    }

    @Nonnull
    public static DesyArchiveRequestType getDefault() {
        return RAW;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public <T> IArchiveRequestTypeParameter<T> getParameter(@Nonnull final String id,
                                                            @Nonnull final Class<T> clazz) throws RequestTypeParameterException {
        return _delegate.getParameter(id, clazz);
    }

    @Nonnull
    public static DesyArchiveRequestType valueOf(@Nonnull final IArchiveRequestType other) {

        if (other instanceof DesyArchiveRequestType) {
            return (DesyArchiveRequestType) other;
        }
        throw new IllegalArgumentException("Archive request type is not an instance of " +
                                           DesyArchiveRequestType.class.getName() +
                                           "!");
    }

    @CheckForNull
    public DesyArchiveRequestType getNextLowerOrderRequestType() {
        return _nextLowerOrderRequestType;
    }
}
