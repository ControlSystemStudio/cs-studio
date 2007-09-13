package org.csstudio.platform.internal.xmlprefs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;


/**
 * Test class for XMLStore.
 * 
 * @author Andre Grunow
 * @version 0.1
 */
public class XMLStoreTest
	extends TestCase
{
	// --------------------------------------------------------------------------------------------
	
	/**
	 * The dummy pluginId.
	 */
	private final static String PLUGIN_ID = "org.csstudio.platform.core";
	
	// --------------------------------------------------------------------------------------------

	/**	This lsit contains all properties, that are set by a test and that shall be removed. */
	private List<String> propertiesSet = new ArrayList<String>();
	
	// --------------------------------------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp()
	{
	}

	// --------------------------------------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown()
	{
		XMLStore store = XMLStore.getInstance();
		
		for (Iterator iter = propertiesSet.iterator(); iter.hasNext();)
		{
			String property = (String) iter.next();
			store.removeProperty(PLUGIN_ID, property);
		}
		
		propertiesSet = new ArrayList<String>();
	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Test, if the store looks for the config file and create it, if it does not exist yet.
	 */
	public void testXMLStore()
	{
		XMLStore store = XMLStore.getInstance();
		assertNotNull("store not null!", store);
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Add a property that does not exist yet.
	 */
	public void testsetDefaultProperty()
	{
		XMLStore store = XMLStore.getInstance();
		store.setDefaultProperty(PLUGIN_ID, "someName", "someValue");
		assertEquals("value is \"someValue\"", "someValue", store.getPropertyValue(PLUGIN_ID, "someName", true));
	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Add many properties that do not exist yet.
	 */
	public void testAddManyProperties()
	{
		XMLStore store = XMLStore.getInstance();
		store.setDefaultProperty(PLUGIN_ID, "someName", "someValue");
		store.setDefaultProperty(PLUGIN_ID, "someOtherName", "someOtherValue");
		store.setDefaultProperty(PLUGIN_ID, "someNewName", "someNewValue");
		assertEquals("value is \"someValue\"", "someValue", store.getPropertyValue(PLUGIN_ID, "someName", true));
		assertEquals("value is \"someOtherValue\"", "someOtherValue", store.getPropertyValue(PLUGIN_ID, "someOtherName", true));
		assertEquals("value is \"someNewValue\"", "someNewValue", store.getPropertyValue(PLUGIN_ID, "someNewName", true));
	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Override an existing property.
	 */
	public void testOverrideProperty()
	{
		XMLStore store = XMLStore.getInstance();
		store.setDefaultProperty(PLUGIN_ID, "someName", "someValue");
		assertEquals("value is \"someValue\"", "someValue", store.getPropertyValue(PLUGIN_ID, "someName", true));
		
		store.setDefaultProperty(PLUGIN_ID, "someName", "someOtherValue");
		assertEquals("value is \"someOtherValue\"", "someOtherValue", store.getPropertyValue(PLUGIN_ID, "someName", true));
	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Testing getting a property.
	 */
	public void testGetProperty()
	{
		XMLStore store = XMLStore.getInstance();
		store.setDefaultProperty(PLUGIN_ID, "someName", "someValue");
		store.setDefaultProperty(PLUGIN_ID, "someOtherName", "someOtherValue");
		
		store.setProperty(PLUGIN_ID, "someName", "someNewValue");
		store.setProperty(PLUGIN_ID, "someOtherName", "someOtherNewValue");
		
		// get default values
		
		assertEquals("value is \"someValue\"", "someValue", store.getPropertyValue(PLUGIN_ID, "someName", true));
		assertEquals("value is \"someOtherValue\"", "someOtherValue", store.getPropertyValue(PLUGIN_ID, "someOtherName", true));

		// get current values
		
		assertEquals("value is \"someNewValue\"", "someNewValue", store.getPropertyValue(PLUGIN_ID, "someName", false));
		assertEquals("value is \"someOtherNewValue\"", "someOtherNewValue", store.getPropertyValue(PLUGIN_ID, "someOtherName", false));
	}
	
	// --------------------------------------------------------------------------------------------

	public void testRemoveProperty()
	{
		XMLStore store = XMLStore.getInstance();
		store.setDefaultProperty(PLUGIN_ID, "someName", "someValue");
		store.setDefaultProperty(PLUGIN_ID, "someOtherName", "otherValue");
		assertEquals("value is \"someValue\"", "someValue", store.getPropertyValue(PLUGIN_ID, "someName", true));
		assertEquals("value is \"otherValue\"", "otherValue", store.getPropertyValue(PLUGIN_ID, "someOtherName", true));
		
		// remove the someOtherName-Property

		store.removeProperty(PLUGIN_ID, "someOtherName");
		assertEquals("value is empty String", "", store.getPropertyValue(PLUGIN_ID, "someOtherName", false));
	}
}

