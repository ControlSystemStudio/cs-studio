/*
 * %W% %E% Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved. ORACLE
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.csstudio.saverestore.ui.util;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is a copy of {@link ArrayBlockingQueue} which works with {@link RunnableWithID}. The queue can contain
 * only one instance of the runnable with the same ID. If another instance is added which has the same ID, it will
 * replace the existing instance.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public final class IDBackedBlockingQueue extends AbstractQueue<Runnable> implements BlockingQueue<Runnable> {

    /** The queued items */
    private final RunnableWithID[] items;
    /** items index for next take, poll or remove */
    private int takeIndex;
    /** items index for next put, offer, or add. */
    private int putIndex;
    /** Number of items in the queue */
    private int count;
    /*
     * Concurrency control uses the classic two-condition algorithm found in any textbook.
     */
    /** Main lock guarding all access */
    private final ReentrantLock lock;
    /** Condition for waiting takes */
    private final Condition notEmpty;
    /** Condition for waiting puts */
    private final Condition notFull;

    // Internal helper methods
    /**
     * Circularly increment i.
     */
    private final int inc(int i) {
        return (i + 1 == items.length) ? 0 : i;
    }

    /**
     * Inserts element at current put position, advances, and signals. Call only when holding lock.
     */
    private void insert(Runnable x) {
        if (x instanceof RunnableWithID) {
            RunnableWithID r = (RunnableWithID) x;
            int id = r.getID();
            for (int i = items.length - 1; i > -1; i--) {
                if (items[i] == null)
                    continue;
                if (items[i].getID() == id) {
                    items[i] = r;
                    return;
                }
            }
            items[putIndex] = r;
            putIndex = inc(putIndex);
            ++count;
            notEmpty.signal();
        }
    }

    /**
     * Extracts element at current take position, advances, and signals. Call only when holding lock.
     */
    private RunnableWithID extract() {
        RunnableWithID x = items[takeIndex];
        items[takeIndex] = null;
        takeIndex = inc(takeIndex);
        --count;
        notFull.signal();
        return x;
    }

    /**
     * Utility for remove and iterator.remove: Delete item at position i. Call only when holding lock.
     */
    private void removeAt(int i) {
        // if removing front item, just advance
        if (i == takeIndex) {
            items[takeIndex] = null;
            takeIndex = inc(takeIndex);
        } else {
            // slide over all others up through putIndex.
            for (;;) {
                int nexti = inc(i);
                if (nexti != putIndex) {
                    items[i] = items[nexti];
                    i = nexti;
                } else {
                    items[i] = null;
                    putIndex = i;
                    break;
                }
            }
        }
        --count;
        notFull.signal();
    }

    /**
     * Creates an <tt>ArrayBlockingQueue</tt> with the given (fixed) capacity and default access policy.
     *
     * @param capacity the capacity of this queue
     * @throws IllegalArgumentException if <tt>capacity</tt> is less than 1
     */
    public IDBackedBlockingQueue(int capacity) {
        this(capacity, false);
    }

    /**
     * Creates an <tt>ArrayBlockingQueue</tt> with the given (fixed) capacity and the specified access policy.
     *
     * @param capacity the capacity of this queue
     * @param fair if <tt>true</tt> then queue accesses for threads blocked on insertion or removal, are processed in
     *            FIFO order; if <tt>false</tt> the access order is unspecified.
     * @throws IllegalArgumentException if <tt>capacity</tt> is less than 1
     */
    public IDBackedBlockingQueue(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        items = new RunnableWithID[capacity];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    /**
     * Creates an <tt>ArrayBlockingQueue</tt> with the given (fixed) capacity, the specified access policy and initially
     * containing the elements of the given collection, added in traversal order of the collection's iterator.
     *
     * @param capacity the capacity of this queue
     * @param fair if <tt>true</tt> then queue accesses for threads blocked on insertion or removal, are processed in
     *            FIFO order; if <tt>false</tt> the access order is unspecified.
     * @param c the collection of elements to initially contain
     * @throws IllegalArgumentException if <tt>capacity</tt> is less than <tt>c.size()</tt>, or less than 1.
     * @throws NullPointerException if the specified collection or any of its elements are null
     */
    public IDBackedBlockingQueue(int capacity, boolean fair, Collection<? extends RunnableWithID> c) {
        this(capacity, fair);
        if (capacity < c.size())
            throw new IllegalArgumentException();
        for (Iterator<? extends RunnableWithID> it = c.iterator(); it.hasNext();)
            add(it.next());
    }

    /**
     * Inserts the specified element at the tail of this queue if it is possible to do so immediately without exceeding
     * the queue's capacity, returning <tt>true</tt> upon success and <tt>false</tt> if this queue is full. This method
     * is generally preferable to method {@link #add}, which can fail to insert an element only by throwing an
     * exception.
     *
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean offer(Runnable e) {
        if (e == null)
            throw new NullPointerException();
        lock.lock();
        try {
            if (count == items.length)
                return false;
            else {
                insert(e);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element at the tail of this queue, waiting for space to become available if the queue is
     * full.
     *
     * @throws InterruptedException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void put(Runnable e) throws InterruptedException {
        if (e == null)
            throw new NullPointerException();
        lock.lockInterruptibly();
        try {
            try {
                while (count == items.length)
                    notFull.await();
            } catch (InterruptedException ie) {
                notFull.signal(); // propagate to non-interrupted thread
                throw ie;
            }
            insert(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element at the tail of this queue, waiting up to the specified wait time for space to
     * become available if the queue is full.
     *
     * @throws InterruptedException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean offer(Runnable e, long timeout, TimeUnit unit) throws InterruptedException {
        if (e == null)
            throw new NullPointerException();
        long nanos = unit.toNanos(timeout);
        lock.lockInterruptibly();
        try {
            for (;;) {
                if (count != items.length) {
                    insert(e);
                    return true;
                }
                if (nanos <= 0)
                    return false;
                try {
                    nanos = notFull.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    notFull.signal(); // propagate to non-interrupted thread
                    throw ie;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Queue#poll()
     */
    @Override
    public RunnableWithID poll() {
        lock.lock();
        try {
            if (count == 0)
                return null;
            return extract();
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.BlockingQueue#take()
     */
    @Override
    public RunnableWithID take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            try {
                while (count == 0)
                    notEmpty.await();
            } catch (InterruptedException ie) {
                notEmpty.signal(); // propagate to non-interrupted thread
                throw ie;
            }
            return extract();
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.BlockingQueue#poll(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public RunnableWithID poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        lock.lockInterruptibly();
        try {
            for (;;) {
                if (count != 0) {
                    return extract();
                }
                if (nanos <= 0)
                    return null;
                try {
                    nanos = notEmpty.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    notEmpty.signal(); // propagate to non-interrupted thread
                    throw ie;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Queue#peek()
     */
    @Override
    public RunnableWithID peek() {
        lock.lock();
        try {
            return (count == 0) ? null : items[takeIndex];
        } finally {
            lock.unlock();
        }
    }

    // this doc comment is overridden to remove the reference to collections
    // greater in size than Integer.MAX_VALUE
    /**
     * Returns the number of elements in this queue.
     *
     * @return the number of elements in this queue
     */
    @Override
    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    // this doc comment is a modified copy of the inherited doc comment,
    // without the reference to unlimited queues.
    /**
     * Returns the number of additional elements that this queue can ideally (in the absence of memory or resource
     * constraints) accept without blocking. This is always equal to the initial capacity of this queue less the current
     * <tt>size</tt> of this queue.
     * <p>
     * Note that you <em>cannot</em> always tell if an attempt to insert an element will succeed by inspecting
     * <tt>remainingCapacity</tt> because it may be the case that another thread is about to insert or remove an
     * element.
     */
    @Override
    public int remainingCapacity() {
        lock.lock();
        try {
            return items.length - count;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes a single instance of the specified element from this queue, if it is present. More formally, removes an
     * element <tt>e</tt> such that <tt>o.equals(e)</tt>, if this queue contains one or more such elements. Returns
     * <tt>true</tt> if this queue contained the specified element (or equivalently, if this queue changed as a result
     * of the call).
     *
     * @param o element to be removed from this queue, if present
     * @return <tt>true</tt> if this queue changed as a result of the call
     */
    @Override
    public boolean remove(Object o) {
        if (o == null)
            return false;
        lock.lock();
        try {
            int i = takeIndex;
            int k = 0;
            for (;;) {
                if (k++ >= count)
                    return false;
                if (o.equals(items[i])) {
                    removeAt(i);
                    return true;
                }
                i = inc(i);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns <tt>true</tt> if this queue contains the specified element. More formally, returns <tt>true</tt> if and
     * only if this queue contains at least one element <tt>e</tt> such that <tt>o.equals(e)</tt>.
     *
     * @param o object to be checked for containment in this queue
     * @return <tt>true</tt> if this queue contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        if (o == null)
            return false;
        lock.lock();
        try {
            int i = takeIndex;
            int k = 0;
            while (k++ < count) {
                if (o.equals(items[i]))
                    return true;
                i = inc(i);
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns an array containing all of the elements in this queue, in proper sequence.
     * <p>
     * The returned array will be "safe" in that no references to it are maintained by this queue. (In other words, this
     * method must allocate a new array). The caller is thus free to modify the returned array.
     * <p>
     * This method acts as bridge between array-based and collection-based APIs.
     *
     * @return an array containing all of the elements in this queue
     */
    @Override
    public Object[] toArray() {
        lock.lock();
        try {
            Object[] a = new Object[count];
            int k = 0;
            int i = takeIndex;
            while (k < count) {
                a[k++] = items[i];
                i = inc(i);
            }
            return a;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns an array containing all of the elements in this queue, in proper sequence; the runtime type of the
     * returned array is that of the specified array. If the queue fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the specified array and the size of this queue.
     * <p>
     * If this queue fits in the specified array with room to spare (i.e., the array has more elements than this queue),
     * the element in the array immediately following the end of the queue is set to <tt>null</tt>.
     * <p>
     * Like the {@link #toArray()} method, this method acts as bridge between array-based and collection-based APIs.
     * Further, this method allows precise control over the runtime type of the output array, and may, under certain
     * circumstances, be used to save allocation costs.
     * <p>
     * Suppose <tt>x</tt> is a queue known to contain only strings. The following code can be used to dump the queue
     * into a newly allocated array of <tt>String</tt>:
     *
     * <pre>
     *
     * String[] y = x.toArray(new String[0]);
     * </pre>
     *
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to <tt>toArray()</tt>.
     *
     * @param a the array into which the elements of the queue are to be stored, if it is big enough; otherwise, a new
     *            array of the same runtime type is allocated for this purpose
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of the runtime type of
     *             every element in this queue
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        lock.lock();
        try {
            if (a.length < count)
                a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), count);
            int k = 0;
            int i = takeIndex;
            while (k < count) {
                a[k++] = (T) items[i];
                i = inc(i);
            }
            if (a.length > count)
                a[count] = null;
            return a;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return super.toString();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Atomically removes all of the elements from this queue. The queue will be empty after this call returns.
     */
    @Override
    public void clear() {
        lock.lock();
        try {
            int i = takeIndex;
            int k = count;
            while (k-- > 0) {
                items[i] = null;
                i = inc(i);
            }
            count = 0;
            putIndex = 0;
            takeIndex = 0;
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public int drainTo(Collection<? super Runnable> c) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        lock.lock();
        try {
            int i = takeIndex;
            int n = 0;
            int max = count;
            while (n < max) {
                c.add(items[i]);
                items[i] = null;
                i = inc(i);
                ++n;
            }
            if (n > 0) {
                count = 0;
                putIndex = 0;
                takeIndex = 0;
                notFull.signalAll();
            }
            return n;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public int drainTo(Collection<? super Runnable> c, int maxElements) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        if (maxElements <= 0)
            return 0;
        lock.lock();
        try {
            int i = takeIndex;
            int n = 0;
            int max = (maxElements < count) ? maxElements : count;
            while (n < max) {
                c.add(items[i]);
                items[i] = null;
                i = inc(i);
                ++n;
            }
            if (n > 0) {
                count -= n;
                takeIndex = i;
                notFull.signalAll();
            }
            return n;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns an iterator over the elements in this queue in proper sequence. The returned <tt>Iterator</tt> is a
     * "weakly consistent" iterator that will never throw {@link ConcurrentModificationException}, and guarantees to
     * traverse elements as they existed upon construction of the iterator, and may (but is not guaranteed to) reflect
     * any modifications subsequent to construction.
     *
     * @return an iterator over the elements in this queue in proper sequence
     */
    @Override
    public Iterator<Runnable> iterator() {
        lock.lock();
        try {
            return new Itr();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Iterator for ArrayBlockingQueue
     */
    private class Itr implements Iterator<Runnable> {

        /**
         * Index of element to be returned by next, or a negative number if no such.
         */
        private int nextIndex;
        /**
         * nextItem holds on to item fields because once we claim that an element exists in hasNext(), we must return it
         * in the following next() call even if it was in the process of being removed when hasNext() was called.
         */
        private Runnable nextItem;
        /**
         * Index of element returned by most recent call to next. Reset to -1 if this element is deleted by a call to
         * remove.
         */
        private int lastRet;

        Itr() {
            lastRet = -1;
            if (count == 0)
                nextIndex = -1;
            else {
                nextIndex = takeIndex;
                nextItem = items[takeIndex];
            }
        }

        @Override
        public boolean hasNext() {
            /*
             * No sync. We can return true by mistake here only if this iterator passed across threads, which we don't
             * support anyway.
             */
            return nextIndex >= 0;
        }

        /**
         * Checks whether nextIndex is valid; if so setting nextItem. Stops iterator when either hits putIndex or sees
         * null item.
         */
        private void checkNext() {
            if (nextIndex == putIndex) {
                nextIndex = -1;
                nextItem = null;
            } else {
                nextItem = items[nextIndex];
                if (nextItem == null)
                    nextIndex = -1;
            }
        }

        @Override
        public Runnable next() {
            lock.lock();
            try {
                if (nextIndex < 0)
                    throw new NoSuchElementException();
                lastRet = nextIndex;
                Runnable x = nextItem;
                nextIndex = inc(nextIndex);
                checkNext();
                return x;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void remove() {
            lock.lock();
            try {
                int i = lastRet;
                if (i == -1)
                    throw new IllegalStateException();
                lastRet = -1;
                int ti = takeIndex;
                removeAt(i);
                // back up cursor (reset to front if was first element)
                nextIndex = (i == ti) ? takeIndex : i;
                checkNext();
            } finally {
                lock.unlock();
            }
        }
    }
}
