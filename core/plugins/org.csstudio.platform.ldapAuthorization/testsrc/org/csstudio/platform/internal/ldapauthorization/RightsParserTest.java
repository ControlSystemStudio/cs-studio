/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
