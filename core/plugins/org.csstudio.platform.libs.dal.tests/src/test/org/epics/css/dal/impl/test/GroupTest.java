/**
 * 
 */
package org.epics.css.dal.impl.test;

import junit.framework.TestCase;

import org.epics.css.dal.NumericProperty;
import org.epics.css.dal.group.PropertyCollectionMap;

/**
 * @author ikriznar
 *
 */
public class GroupTest extends TestCase {

	public void testPropertyCollectionMap() {
		try {
			
			Class<NumericProperty> npType= NumericProperty.class;
			PropertyCollectionMap<NumericProperty> pcm= new PropertyCollectionMap<NumericProperty>(npType);
			
			NumericProperty[] np= pcm.get("not there");
			
			assertNotNull(np);
			assertEquals(0, np.length);
			assertEquals(npType, np.getClass().getComponentType());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
