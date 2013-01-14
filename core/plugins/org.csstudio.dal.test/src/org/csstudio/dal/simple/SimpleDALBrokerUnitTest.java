/**
 * 
 */
package org.csstudio.dal.simple;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for the simple dal broker
 * 
 * @author jpenning
 *
 */
public class SimpleDALBrokerUnitTest {
    
    @Test
    public void testCreateDefaultBroker() {
        SimpleDALBroker broker = SimpleDALBroker.getInstance();
        
        assertEquals("Simulator", broker.getDefaultPlugType());
        assertEquals(0, broker.getPropertiesMapSize());
    }
    
    @Test
    public void testSetDefaultPlugTypeOnDefaultBroker() {
        SimpleDALBroker broker = SimpleDALBroker.getInstance();
        
        broker.setDefaultPlugType("MyPlug");
        assertEquals("MyPlug", broker.getDefaultPlugType());
    }
    
    // cannot find class to implement access to real pv
    @Test(expected = InstantiationException.class)
    public void testGetValueOnDefaultBroker() throws Exception {
        SimpleDALBroker broker = SimpleDALBroker.getInstance();
        
        broker.getValue("MyPv");
    }
    
    // releaseAll is robust
    @Test
    public void testReleaseAllOnDefaultBroker() throws Exception {
        SimpleDALBroker broker = SimpleDALBroker.getInstance();
        
        broker.releaseAll();
        assertEquals(0, broker.getPropertiesMapSize());
    }
    
}
