
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.archive.sdds.server.type;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test for {@link TypeFactory}.
 *
 * @author mmoeller
 * @since 17.11.2011
 */
public class TypeFactoryUnitTest {

    @Test
    public void testToDouble() throws TypeNotSupportedException {

        Object o = new Integer(1234);
        Double d = TypeFactory.toDouble(o);
        Assert.assertEquals(0, d.compareTo(new Double(1234.0)));

        o = new Long(1234);
        d = TypeFactory.toDouble(o);
        Assert.assertEquals(0, d.compareTo(new Double(1234.0)));

        o = new Float(1234.0f);
        d = TypeFactory.toDouble(o);
        Assert.assertEquals(0, d.compareTo(new Double(1234.0)));

        o = new Double(1234);
        d = TypeFactory.toDouble(o);
        Assert.assertEquals(0, d.compareTo(new Double(1234.0)));
    }

    @Test
    public void testToFloat() throws TypeNotSupportedException {

        Object o = new Integer(1234);
        Float f = TypeFactory.toFloat(o);
        Assert.assertEquals(0, f.compareTo(new Float(1234.0f)));

        o = new Long(1234);
        f = TypeFactory.toFloat(o);
        Assert.assertEquals(0, f.compareTo(new Float(1234.0f)));

        o = new Float(1234.0f);
        f = TypeFactory.toFloat(o);
        Assert.assertEquals(0, f.compareTo(new Float(1234.0f)));

        o = new Double(1234);
        f = TypeFactory.toFloat(o);
        Assert.assertEquals(0, f.compareTo(new Float(1234.0f)));
    }

    @Test
    public void testToInteger() throws TypeNotSupportedException {

        Object o = new Integer(1234);
        Integer i = TypeFactory.toInteger(o);
        Assert.assertEquals(0, i.compareTo(new Integer(1234)));

        o = new Long(1234);
        i = TypeFactory.toInteger(o);
        Assert.assertEquals(0, i.compareTo(new Integer(1234)));

        o = new Float(1234.23f);
        i = TypeFactory.toInteger(o);
        Assert.assertEquals(0, i.compareTo(new Integer(1234)));

        o = new Double(1234.23);
        i = TypeFactory.toInteger(o);
        Assert.assertEquals(0, i.compareTo(new Integer(1234)));
    }

    @Test
    public void testToLong() throws TypeNotSupportedException {

        Object o = new Integer(1234);
        Long l = TypeFactory.toLong(o);
        Assert.assertEquals(0, l.compareTo(new Long(1234)));

        o = new Long(1234);
        l = TypeFactory.toLong(o);
        Assert.assertEquals(0, l.compareTo(new Long(1234)));

        o = new Float(1234.234f);
        l = TypeFactory.toLong(o);
        Assert.assertEquals(0, l.compareTo(new Long(1234)));

        o = new Double(1234.56453);
        l = TypeFactory.toLong(o);
        Assert.assertEquals(0, l.compareTo(new Long(1234)));
    }

    @Test
    public void testToString() throws TypeNotSupportedException {

        Object o = new Integer(1234);
        String s = TypeFactory.toString(o);
        Assert.assertEquals(0, s.compareTo(new String("1234")));

        o = new Long(1234);
        s = TypeFactory.toString(o);
        Assert.assertEquals(0, s.compareTo(new String("1234")));

        o = new Float(1234.435f);
        s = TypeFactory.toString(o);
        Assert.assertEquals(0, s.compareTo(new String("1234.435")));

        o = new Double(1234.345);
        s = TypeFactory.toString(o);
        Assert.assertEquals(0, s.compareTo(new String("1234.345")));
    }

    @Test(expected=TypeNotSupportedException.class)
    public void testTypeNotSupportedException() throws TypeNotSupportedException {

        final Object o = new Short((short) 12);
        TypeFactory.toLong(o);
    }
}
