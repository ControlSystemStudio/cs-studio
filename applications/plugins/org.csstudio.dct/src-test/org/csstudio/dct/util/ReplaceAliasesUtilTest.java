/**
 * 
 */
package org.csstudio.dct.util;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Sven Wende
 * 
 */
public class ReplaceAliasesUtilTest {
	private Map<String, String> aliases;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		aliases = new HashMap<String, String>();
		aliases.put("a", "a$b$");
		aliases.put("b", "b$c$");
		aliases.put("c", "c$d$$e$");
		aliases.put("d", "d");
		aliases.put("e", "e");

	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.util.ReplaceAliasesUtil#createCanonicalName(java.lang.String, java.util.Map)}
	 * .
	 */
	@Test
	public final void testCreateCanonicalName() throws Exception {
		assertEquals("", ReplaceAliasesUtil.createCanonicalName("", aliases));
		assertEquals("a", ReplaceAliasesUtil.createCanonicalName("a", aliases));
		assertEquals("e", ReplaceAliasesUtil.createCanonicalName("$e$", aliases));
		assertEquals("d", ReplaceAliasesUtil.createCanonicalName("$d$", aliases));
		assertEquals("d_e", ReplaceAliasesUtil.createCanonicalName("$d$_$e$", aliases));
		assertEquals("cde", ReplaceAliasesUtil.createCanonicalName("$c$", aliases));
		assertEquals("bcde", ReplaceAliasesUtil.createCanonicalName("$b$", aliases));
		assertEquals("abcde", ReplaceAliasesUtil.createCanonicalName("$a$", aliases));
	}

}
