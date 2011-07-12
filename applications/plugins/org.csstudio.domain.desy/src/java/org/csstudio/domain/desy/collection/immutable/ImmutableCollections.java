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
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Static methods for collections.
 *
 * @author Bastian Knerr (bastian.knerr@desy.de)
 */
public final class ImmutableCollections {

    private static final IImmutableList<?> EMPTY_IMMUTABLE_LIST = ImmutableArrayList.EMPTY_LIST;
    //private static final IImmutableSet<?> EMPTY_IMMUTABLE_SET = ImmutableTreeSet.EMPTY_SET;
    private static final IImmutableMap<?, ?> EMPTY_IMMUTABLE_MAP = ImmutableTreeMap.EMPTY_MAP;

    private ImmutableCollections() {
        super();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> IImmutableList<T> emptyList() {
        return (IImmutableList<T>) EMPTY_IMMUTABLE_LIST;
    }

//    @Nonnull
//    @SuppressWarnings("unchecked")
//    public static <T> IImmutableSet<T> emptySet() {
//        return (IImmutableSet<T>) EMPTY_IMMUTABLE_SET;
//    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <K, V> IImmutableMap<K, V> emptyMap() {
        return (IImmutableMap<K, V>) EMPTY_IMMUTABLE_MAP;
    }

    @Nonnull
    public static <T> IImmutableList<T> singletonList(@Nonnull final T t) {
        final ImmutableArrayList<T> result = new ImmutableArrayList<T>(1);
        result.add(t);
        return result;
    }

//    @Nonnull
//    public static <T> IImmutableSet<T> singleton(@Nonnull final T t) {
//        final ImmutableTreeSet<T> result = new ImmutableTreeSet<T>();
//        result.add(t);
//        return result;
//    }

    @Nonnull
    public static <K, V> IImmutableMap<K, V> singletonMap(@Nonnull final K key, @Nonnull final V value) {
        final ImmutableTreeMap<K, V> result = new ImmutableTreeMap<K, V>();
        result.put(key, value);
        return result;
    }
//    @Nonnull
//    public static <K, V> IImmutableMap<K, IImmutableList<V>> listMap(@Nonnull final Map<K, List<V>> map) {
//        final Map<K, List<V>> intermediate = Maps.newHashMap(map);
//        for (final Entry<K, List<V>> entry : map.entrySet()) {
//            intermediate.put(entry.getKey(), new ImmutableArrayList<V>(entry.getValue()));
//        }
//        return new ImmutableHashMap<K, IImmutableList<V>>(intermediate);
//    }

    @CheckForNull
    public static <V> V firstValue(@Nonnull final IImmutableMap<?, V> map) {
        final Iterator<V> i = map.values().iterator();
        return i.hasNext()
            ? i.next()
            : null;
    }


    @CheckForNull
    public static <V> V firstValue(@Nonnull final IImmutableCollection<V> c) {
        final Iterator<V> i = c.iterator();
        return i.hasNext()
            ? i.next()
            : null;
    }


    public static boolean isEmpty(@Nullable final IImmutableCollection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(@Nullable final IImmutableMap<?, ?> map) {
        return map == null || map.isEmpty();
    }

    @Nonnull
    public static <T> List<T> asList(@Nullable final IImmutableList<T> items) {
        if (items == null) {
            return new ArrayList<T>();
        }
        return items.subList(0, items.size());
    }

    @Nonnull
    public static <T> List<T> asList(@Nullable final IImmutableCollection<T> items) {
        return asCollection(items, new ArrayList<T>());
    }

    @Nonnull
    public static <T, C extends Collection<T>> C asCollection(@Nullable final IImmutableCollection<T> items,
            @Nonnull final C collection) {
        if (items == null) {
            return collection;
        }
        for (final T t : items) {
            collection.add(t);
        }
        return collection;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> ArrayList<T> serializableCopyOf(@Nullable final IImmutableList<T> list) {
        return list == null
            ? new ArrayList<T>()
            : new ArrayList<T>((Collection<T>) list);
    }


    @Nonnull
    public static <E extends Enum<E>> EnumSet<E> enumSetOf(@Nonnull final Class<E> elementType,
            @Nullable final Iterable<E> collection) {
        final EnumSet<E> res = EnumSet.noneOf(elementType);
        if (collection == null) {
            return res;
        }
        for (final E element : collection) {
            res.add(element);
        }
        return res;
    }

}
