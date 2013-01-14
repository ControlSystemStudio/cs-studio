/**
 * 
 */
package org.csstudio.utility.adlparser.fileParser;

import junit.framework.TestCase;

/**
 * @author hammonds
 *
 */
public class ADLResourceTest extends TestCase {

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.ADLResource#ADLResource(java.lang.String, java.lang.Object)}.
	 */
	public void testADLResource() {
		ADLResource res = new ADLResource(ADLResource.ADL_LIMITS, new Integer(5));
		assertTrue("Test resource Name", res.getName().equals(ADLResource.ADL_LIMITS) );;
		assertEquals( "Test resorce Value", res.getValue(), new Integer(5));
	}

}
