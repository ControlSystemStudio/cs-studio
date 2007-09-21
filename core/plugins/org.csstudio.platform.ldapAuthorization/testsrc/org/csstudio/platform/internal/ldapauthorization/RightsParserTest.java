package org.csstudio.platform.internal.ldapauthorization;

import static org.junit.Assert.*;

import org.csstudio.platform.security.Right;
import org.csstudio.platform.security.RightSet;
import org.junit.Test;

public class RightsParserTest {

	@Test
	public void testParseRight() throws Exception {
		String r1 = "(role1,group1)";
		String r2 = "(   role2   ,  \t group2  )";
		String r3 = "  ( role.3, group___3   ) ";
		
		assertEquals(new Right("role1", "group1"), RightsParser.parseRight(r1));
		assertEquals(new Right("role2", "group2"), RightsParser.parseRight(r2));
		assertEquals(new Right("role.3", "group___3"), RightsParser.parseRight(r3));
	}
	
	@Test
	public void testParseRightSet() throws Exception {
		String s1 = "(role1, group1) (role2, group2)";
		String s2 = "   (  role1  ,  group1)\t(role2,group2)   ";
		String s3 = "(role1,group1)(role2,group2)";
		
		RightSet reference = new RightSet("test");
		reference.addRight(new Right("role1", "group1"));
		reference.addRight(new Right("role2", "group2"));
		
		assertEquals(reference.getRights(), RightsParser.parseRightSet(s1, "").getRights());
		assertEquals(reference.getRights(), RightsParser.parseRightSet(s2, "").getRights());
		assertEquals(reference.getRights(), RightsParser.parseRightSet(s3, "").getRights());
	}
}
