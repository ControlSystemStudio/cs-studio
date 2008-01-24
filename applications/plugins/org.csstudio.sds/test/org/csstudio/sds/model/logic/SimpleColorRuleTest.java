/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.sds.model.logic;

import static org.junit.Assert.assertEquals;

import org.eclipse.draw2d.ColorConstants;
import org.junit.Test;

/**
 * Test case for class {@link org.csstudio.sds.model.logic.SimpleColorRule}.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class SimpleColorRuleTest {
	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.logic.SimpleColorRule#evaluate(java.lang.Object[])}.
	 */
	@Test
	public void testEvaluate() {
		SimpleColorRule rule = new SimpleColorRule();

		// > 66: red, > 33: yellow, else green
		assertEquals(ColorConstants.green.getRGB(), rule
				.evaluate(new Object[] { 33.0 }));
		assertEquals(ColorConstants.yellow.getRGB(), rule
				.evaluate(new Object[] { 34.0 }));
		assertEquals(ColorConstants.red.getRGB(), rule
				.evaluate(new Object[] { 67.0 }));

		// fallback: not a double or no arguments = green
		assertEquals(ColorConstants.red.getRGB(), rule
				.evaluate(new Object[] { 99 }));
		assertEquals(ColorConstants.black.getRGB(), rule.evaluate(null));
	}

}
