/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Map that keeps reference count for its objects
 *
 *  <p>Caller may need to synchronize calls to this map,
 *  because there is no atomic operation to check if an item exists,
 *  and if not, create and add it to the map.
 *
 *  @param <K> Key data type
 *  @param <E> Entry data type
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RefCountMap<K, E>
{
    /** Wrapper for an entry with reference count */
    public static class ReferencedEntry<E>
    {
        final private E entry;
        private int references = 1;

        ReferencedEntry(E entry)
        {
            this.entry = entry;
        }

        /** @return Item */
        public E getEntry()
        {
            return entry;
        }

        /** @return Reference count for the item */
        public int getReferences()
        {
            return references;
        }

        void addRef()
        {
            ++references;
        }

        int decRef()
        {
            return --references;
        }

        @Override
        public String toString()
        {
            return entry.toString() + " (" + references + " references)";
        }
    }

    final private Map<K, ReferencedEntry<E>> map = new HashMap<>();

    /** Get an item.
     *  On success, a reference count is added to the item.
     *  @param key Key for item to get
     *  @return Item or <code>null</code>
     */
    public E get(final K key)
    {
        final ReferencedEntry<E> entry = map.get(key);
        if (entry == null)
            return null;
        entry.addRef();
        return entry.getEntry();
    }

    /** Add item to map with initial reference count of 1
     *  @param key Item key
     *  @param entry The item to add
     */
    public void put(final K key, final E entry)
    {
        if (map.containsKey(key))
            throw new IllegalStateException("Already referenced " + key);
        map.put(key,  new ReferencedEntry<E>(entry));
    }

    /** Release an item from the map
     *  @param key Key for item to release
     *  @return Remaining reference counts. 0 if item has been removed from map.
     */
    public int release(final K key)
    {
        final ReferencedEntry<E> entry = map.get(key);
        if (entry == null)
            throw new IllegalStateException("No reference found for " + key);
        final int remaining = entry.decRef();
        if (remaining <= 0)
            map.remove(key);
        return remaining;
    }

    /** @return Entries in map */
    public Collection<ReferencedEntry<E>> getEntries()
    {
        return Collections.unmodifiableCollection(map.values());
    }
}
