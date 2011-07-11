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
package org.csstudio.archive.common.engine.model;

import java.lang.reflect.Constructor;

import org.csstudio.domain.desy.time.StopWatch;
import org.csstudio.domain.desy.time.StopWatch.RunningStopWatch;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the cached constructor performance of the DBR types and their newInstance factory method.
 * 
 * @author bknerr
 * @since 05.05.2011
 */
public class CachedConstructorPerformanceUnitTest {
    
    private static final int ITERATIONS = 100000;

    
    /**
     * Essential parts copied out of {@link gov.aps.jca.dbr.DBRType.DBRType(String, int, Class)}
     * 
     * @author bknerr
     * @since 05.05.2011
     */
    public static class MyDBRType {
        Class<TestDBRType> _class;
        Constructor<TestDBRType> _ctor;

        MyDBRType(/* final String name, final int value, */final Class<TestDBRType> clazz) {
            _class=clazz;
            try {
                _ctor = _class.getConstructor( new Class[] {Integer.TYPE} );
            } catch( Exception ex ) {
                // Empty
            }
        }
        public TestDBRType newInstance( int count ) {
            try {
              return (TestDBRType)_ctor.newInstance( new Object[] {new Integer( count )} );
            } catch( Exception ex ) {
                // Empty
            }
            return null;
        }
        public TestDBRType newInstanceImproved( int count ) {
            try {
                // better use valueOf when x is expected to be very often one out of 0,-1, 1, 2 (that is 'common' cases) -   
                return (TestDBRType)_ctor.newInstance( new Object[] {Integer.valueOf( count )} ); 
            } catch( Exception ex ) {
                // Empty
            }
            return null;
        }
    }
    
    /**
     * Essential parts copied out of {@link gov.aps.jca.dbr.DBR_CTRL_Double}.
     * Left out supertypes.
     * 
     * @author bknerr
     * @since 05.05.2011
     */
    public static final class TestDBRType {
        int _count;
        /**
         * Constructor.
         */
        public TestDBRType(final int count) {
            _count = count;
        }
        public int getCount() {
            return _count;
        }
    }
    
    @Test
    public void testCachedConstructorPerformance() {
        
       
        long cachedConstructorPerf = runCachedConstructorIterations();

        long improvedCachedConstructorPerf = runCachedImprovedConstructorIterations();
        //System.out.println(cachedConstructorPerf*1.0/improvedCachedConstructorPerf + " times longer than cached constructor creation with Integer.valueOf.");
        Assert.assertTrue(cachedConstructorPerf > improvedCachedConstructorPerf);

        long normalPerf = runCompletelyNormalConstructorIterations();

        //System.out.println("And more than " + cachedConstructorPerf/normalPerf + " times longer than simple non cached creation with new .");
        Assert.assertTrue(cachedConstructorPerf > normalPerf);
        Assert.assertTrue(cachedConstructorPerf > 5*normalPerf);
    }
    
    

    private long runCachedImprovedConstructorIterations() {
        long cachedConstructorPerf = 0;
        
        Integer r = 0;
        try {
            MyDBRType type = new MyDBRType(TestDBRType.class); // the map lookup in _cached forValue

            RunningStopWatch watch = StopWatch.start();
            for (int i = 0; i < ITERATIONS; i++) {
                TestDBRType instance = type.newInstanceImproved(i);
                r += instance.getCount();  // avoid compiler optimization for non used/referred to objects
            }
            System.out.println("syso to avoid compiler opt:" + r); // avoid compiler optimization for non used/referred to objects
            cachedConstructorPerf = watch.getElapsedTimeInNS();
        } catch (final Exception e) {
            Assert.fail("Unexpected exception.");
        }
        return cachedConstructorPerf;
    }

    private long runCachedConstructorIterations() {
        long cachedConstructorPerf = 0;
        
        Integer r = 0;
        try {
            MyDBRType type = new MyDBRType(TestDBRType.class);

            RunningStopWatch watch = StopWatch.start();
            for (int i = 0; i < ITERATIONS; i++) {
                TestDBRType instance = type.newInstance(i);
                r += instance.getCount();  // avoid compiler optimization for non used/referred to objects
            }
            System.out.println(r); // avoid compiler optimization for non used/referred to objects
            cachedConstructorPerf = watch.getElapsedTimeInNS();
            
        } catch (final Exception e) {
            Assert.fail("Unexpected exception.");
        }
        return cachedConstructorPerf;
    }

    
    private long runCompletelyNormalConstructorIterations() {
        RunningStopWatch watch = StopWatch.start();
        Integer r = 0;
        for (int i = 0; i < 100000; i++) {                      
            final TestDBRType myDBR = new TestDBRType(i);
            r += myDBR.getCount(); // avoid compiler optimization for non used/referred to objects
        }
        System.out.println(r); // avoid compiler optimization for non used/referred to objects
        return watch.getElapsedTimeInMillis();
    }
}
