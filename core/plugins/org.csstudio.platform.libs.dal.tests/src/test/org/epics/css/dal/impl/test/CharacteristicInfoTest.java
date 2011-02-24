/**
 * 
 */
package org.epics.css.dal.impl.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.epics.css.dal.CharacteristicInfo;
import org.epics.css.dal.DoubleSeqProperty;
import org.epics.css.dal.DynamicValueProperty;

/**
 * @author ikriznar
 *
 */
public class CharacteristicInfoTest extends TestCase {

	public void testDefaultConstants() {
		
		CharacteristicInfo[] ci= CharacteristicInfo.getDefaultCharacteristics(DynamicValueProperty.class, null);
		Set<CharacteristicInfo> set= new HashSet<CharacteristicInfo>(Arrays.asList(ci));
		assertTrue(set.contains(CharacteristicInfo.C_DESCRIPTION));
		assertTrue(set.contains(CharacteristicInfo.C_POSITION));
		assertTrue(set.contains(CharacteristicInfo.C_DISPLAY_NAME));
		assertTrue(set.contains(CharacteristicInfo.C_PROPERTY_TYPE));
		
		for (int i = 0; i < ci.length; i++) {
			assertTrue(ci[i].getName(),!ci[i].isMeta());
		}
		
		ci= CharacteristicInfo.getDefaultCharacteristics(DoubleSeqProperty.class,null);
		set= new HashSet<CharacteristicInfo>(Arrays.asList(ci));
		assertTrue(set.contains(CharacteristicInfo.C_DESCRIPTION));
		assertTrue(set.contains(CharacteristicInfo.C_POSITION));
		assertTrue(set.contains(CharacteristicInfo.C_DISPLAY_NAME));
		assertTrue(set.contains(CharacteristicInfo.C_PROPERTY_TYPE));
		assertTrue(set.contains(CharacteristicInfo.C_ALARM_MAX));
		assertTrue(set.contains(CharacteristicInfo.C_FORMAT));
		assertTrue(set.contains(CharacteristicInfo.C_RESOLUTION));
		assertTrue(set.contains(CharacteristicInfo.C_UNITS));
		assertTrue(set.contains(CharacteristicInfo.C_SEQUENCE_LENGTH));
		
		for (int i = 0; i < ci.length; i++) {
			assertTrue(ci[i].getName(),!ci[i].isMeta());
		}
		
	}
}
