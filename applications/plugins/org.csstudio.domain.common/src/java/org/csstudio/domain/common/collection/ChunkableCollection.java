package org.csstudio.domain.common.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;

/**
 * Helper class to divide a collection into iterable chunks.
 * You set the chunk size and the collection, you get an iterator whose elements are subcollections.
 *
 * @author jpenning
 * @author $Author: jpenning $
 * @version $Revision: 1.1 $
 * @since 31.08.2010
 *
 * @param <E> type of collection elements
 */
public class ChunkableCollection<E> implements Iterable<Collection<E>> {

    private final Collection<E> _collection;
    private final int _chunkSize;
    private final int _chunkCount;
    private int _currentChunk;

    public ChunkableCollection(@Nonnull final Collection<E> collection, final int chunkSize) {
        assert chunkSize > 0 : "chunkSize must be > 0, but was " + chunkSize;

        _collection = collection;
        _chunkSize = chunkSize;
        _currentChunk = 0;
        _chunkCount = calcChunkCount(collection.size(), chunkSize);
    }

    @SuppressWarnings("synthetic-access")
    @Override
    @Nonnull
    public final Iterator<Collection<E>> iterator() {
        return new MyIterator();
    }

    // package scoped for test
    final int calcChunkCount(int wholeSize, int chunkSize) {
        assert wholeSize >= 0 : "wholeSize must be >= 0, but was " + wholeSize;
        assert chunkSize >= 0 : "chunkSize must be >= 0, but was " + chunkSize;

        int result = 0;
        if (wholeSize == 0) {
            result = 0;
        } else if (chunkSize == 0) {
            result = 1;
        } else {
            result = wholeSize / chunkSize;
            if (wholeSize % chunkSize > 0) {
                result++;
            }
        }
        return result;
    }

    /**
     * the iterator delivers subcollections as elements
     */
    @SuppressWarnings("synthetic-access")
    private class MyIterator implements Iterator<Collection<E>> {

        @Override
        public boolean hasNext() {
            return _currentChunk < _chunkCount;
        }

        @Override
        public Collection<E> next() {
            Collection<E> result = new ArrayList<E>(_chunkSize);

            Iterator<E> iter = _collection.iterator();
            skipAlreadyProcessedChunks(iter);
            copyOneChunk(iter, result);

            _currentChunk++;
            return result;
        }

        private void skipAlreadyProcessedChunks(@Nonnull final Iterator<E> iter) {
            int to = _currentChunk * _chunkSize;
            for (int i = 0; i < to; i++) {
                iter.next();
            }
        }

        private void copyOneChunk(@Nonnull final Iterator<E> iter, @Nonnull final Collection<E> result) {
            for (int i = 0; i < calcChunkSize(); i++) {
                result.add(iter.next());
            }
        }

        private int calcChunkSize() {
			int result = _chunkSize;
            if ( (_currentChunk + 1) == _chunkCount) {
                int rest = _collection.size() % _chunkSize;
                result = rest == 0 ? _chunkSize : rest;
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Iterator of "
                    + ChunkableCollection.class.getName() + " cannot remove");
        }

    }

}
