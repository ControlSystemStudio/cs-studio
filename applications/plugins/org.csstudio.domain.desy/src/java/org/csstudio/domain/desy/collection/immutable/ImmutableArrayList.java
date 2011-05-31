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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Immutable collection.
 *
 * @param <T> Type of contained elements
 * @see java.util.ArrayList
 * @author Bastian Knerr (bastian.knerr@desy.de)
 */
public class ImmutableArrayList<T> extends ArrayList<T> implements IImmutableList<T> {

    private static final long serialVersionUID = 450475645815625237L;
    public static final IImmutableList<?> EMPTY_LIST = new ImmutableArrayList<Object>(0);

    public ImmutableArrayList(@Nonnull final Collection<? extends T> c) {
        super(c);
    }

    public ImmutableArrayList(@Nonnull final Iterable<? extends T> i) {
        final Iterator<? extends T> it = i.iterator();
        while (it.hasNext()) {
            final T type = it.next();
            add(type);
        }
    }

    private ImmutableArrayList() {
        super();
    }

    ImmutableArrayList(final int i) {
        super(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ImmutableIterator<T> iterator() {
        return new ImmutableIterator<T>(super.iterator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ImmutableListIterator<T> listIterator() {
        return new ImmutableListIterator<T>(super.listIterator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ImmutableListIterator<T> listIterator(final int index) {
        return new ImmutableListIterator<T>(super.listIterator(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(@Nonnull final IImmutableCollection<? extends T> c) {
        for (final T t : c) {
            if (!contains(t)) {
                return false;
            }
        }
        return true;
    }


//    private void addAll(@Nonnull final IImmutableCollection<? extends T> elements) {
//        for (final T e : elements) {
//            add(e);
//        }
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public T pollFirst() {
        return isEmpty()
            ? null
            : get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public IImmutableList<T> filter(@Nonnull final Filter<T> filter) {
        if (isEmpty()) {
            return ImmutableCollections.emptyList();
        }
        final ImmutableArrayList<T> result = new ImmutableArrayList<T>();
        for (final T item : this) {
            if (filter.filter(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public List<T> toList() {
        return new ArrayList<T>(this);
    }
}
