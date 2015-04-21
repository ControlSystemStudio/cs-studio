/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmEntityTest extends TestCase {
	
	EdmEntity	entity;
	String		id1 = "ATT_ID1";
	String		id2 = "ATT_ID2";
	String		id3 = "ATT_ID3";
	String		val1 = "VALUE_ONE";
	String		val2 = "VALUE_TWO";
	String		val3 = "VALUE_THREE";
	
	/**
	 * Creates basic instance of EdmAttribute class with sample element.
	 */
	public EdmAttribute setupAttribute(String val) {
		EdmAttribute a = new EdmAttribute(val);
		return a;
	}
	
	public void testAddAttribute() throws EdmException {
		
		entity = new EdmEntity("example.edl");
		
		// checking adding and getting attributes
		assertEquals("zero_count", 0, entity.getAttributeCount());
		
		EdmAttribute a1 = setupAttribute(val1);
		EdmAttribute a2 = setupAttribute(val2);
		entity.addAttribute(id1, a1);
		assertEquals("one_count", 1, entity.getAttributeCount());
		
		EdmAttribute checkAtt = entity.getAttribute(id1);
		assertEquals("check_value", a1, checkAtt);
		
		// checking whether attribute is read only!!!
		try {
			entity.addAttribute(id1, a2);
		}
		catch (EdmException e) {
			assertEquals(EdmException.ATTRIBUTE_ALREADY_EXISTS, e.getType());
		}
	}
		
	public void testAddSubEntity() throws EdmException {
		
		entity = new EdmEntity("example.edl");
		
		// add subEntity
		EdmEntity subE = new EdmEntity("SUBexample.edl");
		EdmAttribute a1 = setupAttribute(val1);
		subE.addAttribute(id1, a1);
		
		assertEquals("check_count", 0, entity.getSubEntityCount());
		entity.addSubEntity(subE);
		assertEquals("check_count", 1, entity.getSubEntityCount());
		
		EdmEntity checkSubE = entity.getSubEntity(0);
		assertEquals("check_object", subE, checkSubE);
	}
	
	public void testSetAttribute() throws EdmException {
		
		entity = new EdmEntity("example.edl");
		
		// protected setAtt & setSubE methods
		EdmAttribute a1 = setupAttribute(val1);
		EdmAttribute a3 = setupAttribute(val3);
		
		entity.addAttribute(id1, a1);
		assertEquals("check_old_value", a1, entity.getAttribute(id1));
		
		entity.setAttribute(id1, a3);
		assertEquals("check_new_value", a3, entity.getAttribute(id1));
	}
	
	public void testSetSubEntity() throws EdmException {
		
		entity = new EdmEntity("example.edl");
		
		EdmEntity subE = new EdmEntity("SUBexample.edl"); 
		EdmAttribute a1 = setupAttribute(val1);
		subE.addAttribute(id1, a1);
		
		entity.addSubEntity(subE);
		
		// setSubE
		EdmEntity subE2 = new EdmEntity("SUB2example.edl");
		EdmAttribute a3 = setupAttribute(val3);
		subE2.addAttribute(id3, a3);
		
		//check subentity
		assertEquals("check_old_object", subE, entity.getSubEntity(0));
		entity.setSubEntity(0, subE2);
		assertEquals("check_new_object", subE2, entity.getSubEntity(0));		
	}
	
	public void testCopyConstructor() throws EdmException {
		
		/* setting up an entity with 2 attr. and 1 subentity */
		entity = new EdmEntity("example.edl");
		EdmAttribute a1 = setupAttribute(val1);
		EdmAttribute a2 = setupAttribute(val2);
		entity.addAttribute(id1, a1);
		entity.addAttribute(id2, a2);
		// add subEntity
		EdmEntity subE = new EdmEntity("SUBexample.edl");
		EdmAttribute a3 = setupAttribute(val3);
		subE.addAttribute(id3, a3);
		entity.addSubEntity(subE);
		
		assertEquals("example.edl", entity.getType());
		assertEquals(2, entity.getAttributeCount());
		assertEquals(a1, entity.getAttribute(id1));
		assertEquals(a2, entity.getAttribute(id2));
		
		assertEquals(1, entity.getSubEntityCount());
		assertEquals("SUBexample.edl", entity.getSubEntity(0).getType());
		assertEquals(1, entity.getSubEntity(0).getAttributeCount());
		assertEquals(0, entity.getSubEntity(0).getSubEntityCount());
		assertEquals(a3, entity.getSubEntity(0).getAttribute(id3));
		
		
		EdmEntity copy = new EdmEntity(entity);
		
		assertEquals(entity.getType(), copy.getType());
		assertEquals(entity.getAttributeCount(), copy.getAttributeCount());
		for (String key : copy.getAttributeIdSet())
			assertEquals(entity.getAttribute(key), copy.getAttribute(key));
		
		assertEquals(entity.getSubEntityCount(), copy.getSubEntityCount());
		for (int i = 0; i < copy.getSubEntityCount(); i++) {
			assertEquals(entity.getSubEntity(i).getType(), copy.getSubEntity(i).getType());
			assertEquals(entity.getSubEntity(i).getAttributeCount(), 
					copy.getSubEntity(i).getAttributeCount());
			for (String key : copy.getSubEntity(i).getAttributeIdSet())
				assertEquals(entity.getSubEntity(i).getAttribute(key), 
						copy.getSubEntity(i).getAttribute(key));
		}
	}
}
