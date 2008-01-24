/**
 * 
 */
package org.csstudio.sds.internal.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;

import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ChannelReference}.
 * 
 * @author Sven Wende
 * 
 */
public final class ChannelReferenceTest {

	/**
	 * A test channel.
	 */
	private ChannelReference _reference1;

	/**
	 * A test channel.
	 */
	private ChannelReference _reference2;

	/**
	 * A test channel.
	 */
	private ChannelReference _reference3;
	
	/**
	 * A test channel.
	 */
	private ChannelReference _reference4;
	
	/**
	 * A test channel.
	 */
	private ChannelReference _reference5;

	/**
	 */
	@Before
	public void setUp() {
		_reference1 = new ChannelReference("A", Integer.class);
		_reference2 = new ChannelReference("A", Integer.class);
		_reference3 = new ChannelReference("B", Integer.class);
		_reference4 = new ChannelReference("A", Double.class);
		_reference5 = new ChannelReference("XX_$VAR$_XX", Double.class);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.internal.connection.ChannelReference#getRawChannelName()}.
	 */
	@Test
	public void testGetRawChannelName() {
		assertEquals("A", _reference1.getRawChannelName());
		assertEquals("A", _reference2.getRawChannelName());
		assertEquals("B", _reference3.getRawChannelName());
		assertEquals("A", _reference4.getRawChannelName());
		assertEquals("XX_$VAR$_XX", _reference5.getRawChannelName());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.internal.connection.ChannelReference#getCanonicalName(java.util.HashMap)}.
	 */
	@Test
	public void testGetCanonicalName() {
		HashMap<String, String> aliases = new HashMap<String, String>();
		aliases.put("VAR", "YYY");
		aliases.put("ANY", "ZZZ");
		
		try {
			assertEquals("A", _reference1.getCanonicalName(aliases));
		} catch (ChannelReferenceValidationException e) {
			assertFalse(true);
		}
		
		try {
			assertEquals("A", _reference2.getCanonicalName(aliases));
		} catch (ChannelReferenceValidationException e) {
			assertFalse(true);
		}
		
		try {
			assertEquals("B", _reference3.getCanonicalName(aliases));
		} catch (ChannelReferenceValidationException e) {
			assertFalse(true);
		}
		
		try {
			assertEquals("A", _reference4.getCanonicalName(aliases));
		} catch (ChannelReferenceValidationException e) {
			assertFalse(true);
		}
		
		try {
			assertEquals("XX_YYY_XX", _reference5.getCanonicalName(aliases));
		} catch (ChannelReferenceValidationException e) {
			assertFalse(true);
		}
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.internal.connection.ChannelReference#getType()}.
	 */
	@Test
	public void testGetType() {
		assertEquals(_reference1.getType(), Integer.class);
		assertEquals(_reference2.getType(), Integer.class);
		assertEquals(_reference3.getType(), Integer.class);
		assertEquals(_reference4.getType(), Double.class);
		assertEquals(_reference5.getType(), Double.class);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.internal.connection.ChannelReference#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertEquals(_reference1,_reference2);
		//TODO: HIER WEIDA
	}

}
