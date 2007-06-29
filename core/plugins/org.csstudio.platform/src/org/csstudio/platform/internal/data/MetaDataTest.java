package org.csstudio.platform.internal.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ValueFactory;
import org.junit.Test;

@SuppressWarnings("nls")
public class MetaDataTest
{
    @Test
    public void compareEnum()
    {
        final String s1[] = new String[] { "one", "two", "three" };
        final String s2[] = new String[] { "one", "two", "three" };
        final String s3[] = new String[] { "Eins", "Zwei", "Drei" };

        final IEnumeratedMetaData m1 = ValueFactory.createEnumeratedMetaData(s1); 
        final IEnumeratedMetaData m2 = ValueFactory.createEnumeratedMetaData(s2); 
        final IEnumeratedMetaData m3 = ValueFactory.createEnumeratedMetaData(s3);
        
        assertEquals("two", m2.getState(1));
        
        assertEquals(m1, m2);
        assertEquals(m2, m1);
        assertNotSame(m1, m2);
        assertTrue(! m1.equals(m3));
    }

    @Test
    public void compareNumeric()
    {
        final INumericMetaData m1 = ValueFactory.createNumericMetaData(-10.0, 10.0, 1.0, 8.0, 0.0, 9.0, 2, "socks");
        final INumericMetaData m2 = ValueFactory.createNumericMetaData(-10.0, 10.0, 1.0, 8.0, 0.0, 9.0, 2, "socks");
        final INumericMetaData m3 = ValueFactory.createNumericMetaData(-10.0, 10.0, 1.0, 8.0, 0.0, 9.0, 20, "socks");
        
        assertEquals("socks", m2.getUnits());
        
        assertEquals(m1, m2);
        assertEquals(m2, m1);
        assertNotSame(m1, m2);
        assertTrue(! m1.equals(m3));
    }
}
