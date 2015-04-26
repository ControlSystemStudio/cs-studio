package org.csstudio.opibuilder.util;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;

public class MacrosInputTest {

	MacrosInput macrosInput = new MacrosInput(
			new LinkedHashMap<String, String>(), true);
	String persistResult;
	
	@Before
	public void setup(){
		macrosInput.put("m1", "v1");
		macrosInput.put("m2", "");
		macrosInput.put("m3", "123");
		persistResult="\"true\",\"m1=v1\",\"m2=\",\"m3=123\"";
	}
	
	@Test
	public void testToPersistenceString() {
		String s=macrosInput.toPersistenceString();
		assertEquals(persistResult, s);
	}

	@Test
	public void testRecoverFromString() throws Exception {		
		assertEquals(macrosInput, MacrosInput.recoverFromString(persistResult));
	}

}
