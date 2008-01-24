package org.csstudio.sds.ui.internal.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.ui.internal.editor.AliasInitializationDialog.Columns;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Hofer
 * @version $Revision$
 *
 */
public final class AliasInitializationDialogTest {

	/**
	 * Test fixture.
	 */
	private Map<String, String> _aliases;
	
	/**
	 * The instance under test.
	 */
	private AliasInitializationDialog _dialog;

	/**
	 * Create test fixture.
	 * 
	 */
	@Before
	public void setUp() {
		_aliases = new HashMap<String, String>();
		_aliases.put("alias1", ""); //$NON-NLS-2$ //$NON-NLS-3$
		_aliases.put("alias2", "someValue"); //$NON-NLS-2$ //$NON-NLS-3$
		_aliases.put("alias3", ""); //$NON-NLS-2$ //$NON-NLS-3$
		
		_dialog = new AliasInitializationDialog(null, _aliases);
		_dialog.createDialogArea(new Shell()); // initializes static fields
	}

	/**
	 * Tests the getter.
	 *
	 */
	@Test
	public void testGetAliasDescriptors() {
		assertEquals(_aliases, _dialog.getAliasDescriptors());
	}
	
	/**
	 * Test the inner class {@link AliasInitializationDialog}.
	 *
	 */
	@Test
	public void testCellModifierCanModify() {
		AliasInitializationDialog.AliasTableCellModifier modifier = new AliasInitializationDialog.AliasTableCellModifier();
		assertFalse(modifier.canModify(null, Columns.first.toString()));
		assertTrue(modifier.canModify(null, Columns.second.toString()));
	}
	
	/**
	 * Test the inner class {@link AliasInitializationDialog}.
	 *
	 */
	@Test
	public void testCellModifierGetValue() {
		AliasInitializationDialog.AliasTableCellModifier modifier = new AliasInitializationDialog.AliasTableCellModifier();
		String key = _aliases.keySet().toArray(new String[_aliases.keySet().size()])[0];
		String value = _aliases.get(key);
		assertEquals(key, modifier.getValue(key, Columns.first.toString()));
		assertEquals(value, modifier.getValue(key, Columns.second.toString()));
	}
	
	/**
	 * Test the inner class {@link AliasInitializationDialog}.
	 *
	 */
	@Test
	public void testCellModifierModify() {
		AliasInitializationDialog.AliasTableCellModifier modifier = new AliasInitializationDialog.AliasTableCellModifier();
		String key = _aliases.keySet().toArray(new String[_aliases.keySet().size()])[0];
		
		final String oldValue = _aliases.get(key);
		assertEquals(oldValue, modifier.getValue(key, Columns.second.toString()));
		
		final String newValue = "aNewValue"; //$NON-NLS-1$
		assertFalse(oldValue.equals(newValue));
		
		modifier.modify(key, Columns.second.toString(), newValue);
		assertEquals(newValue, modifier.getValue(key, Columns.second.toString()));
	}

}
