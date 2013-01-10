package org.csstudio.domain.common.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

/**
 * Test of the chunkable collection
 * 
 * @author jpenning
 * @author $Author: jpenning $
 * @version $Revision: 1.1 $
 * @since 31.08.2010
 */
public class ChunkableCollectionUnitTest {

	@SuppressWarnings("unused")
	@Test(expected = AssertionError.class)
	public void testNoChunks() {
		new ChunkableCollection<Object>(new ArrayList<Object>(), 0);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testNoRemove() {
		ChunkableCollection<Object> chunkColl = new ChunkableCollection<Object>(
				new ArrayList<Object>(), 1);
		Iterator<Collection<Object>> iter = chunkColl.iterator();
		iter.remove();
	}

	@Test
	public void testNoElements() {
		// the collection has no elements, they are iterated in chunks of 2
		Collection<Object> sourceCollection = new ArrayList<Object>();
		ChunkableCollection<Object> chunkColl = new ChunkableCollection<Object>(
				sourceCollection, 2);

		Iterator<Collection<Object>> iter = chunkColl.iterator();
		assertFalse(iter.hasNext());
	}

	@Test
	public void testSingleChunk() {
		// the collection has 3 elements, they are iterated in a single chunk of
		// size 10
		Collection<Object> sourceCollection = new ArrayList<Object>();
		Object element0 = new Object();
		Object element1 = new Object();
		Object element2 = new Object();
		sourceCollection.add(element0);
		sourceCollection.add(element1);
		sourceCollection.add(element2);
		ChunkableCollection<Object> chunkColl = new ChunkableCollection<Object>(
				sourceCollection, 10);

		Iterator<Collection<Object>> iter = chunkColl.iterator();

		assertTrue(iter.hasNext());
		Collection<Object> chunk1 = iter.next();
		assertEquals(3, chunk1.size());
		Iterator<Object> iterator = chunk1.iterator();
		assertEquals(element0, iterator.next());
		assertEquals(element1, iterator.next());
		assertEquals(element2, iterator.next());

		assertFalse(iter.hasNext());
	}

	// CHECKSTYLE:OFF
	//disable checking for duplicated lines
	@Test
	public void testMatchingChunks() {
		// the collection has 4 elements, they are iterated in chunks of 2
		Collection<Object> sourceCollection = new ArrayList<Object>();
		Object element0 = new Object();
		Object element1 = new Object();
		Object element2 = new Object();
		Object element3 = new Object();
		sourceCollection.add(element0);
		sourceCollection.add(element1);
		sourceCollection.add(element2);
		sourceCollection.add(element3);
		ChunkableCollection<Object> chunkColl = new ChunkableCollection<Object>(
				sourceCollection, 2);

		Iterator<Collection<Object>> iter = chunkColl.iterator();

		assertTrue(iter.hasNext());
		Collection<Object> chunk1 = iter.next();
		assertEquals(2, chunk1.size());
		assertEquals(element0, chunk1.iterator().next());

		assertTrue(iter.hasNext());
		Collection<Object> chunk2 = iter.next();
		assertEquals(2, chunk2.size());
		assertEquals(element2, chunk2.iterator().next());

		assertFalse(iter.hasNext());
	}

	// CHECKSTYLE:ON

	@Test
	public void testNonMatchingChunks() {
		// the collection has 5 elements, they are iterated in chunks of 2
		Collection<Object> sourceCollection = new ArrayList<Object>();
		Object element0 = new Object();
		Object element1 = new Object();
		Object element2 = new Object();
		Object element3 = new Object();
		Object element4 = new Object();
		sourceCollection.add(element0);
		sourceCollection.add(element1);
		sourceCollection.add(element2);
		sourceCollection.add(element3);
		sourceCollection.add(element4);
		ChunkableCollection<Object> chunkColl = new ChunkableCollection<Object>(
				sourceCollection, 2);

		Iterator<Collection<Object>> iter = chunkColl.iterator();

		assertTrue(iter.hasNext());
		Collection<Object> chunk1 = iter.next();
		assertEquals(2, chunk1.size());
		assertEquals(element0, chunk1.iterator().next());

		assertTrue(iter.hasNext());
		Collection<Object> chunk2 = iter.next();
		assertEquals(2, chunk2.size());
		assertEquals(element2, chunk2.iterator().next());

		assertTrue(iter.hasNext());
		Collection<Object> chunk3 = iter.next();
		assertEquals(1, chunk3.size());
		assertEquals(element4, chunk3.iterator().next());

		assertFalse(iter.hasNext());
	}

	@Test
	public void testCalcChunkCount() {
		ChunkableCollection<Object> chunkableCollection = new ChunkableCollection<Object>(
				new ArrayList<Object>(), 1);

		// wholeSize is 0, so there is nothing to do
		assertEquals(0, chunkableCollection.calcChunkCount(0, 0));
		assertEquals(0, chunkableCollection.calcChunkCount(0, 1));
		assertEquals(0, chunkableCollection.calcChunkCount(0, 10));

		// chunkSize is 0, so all will be in one chunk
		assertEquals(1, chunkableCollection.calcChunkCount(1, 0));
		assertEquals(1, chunkableCollection.calcChunkCount(2, 0));
		assertEquals(1, chunkableCollection.calcChunkCount(10, 0));

		// all fit in one chunk
		assertEquals(1, chunkableCollection.calcChunkCount(1, 1));
		assertEquals(1, chunkableCollection.calcChunkCount(1, 10));
		assertEquals(1, chunkableCollection.calcChunkCount(2, 10));
		assertEquals(1, chunkableCollection.calcChunkCount(10, 10));

		// now we need another chunk
		assertEquals(2, chunkableCollection.calcChunkCount(11, 10));
	}

}
