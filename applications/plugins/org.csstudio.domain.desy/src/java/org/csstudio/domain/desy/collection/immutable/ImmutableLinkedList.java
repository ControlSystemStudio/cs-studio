///*
// * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
// * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
// *
// * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
// * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
// * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
// * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
// * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
// * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
// * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
// * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
// * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
// * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
// * OR MODIFICATIONS.
// * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
// * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
// * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
// * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
// */
//package org.csstudio.domain.desy.collection.immutable;
//import java.util.Collection;
//import java.util.LinkedList;
//import java.util.List;
//
//import javax.annotation.Nonnull;
//
///**
// * Immutable linked list.
// * @author baschtl
// *
// * @param <T> the type of the contained elements
// */
//public class ImmutableLinkedList<T> extends LinkedList<T> implements IImmutableList<T> {
//
//    private static final long serialVersionUID = 3086966443157856480L;
//    public static final IImmutableList<?> EMPTY_LIST = new ImmutableLinkedList<Object>();
//
//    public ImmutableLinkedList() {
//        super();
//    }
//
//
//    public ImmutableLinkedList(@Nonnull final Collection<? extends T> c) {
//        super(c);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Nonnull
//    public ImmutableIterator<T> iterator() {
//        return new ImmutableIterator<T>(super.iterator());
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Nonnull
//    public ImmutableListIterator<T> listIterator() {
//        return new ImmutableListIterator<T>(super.listIterator());
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Nonnull
//    public ImmutableListIterator<T> listIterator(final int index) {
//        return new ImmutableListIterator<T>(super.listIterator(index));
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public boolean containsAll(@Nonnull final IImmutableCollection<? extends T> c) {
//        for (final T t : c) {
//            if (!contains(t)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Nonnull
//    @Override
//    public IImmutableList<T> filter(@Nonnull final Filter<T> filter) {
//        if (isEmpty()) {
//            return ImmutableCollections.emptyList();
//        }
//        final ImmutableLinkedList<T> result = new ImmutableLinkedList<T>();
//        for (final T item : this) {
//            if (filter.filter(item)) {
//                result.add(item);
//            }
//        }
//        return result;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Nonnull
//    public List<T> toList() {
//        return new LinkedList<T>(this);
//    }
//
//}
