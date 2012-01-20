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
package org.csstudio.sds.cosyrules.color;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 20.12.2011
 */
public class IfGreaterThenUnitTest {

    private IfGraterThen _ifGraterThen;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _ifGraterThen = new IfGraterThen();
    }

    @Test
    public void test() {
        Object[] arguments = new Object[] {1,2};
        // int, int
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));
        arguments = new Object[] {-1,-2};
        assertTrue((Boolean)_ifGraterThen.evaluate(arguments));

        // long, long
        arguments = new Object[] {100000000l,20000000000l};
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));
        arguments = new Object[] {2000000000l,-100000000l};
        assertTrue((Boolean)_ifGraterThen.evaluate(arguments));

        // double, double
        arguments = new Object[] {100000000.151245,20000000000.1634545d};
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));
        arguments = new Object[] {-0.0002345,-0.0003455466d};
        assertTrue((Boolean)_ifGraterThen.evaluate(arguments));

        // String, String
        arguments = new Object[] {"1","2"};
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));
        arguments = new Object[] {"-1","-2"};
        assertTrue((Boolean)_ifGraterThen.evaluate(arguments));

        arguments = new Object[] {"1.2","2.2"};
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));
        arguments = new Object[] {"-1.2","-2.3"};
        assertTrue((Boolean)_ifGraterThen.evaluate(arguments));

        arguments = new Object[] {"1,1","2,4"};
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));
        arguments = new Object[] {"-1,3","-2,9"};
        assertTrue((Boolean)_ifGraterThen.evaluate(arguments));

        arguments = new Object[] {"1,1","2wrong4"};
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));
        arguments = new Object[] {"2wrong4","1,1"};
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));

        // String, int
        arguments = new Object[] {"1,2",2};
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));
        arguments = new Object[] {"2,4",1};
        assertTrue((Boolean)_ifGraterThen.evaluate(arguments));

        // int, long
        arguments = new Object[] {1,2l};
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));
        arguments = new Object[] {2,1l};
        assertTrue((Boolean)_ifGraterThen.evaluate(arguments));

        // long double
        arguments = new Object[] {1l,2.1d};
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));
        arguments = new Object[] {2l,1.1d};
        assertTrue((Boolean)_ifGraterThen.evaluate(arguments));

        // double, String
        arguments = new Object[] {2.1d,"4"};
        assertFalse((Boolean)_ifGraterThen.evaluate(arguments));
        arguments = new Object[] {1.1d,"0,23"};
        assertTrue((Boolean)_ifGraterThen.evaluate(arguments));
    }

}
