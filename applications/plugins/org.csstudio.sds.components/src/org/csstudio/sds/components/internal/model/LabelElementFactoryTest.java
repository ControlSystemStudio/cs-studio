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
package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.*;

import org.csstudio.sds.model.DisplayModelElement;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link LabelElementFactory}.
 * 
 * @author Sven Wende
 *
 */
public final class LabelElementFactoryTest {

	/**
	 * A element instanc for testing issues.
	 */
	private LabelElementFactory _elementFactory;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp()  {
		_elementFactory= new LabelElementFactory();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.LabelElementFactory#createModelElement()}.
	 */
	@Test
	public void testCreateModelElement() {
		DisplayModelElement element = _elementFactory.createModelElement();
		assertNotNull(element);
		assertTrue(element instanceof LabelElement);
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.LabelElementFactory#getModelElementType()}.
	 */
	@Test
	public void testGetModelElementType() {
		assertEquals(LabelElement.class, _elementFactory.getModelElementType());
	}

}
