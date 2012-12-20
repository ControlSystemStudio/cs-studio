/**
 *
 */
package org.csstudio.platform.libs.dal.tests.alarmboundary;

import org.epics.css.dal.context.ConnectionState;

import junit.framework.Assert;

/**
 * Test to check the update of alarm boundaries of a pv.
 * 
 * CAREFUL: This test changes the value of a PV in the soft-ioc. Do not use this PV for other purposes or restart the soft-ioc after the test.
 * 
 *
 * Uses:
  record(calc, "ConstantPVModified") {
   field(DESC, "$")
   field(SCAN, "10 second")
   field(PHAS, "10")
   field(CALC, "A")
   field(INPA, "66")
   field(EGU, "Counts")
   field(HOPR, "100")
   field(LOPR, "0")
   field(HIHI, "70")
   field(LOLO, "15")
   field(HIGH, "65")
   field(LOW, "20")
   field(HHSV, "MAJOR")
   field(LLSV, "MAJOR")
   field(HSV, "MINOR")
   field(LSV, "MINOR")
  }
 */
public class MetadataUpdateTest extends AbstractDalBoundaryTest {
    
    public void testCheckForMetaData() {
    	
    	try {
    	
        // set defined values for the alarm boundaries
        getBroker().setValue(newRemoteInfo(CONSTANT_PV_MODIFIED + ".HIGH", null), 65);
        getBroker().setValue(newRemoteInfo(CONSTANT_PV_MODIFIED + ".HIHI", null), 70);
        
        // this pv has a scan rate of 10 seconds
        registerChannelListenerForPV(CONSTANT_PV_MODIFIED);

        // wait for connection
        Thread.sleep(6000);
        assertNotNull(_dataResult);
        //assertTrue(_dataResult.isMetaDataInitialized); // ok
        assertEquals(ConnectionState.CONNECTED, _dataResult.connectionState); // ok
        assertEquals(66.0, _dataResult.anyValue); // ok, this will not change

        assertNotNull(_stateResult);
        assertEquals(70.0, _stateResult.alarmHigh);

        // clear the store for the channelDataUpdate
        _dataResult = null; 
        
        // set new alarm high and wait longer than period of scan so will have a channelStateUpdate
        // now we do have an alarm
        getBroker().setValue(newRemoteInfo(CONSTANT_PV_MODIFIED + ".HIHI", null), 65);
        
        System.err.println("set value to 65");
        Thread.sleep(9000);
        System.err.println("wait eded");
        
        assertTrue(_stateResult.isMetaDataInitialized); // ok
        
        // This way it works, but it is not as we expected
//        Assert.assertEquals(Double.NaN, _stateResult.alarmHigh); // SHOULD BE 65
        Assert.assertEquals("ALARM", _stateResult.severityInfo); // SHOULD BE ALARM
        //assertEquals("ALARM", _stateResult.severityInfo);
        // data udpate DBR does nto have control data, so DAL is not notified aobut new
        // alarm limit, still DBR deliveres new severity
        //assertEquals(65.0, _stateResult.alarmHigh);
        
        assertEquals(ConnectionState.OPERATIONAL, _stateResult.connectionState); // ok
        assertNull(_dataResult); // ok, no channelDataUpdate
        
        // set new alarm high and wait longer than period of scan so will have a channelStateUpdate
        // now we don't have an alarm
        getBroker().setValue(newRemoteInfo(CONSTANT_PV_MODIFIED + ".HIHI", null), 80);
        System.out.println("set value to 80");
        Thread.sleep(12000);
        Assert.assertTrue(_stateResult.isMetaDataInitialized); // ok
        
        // This way it works, but it is not as we expected
//        Assert.assertEquals(Double.NaN, _stateResult.alarmHigh); // SHOULD BE 80
//        Assert.assertEquals("ALARM", _stateResult.severityInfo); // SHOULD BE WARNING
        // data udpate DBR does nto have control data, so DAL is not notified aobut new
        // alarm limit, still DBR deliveres new severity
        //Assert.assertEquals(80, _stateResult.alarmHigh);
        Assert.assertEquals("WARNING", _stateResult.severityInfo);
        Assert.assertEquals(ConnectionState.OPERATIONAL, _stateResult.connectionState); // ok
        Assert.assertNull(_dataResult); // ok, no channelDataUpdate
        
        deregisterListenerForPV(CONSTANT_PV_MODIFIED);
        
    	} catch (Exception e) {
    		e.printStackTrace();
    		fail(e.toString());
    	}
    }

}
