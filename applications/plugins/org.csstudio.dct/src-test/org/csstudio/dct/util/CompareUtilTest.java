/**
 *
 */
package org.csstudio.dct.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.model.internal.AbstractElement;
import org.csstudio.dct.model.internal.Record;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sven Wende
 *
 */
public class CompareUtilTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.util.CompareUtil#equals(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testEquals() {
        assertTrue(CompareUtil.equals(null, null));
        assertTrue(CompareUtil.equals("", ""));
        assertTrue(CompareUtil.equals("a", "a"));
        assertFalse(CompareUtil.equals(null, ""));
        assertFalse(CompareUtil.equals("", null));
        assertFalse(CompareUtil.equals("a", null));
        assertFalse(CompareUtil.equals("a", "b"));
        assertFalse(CompareUtil.equals("b", "a"));
        assertFalse(CompareUtil.equals("", "a"));

    }

    /**
     * Test method for
     * {@link org.csstudio.dct.util.CompareUtil#equals(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testIdsEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        IElement element1 = new AbstractElement("e1", id1) {
            public void accept(IVisitor visitor) {
            }

            public boolean isInherited() {
                return false;
            }
        };

        IElement element2 = new AbstractElement("e2", id2) {
            public void accept(IVisitor visitor) {
            }

            public boolean isInherited() {
                return false;
            }
        };

        assertTrue(CompareUtil.idsEqual(null, null));
        assertTrue(CompareUtil.idsEqual(element1, element1));
        assertTrue(CompareUtil.idsEqual(element2, element2));
        assertFalse(CompareUtil.idsEqual(null,element1));
        assertFalse(CompareUtil.idsEqual(element1, null));
        assertFalse(CompareUtil.idsEqual(element1, element2));
    }

    public final void testContainsOnly() {
        List list = new ArrayList();
        list.add("a");
        list.add("b");

        assertTrue(CompareUtil.containsOnly(String.class, list));
        assertTrue(CompareUtil.containsOnly(Object.class, list));
        assertFalse(CompareUtil.containsOnly(IRecord.class, list));

        list.add(new Record());

        assertFalse(CompareUtil.containsOnly(String.class, list));
        assertTrue(CompareUtil.containsOnly(Object.class, list));
        assertFalse(CompareUtil.containsOnly(IRecord.class, list));

        list.remove(0);
        list.remove(0);

        assertFalse(CompareUtil.containsOnly(String.class, list));
        assertTrue(CompareUtil.containsOnly(Object.class, list));
        assertTrue(CompareUtil.containsOnly(IRecord.class, list));
    }
}
