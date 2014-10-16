package org.csstudio.archive.reader.fastarchiver;

import static org.junit.Assert.*;

import org.csstudio.archive.vtype.ArchiveVNumber;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.junit.Before;
import org.junit.Test;

public class FAValueIteratorTest {
	Timestamp now = Timestamp.now();
	int[] values = { 45, 6000, 9 };
	ArchiveVNumber[] nonEmptyInput;
	ArchiveVNumber[] emptyInput = new ArchiveVNumber[0];
	ArchiveVNumber[] nullInput = null;
	ArchiveVNumber[] singleInput = null;
	FAValueIterator faValIt;

	@Before
	public void setUpBeforeClass() throws Exception {
		nonEmptyInput = new ArchiveVNumber[values.length];
		singleInput = new ArchiveVNumber[] { new ArchiveVNumber(now,
				AlarmSeverity.NONE, "status", null, values[0]) };
		for (int i = 0; i < values.length; i++) {
			nonEmptyInput[i] = new ArchiveVNumber(now, AlarmSeverity.NONE,
					"status", null, values[i]);
		}
	}

	@Test
	public void testHasNextNullInput() {
		faValIt = new FAValueIterator(nullInput);
		assertFalse("hasNext() returns true for null input", faValIt.hasNext());
	}

	@Test
	public void testHasNextEmptyInput() {
		faValIt = new FAValueIterator(emptyInput);
		assertFalse("hasNext() returns true for input of zero length",
				faValIt.hasNext());
	}

	@Test
	public void testHasNextWithValidInput() {
		faValIt = new FAValueIterator(nonEmptyInput);
		assertTrue("hasNext() returns false, when it has more values",
				faValIt.hasNext());
	}

	@Test
	public void testHasNextAtEnd() {
		faValIt = new FAValueIterator(singleInput);
		assertTrue("hasNext() returns false, when it has one value left",
				faValIt.hasNext());
	}

	@Test
	public void testNextNullInput() {
		faValIt = new FAValueIterator(nullInput);
		boolean exceptionWhenNull = false;
		try {
			faValIt.next();
		} catch (Exception e) {
			exceptionWhenNull = true;
		}
		assertTrue("next() does not throw an Exception when input is null",
				exceptionWhenNull);
	}

	@Test
	public void testNextEmptyInput() {
		faValIt = new FAValueIterator(emptyInput);
		boolean exceptionWhenEmpty = false;
		try {
			faValIt.next();
		} catch (Exception e) {
			exceptionWhenEmpty = true;
		}
		assertTrue(
				"next() does not throw an Exception when input is of length 0",
				exceptionWhenEmpty);
	}

	@Test
	public void testNextWithValidInput() {
		faValIt = new FAValueIterator(nonEmptyInput);
		try {
			assertNotNull("next() returns null, when it has more values",
					faValIt.next());
		} catch (Exception e) {
			fail("next() throws an exception, when it has more values");
		}
	}

	@Test
	public void testNextAtEnd() {
		faValIt = new FAValueIterator(singleInput);
		try {
			assertNotNull("next() returns null, when it has one value left",
					faValIt.next());
		} catch (Exception e) {
			fail("next() throws an exception, when it has one value left");
		}
	}

//	@Test
//	public void testRemainingNullInput() {
//		// null
//		faValIt = new FAValueIterator(nullInput);
//		assertEquals(-1, faValIt.remaining());
//	}
//
//	@Test
//	public void testRemainingEmptyInput() {
//		// empty
//		faValIt = new FAValueIterator(emptyInput);
//		assertEquals(0, faValIt.remaining());
//	}
//
//	@Test
//	public void testRemainingWithValidInput() {
//		// valid
//		faValIt = new FAValueIterator(nonEmptyInput);
//		assertEquals(nonEmptyInput.length, faValIt.remaining());
//	}
//
//	@Test
//	public void testRemainingAtEnd() {
//		faValIt = new FAValueIterator(singleInput);
//		assertEquals(singleInput.length, faValIt.remaining());
//
//	}

}
