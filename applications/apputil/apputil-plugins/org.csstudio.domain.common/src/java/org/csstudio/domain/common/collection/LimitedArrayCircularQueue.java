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
package org.csstudio.domain.common.collection;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A non-blocking circular buffer queue with limited size and random access
 * to its elements.
 *
 * Copied from {@link java.util.concurrent.ArrayBlockingQueue}, but is
 * non blocking (all blocking methods are removed or replaced by their
 * non-blocking counterparts, hence building a circular buffer,
 * adhering to the {@link Queue} interface.
 *
 * Additionally a public accessor to the underlying
 * array is provided to permit random access on contained elements.
 *
 * @author Doug Lea (of the original {@link java.util.concurrent.ArrayBlockingQueue}
 * class.
 * @param <E> the type of elements held in this collection
 */
public class LimitedArrayCircularQueue<E> extends AbstractQueue<E>
        implements java.io.Serializable {

    /**
     * Serialization ID. This class relies on default serialization
     * even for the _items array, which is default-serialized, even if
     * it is empty. Otherwise it could not be declared final, which is
     * necessary here.
     */
    private static final long serialVersionUID = -5231254513294942267L;

    /** The queued _items  */
    private E[] _items;
    /** _items index for next take, poll or remove */
    private int _takeIndex;
    /** _items index for next put, offer, or add. */
    private int _putIndex;
    /** Number of _items in the queue */
    private int _count;

    /*
     * Concurrency control uses the classic two-condition algorithm
     * found in any textbook.
     */
    /** Main _lock guarding all access */
    private final ReentrantLock _lock;
    /** Condition for waiting takes */
    private final Condition _notEmpty;
    /** Condition for waiting puts */
    private final Condition _notFull;

    /**
     * Creates an <code>{@link LimitedArrayCircularQueue}</code> with the given (fixed)
     * capacity and default access policy.
     *
     * @param capacity the capacity of this queue
     * @throws IllegalArgumentException if <code>capacity</code> is less than 1
     */
    public LimitedArrayCircularQueue(final int capacity) {
        this(capacity, false);
    }

    /**
     * Creates an <code>{@link LimitedArrayCircularQueue}</code> with the given (fixed)
     * capacity and the specified access policy.
     *
     * @param capacity the capacity of this queue
     * @param fair if <code>true</code> then queue accesses for threads blocked
     *        on insertion or removal, are processed in FIFO order;
     *        if <code>false</code> the access order is unspecified.
     * @throws IllegalArgumentException if <code>capacity</code> is less than 1
     */
    @SuppressWarnings("unchecked")
    public LimitedArrayCircularQueue(final int capacity, final boolean fair) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        _items = (E[]) new Object[capacity];
        _lock = new ReentrantLock(fair);
        _notEmpty = _lock.newCondition();
        _notFull =  _lock.newCondition();
    }

    /**
     * Creates an <code>{@link LimitedArrayCircularQueue}</code> with the given (fixed)
     * capacity, the specified access policy and initially containing the
     * elements of the given collection,
     * added in traversal order of the collection's iterator.
     *
     * @param capacity the capacity of this queue
     * @param fair if <code>true</code> then queue accesses for threads blocked
     *        on insertion or removal, are processed in FIFO order;
     *        if <code>false</code> the access order is unspecified.
     * @param c the collection of elements to initially contain
     * @throws IllegalArgumentException if <code>capacity</code> is less than
     *         <code>c.size()</code>, or less than 1.
     * @throws NullPointerException if the specified collection or any
     *         of its elements are null
     */
    public LimitedArrayCircularQueue(final int capacity,
                                     final boolean fair,
                                     final Collection<? extends E> c) {
        this(capacity, fair);
        if (capacity < c.size()) {
            throw new IllegalArgumentException();
        }

        for (final E name : c) {
            add(name);
        }
    }

    /**
     * Circularly increment i.
     */
    private int inc(final int i) {
        final int ipp = i+1;
        return ipp == _items.length ? 0 : ipp;
    }

    /**
     * Inserts element at current put position, perhaps overriding the
     * element on tail, advances, and signals.
     * Call only when holding _lock.
     */
    private void insert(final E x) {
        if (_putIndex == _takeIndex) {
            if (_count == 0) {
                ++_count; // the yet empty corner case
            } else {
                _takeIndex = inc(_takeIndex); // the circular override corner case
            }
        } else {
            ++_count; // the normal case
        }
        _items[_putIndex] = x;
        _putIndex = inc(_putIndex);
        _notEmpty.signal();
    }

    /**
     * Extracts element at current take position, advances, and signals.
     * Call only when holding _lock.
     */
    private E extract() {
        final E[] items = _items;
        final E x = items[_takeIndex];
        items[_takeIndex] = null;
        _takeIndex = inc(_takeIndex);
        --_count;
        _notFull.signal();
        return x;
    }

    public int getCapacity() {
        return _items.length;
    }

    public void setCapacity(final int newCapacity) {
        _lock.lock();
        try {
            @SuppressWarnings("unchecked")
            final E[] newItems = (E[]) new Object[newCapacity];
            while (_count > newCapacity) {
                extract(); // remove all old samples that wouldn't have space in new items array
            }
            final int min = Math.min(_count, newCapacity);
            for (int i = 0; i < min; i++) {
                newItems[i] = extract();
            }
            _items = newItems;
            _count = min;
            _putIndex = min % _items.length;
            _takeIndex = 0;

        } finally {
            _lock.unlock();
        }
    }

    /**
     * Inserts the specified element at the tail of this queue, potentially overriding
     * another element - it's a non-blocking circular buffer!
     *
     * @param e the element to add
     * @return <code>true</code> if adding succeeded
     */
    @Override
    public boolean add(final E e) {
        return offer(e);
    }

    /**
     * Inserts the specified element at the tail of this queue, potentially overriding
     * another element - it's a non-blocking circular buffer!
     *
     * @param e the element to add
     * @return <code>true</code> if adding/offering succeeded
     */
    @Override
    public boolean offer(final E e) {
        _lock.lock();
        try {
            insert(e);
            return true;
        } finally {
            _lock.unlock();
        }
    }

    @Override
    public E poll() {
        _lock.lock();
        try {
            if (_count == 0) {
                return null;
            }
            return extract();
        } finally {
            _lock.unlock();
        }
    }

    @Override
    public E peek() {
        _lock.lock();
        try {
            return get(0);
        } finally {
            _lock.unlock();
        }
    }

    /**
     * Retrieves the element on position i.
     * @param i
     * @return the element on the position or <code>null</code> when i > size
     * @throws IllegalArgumentException on i smaller 0
     */
    public E get(final int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Index is smaller than 0.");
        }
        _lock.lock();
        try {
            if (i >= _count) {
                return null;
            }
            final int circI = (_takeIndex + i) % _items.length;
            return _items[circI];
        } finally {
            _lock.unlock();
        }
    }

    /**
     * Returns the number of elements in this queue.
     *
     * @return the number of elements in this queue
     */
    @Override
    public int size() {
        _lock.lock();
        try {
            return _count;
        } finally {
            _lock.unlock();
        }
    }

    /**
     * Guarantueed to throw an {@link UnsupportedOperationException}.
     */
    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException("Circular buffer mustn't remove arbitrary elements.");
    }

    /**
     * Guarantueed to throw an {@link UnsupportedOperationException}.
     */
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException("Circular buffer mustn't remove arbitrary elements.");
    }

    /**
     * Guarantueed to throw an {@link UnsupportedOperationException}.
     */
    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException("Circular buffer mustn't remove arbitrary elements.");
    }

    /**
     * Returns the number of additional elements that this queue can ideally
     * (in the absence of memory or resource constraints) accept without
     * overriding. This is always equal to the initial capacity of this queue
     * less the current <code>size</code> of this queue.
     *
     * <p>Note that you <em>cannot</em> always tell if an attempt to insert
     * an element will succeed by inspecting <code>remainingCapacity</code>
     * because it may be the case that another thread is about to
     * insert or remove an element.
     */
    public int remainingCapacity() {
        _lock.lock();
        try {
            return _items.length - _count;
        } finally {
            _lock.unlock();
        }
    }

    /**
     * Returns <code>true</code> if this queue contains the specified element.
     * More formally, returns <code>true</code> if and only if this queue contains
     * at least one element <code>e</code> such that <code>o.equals(e)</code>.
     *
     * @param o object to be checked for containment in this queue
     * @return <code>true</code> if this queue contains the specified element
     */
    @Override
    public boolean contains(final Object o) {

        final E[] items = _items;
        _lock.lock();
        if (_count <= 0 || !o.getClass().isAssignableFrom(items[_takeIndex].getClass())) {
            return false;
        }
        try {
            int i = _takeIndex;
            int k = 0;
            while (k++ < _count) {
                if (o.equals(items[i])) {
                    return true;
                }
                i = inc(i);
            }
            return false;
        } finally {
            _lock.unlock();
        }
    }

    /**
     * Returns an array containing all of the elements in this queue, in
     * proper sequence.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this queue.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this queue
     */
    @Override
    public Object[] toArray() {
        final E[] items = _items;
        _lock.lock();
        try {
            final Object[] a = new Object[_count];
            int k = 0;
            int i = _takeIndex;
            while (k < _count) {
                a[k++] = items[i];
                i = inc(i);
            }
            return a;
        } finally {
            _lock.unlock();
        }
    }

    /**
     * Returns an array containing all of the elements in this queue, in
     * proper sequence; the runtime type of the returned array is that of
     * the specified array. If the queue fits in the specified array, it
     * is returned therein. Otherwise, a new array is allocated with the
     * runtime type of the specified array and the size of this queue.
     *
     * <p>If this queue fits in the specified array with room to spare
     * (i.e., the array has more elements than this queue), the element in
     * the array immediately following the end of the queue is set to
     * <code>null</code>.
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose <code>x</code> is a queue known to contain only strings.
     * The following code can be used to dump the queue into a newly
     * allocated array of <code>String</code>:
     *
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that <code>toArray(new Object[0])</code> is identical in function to
     * <code>toArray()</code>.
     *
     * @param a the array into which the elements of the queue are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this queue
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(final T[] a) {
        final E[] items = _items;
        _lock.lock();
        try {
            T[] array = a;
            if (array.length < _count) {
                array = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(),
                                                                  _count);
            }

            int k = 0;
            int i = _takeIndex;
            while (k < _count) {
                array[k++] = (T) items[i];
                i = inc(i);
            }
            if (array.length > _count) {
                array[_count] = null;
            }
            return array;
        } finally {
            _lock.unlock();
        }
    }

    @Override
    public String toString() {
        _lock.lock();
        try {
            return super.toString();
        } finally {
            _lock.unlock();
        }
    }

    /**
     * Atomically removes all of the elements from this queue.
     * The queue will be empty after this call returns.
     */
    @Override
    public void clear() {
        final E[] items = _items;
        _lock.lock();
        try {
            int i = _takeIndex;
            int k = _count;
            while (k-- > 0) {
                items[i] = null;
                i = inc(i);
            }
            _count = 0;
            _putIndex = 0;
            _takeIndex = 0;
            _notFull.signalAll();
        } finally {
            _lock.unlock();
        }
    }

    /**
     * @throws UnsupportedOperationException
     * @throws ClassCastException
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public int drainTo(final Collection<? super E> c) {
        if (c == this) {
            throw new IllegalArgumentException();
        }
        final E[] items = _items;
        _lock.lock();
        try {
            int i = _takeIndex;
            int n = 0;
            final int max = _count;
            while (n < max) {
                c.add(items[i]);
                items[i] = null;
                i = inc(i);
                ++n;
            }
            if (n > 0) {
                _count = 0;
                _putIndex = 0;
                _takeIndex = 0;
                _notFull.signalAll();
            }
            return n;
        } finally {
            _lock.unlock();
        }
    }

    /**
     * @throws UnsupportedOperationException
     * @throws ClassCastException
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public int drainTo(final Collection<? super E> c,
                       final int maxElements) {
        if (c == this) {
            throw new IllegalArgumentException();
        }
        if (maxElements <= 0) {
            return 0;
        }
        final E[] items = _items;
        _lock.lock();
        try {
            int i = _takeIndex;
            int n = 0;
            final int max = maxElements < _count ? maxElements : _count;
            while (n < max) {
                c.add(items[i]);
                items[i] = null;
                i = inc(i);
                ++n;
            }
            if (n > 0) {
                _count -= n;
                _takeIndex = i;
                _notFull.signalAll();
            }
            return n;
        } finally {
            _lock.unlock();
        }
    }


    /**
     * Returns an iterator over the elements in this queue in proper sequence.
     * The returned <code>Iterator</code> is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException},
     * and guarantees to traverse elements as they existed upon
     * construction of the iterator, and may (but is not guaranteed to)
     * reflect any modifications subsequent to construction.
     *
     * @return an iterator over the elements in this queue in proper sequence
     */
    @Override
    public Iterator<E> iterator() {
        _lock.lock();
        try {
            return new Itr();
        } finally {
            _lock.unlock();
        }
    }

    /**
     * Iterator for {@link LimitedArrayCircularQueue}.
     * see {@link java.util.concurrent.ArrayBlockingQueue.Itr}
     */
    @SuppressWarnings("synthetic-access")
    private class Itr implements Iterator<E> {
        /**
         * Index of element to be returned by next,
         * or a negative number if no such.
         */
        private int _nextIndex;

        /**
         * _nextItem holds on to item fields because once we claim
         * that an element exists in hasNext(), we must return it in
         * the following next() call even if it was in the process of
         * being removed when hasNext() was called.
         */
        private E _nextItem;

        /**
         * Constructor.
         */
        Itr() {
            if (_count == 0) {
                _nextIndex = -1;
            } else {
                _nextIndex = _takeIndex;
                _nextItem = _items[_takeIndex];
            }
        }

        @Override
        public boolean hasNext() {
            /*
             * No sync. We can return true by mistake here
             * only if this iterator passed across threads,
             * which we don't support anyway.
             */
            return _nextIndex >= 0;
        }

        /**
         * Checks whether _nextIndex is valid; if so setting _nextItem.
         * Stops iterator when either hits _putIndex or sees null item.
         */
        private void checkNext() {
            if (_nextIndex == _putIndex) {
                _nextIndex = -1;
                _nextItem = null;
            } else {
                _nextItem = _items[_nextIndex];
                if (_nextItem == null) {
                    _nextIndex = -1;
                }
            }
        }

        @Override
            public E next() {
            final ReentrantLock lock = LimitedArrayCircularQueue.this._lock;
            lock.lock();
            try {
                if (_nextIndex < 0) {
                    throw new NoSuchElementException();
                }
                final E x = _nextItem;
                _nextIndex = inc(_nextIndex);
                checkNext();
                return x;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removing from the circular buffer is not permitted.");
        }
    }
}
