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
package org.csstudio.domain.desy.collection.immutable;
import java.util.ListIterator;

import javax.annotation.Nullable;

/**
 * Immutable list iterator. Guaranteed to throw {@link UnsupportedOperationException}s on
 * modifying methods.
 * @author baschtl
 *
 * @param <T> type of the iterables
 */
public abstract class AbstractImmutableListIterator<T> extends AbstractImmutableIterator<T> implements ListIterator<T> {

    /**
     * {@inheritDoc}
     *
     * Guaranteed to throw {@link UnsupportedOperationException} on invoking
     * {@link java.util.ListIterator#add(Object)}.
     */
    @Override
    public void add(@Nullable final T e) {
        throw new UnsupportedOperationException("ImmutableListIterator");
    }

    /**
     * {@inheritDoc}
     *
     * Guaranteed to throw {@link UnsupportedOperationException} on invoking
     * {@link java.util.ListIterator#set(Object)}.
     */
    @Override
    public void set(@Nullable final T e) {
        throw new UnsupportedOperationException("ImmutableListIterator");
    }

}
