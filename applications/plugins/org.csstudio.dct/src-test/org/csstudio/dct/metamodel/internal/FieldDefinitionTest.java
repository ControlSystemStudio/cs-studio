/**
 * 
 */
package org.csstudio.dct.metamodel.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link FieldDefinition}.
 * 
 * @author Sven Wende
 * 
 */
public class FieldDefinitionTest {

	FieldDefinition fieldDefinition;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		fieldDefinition = new FieldDefinition("PREC", "DBF_SHORT");
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.metamodel.internal.FieldDefinition#FieldDefinition(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testFieldDefinition() {
		assertEquals("DBF_SHORT", fieldDefinition.getType());
		assertEquals("PREC", fieldDefinition.getName());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.metamodel.internal.FieldDefinition#getExtra()}.
	 */
	@Test
	public final void testGetSetExtra() {
		assertNull(fieldDefinition.getExtra());
		String extra = "epicsMutexId	mlok";
		fieldDefinition.setExtra(extra);
		assertEquals(extra, fieldDefinition.getExtra());

	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.metamodel.internal.FieldDefinition#getInitial()}.
	 */
	@Test
	public final void testGetSetInitial() {
		assertNull(fieldDefinition.getInitial());
		String initial = "1";
		fieldDefinition.setInitial(initial);
		assertEquals(initial, fieldDefinition.getInitial());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.metamodel.internal.FieldDefinition#getInterest()}.
	 */
	@Test
	public final void testGetSetInterest() {
		assertNull(fieldDefinition.getInterest());
		String interest = "1";
		fieldDefinition.setInterest(interest);
		assertEquals(interest, fieldDefinition.getInterest());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.metamodel.internal.FieldDefinition#getMenu()}.
	 */
	@Test
	public final void testGetSetMenu() {
		assertNull(fieldDefinition.getMenu());
		MenuDefinition menu = new MenuDefinition("menu");
		fieldDefinition.setMenuDefinition(menu);
		assertEquals(menu, fieldDefinition.getMenu());

	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.metamodel.internal.FieldDefinition#getName()}.
	 */
	@Test
	public final void testGetName() {
		assertEquals("PREC", fieldDefinition.getName());

	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.metamodel.internal.FieldDefinition#getPrompt()}.
	 */
	@Test
	public final void testGetSetPrompt() {
		assertNull(fieldDefinition.getPrompt());
		String prompt = "Access Security Group";
		fieldDefinition.setPrompt(prompt);
		assertEquals(prompt, fieldDefinition.getPrompt());

	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.metamodel.internal.FieldDefinition#getPromptGroup()}.
	 */
	@Test
	public final void testGetSetPromptGroup() {
		assertNull(fieldDefinition.getPromptGroup());
		String promptgroup = "GUI_COMMON";
		fieldDefinition.setPromptGroup(promptgroup);
		assertEquals(promptgroup, fieldDefinition.getPromptGroup());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.metamodel.internal.FieldDefinition#getSize()}.
	 */
	@Test
	public final void testGetSetSize() {
		assertNull(fieldDefinition.getSize());
		String size = "29";
		fieldDefinition.setSize(size);
		assertEquals(size, fieldDefinition.getSize());

	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.metamodel.internal.FieldDefinition#getSpecial()}.
	 */
	@Test
	public final void testGetSetSpecial() {
		assertNull(fieldDefinition.getSpecial());
		String special = "SPC_NOMOD";
		fieldDefinition.setSpecial(special);
		assertEquals(special, fieldDefinition.getSpecial());

	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.metamodel.internal.FieldDefinition#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals("DBF_SHORT", fieldDefinition.getType());
	}

}
