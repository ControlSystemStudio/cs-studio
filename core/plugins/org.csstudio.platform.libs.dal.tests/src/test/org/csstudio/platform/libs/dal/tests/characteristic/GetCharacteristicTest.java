package org.csstudio.platform.libs.dal.tests.characteristic;

import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.junit.Test;

/**
 * This test connects twice to a pv and reads its characteristics. 
 * The second pv is connected via a different SimpleDALBroker. It gets no metadata, which is shown in the test.
 *
 * Uses:
  record(calc, "ConstantPV") {
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
  
 * @author jpenning
 */
public class GetCharacteristicTest extends AbstractDALBoundaryTest {
    
    //    private static final double EXPECTED_VALUE = 40.0; // for Chiller:Pressure:1
    private static final double EXPECTED_VALUE = 100.0;
    
    private static final double EPSILON = 0.0001;
    private Holder<Double> _holder1 = new Holder<Double>();
    private Holder<Double> _holder2 = new Holder<Double>();
    
    
    
    
    @Test
    public void testDoubleProperty() throws Exception {
        RemoteInfo ri = createRemoteInfo();
        
        ConnectionParameters cp = new ConnectionParameters(ri, Double.class);
        ChannelListener listener1 = createChannelListener(_holder1);
        getBroker().registerListener(cp, listener1);
        
        Thread.sleep(1000);
        assertEquals(EXPECTED_VALUE, _holder1.getValue(), EPSILON);
        
        SimpleDALBroker broker2 = SimpleDALBroker
                .newInstance(new DefaultApplicationContext("Test"));
        ChannelListener listener2 = createChannelListener(_holder2);
        broker2.registerListener(cp, listener2);
        
        Thread.sleep(1000);
        assertEquals(EXPECTED_VALUE, _holder2.getValue(), EPSILON);
        
//        getBroker().deregisterListener(cp, listener1);
//        broker2.deregisterListener(cp, listener2);
//        Thread.sleep(1000);
        
        broker2.releaseAll();
    }
    
    private ChannelListener createChannelListener(final Holder<Double> holder) {
        return new ChannelListener() {
            
            public void channelStateUpdate(AnyDataChannel channel) {
//                System.out.println(">>>>> channelStateUpdate");
//                System.out.println(">>>>> property " + channel.getProperty());
//                System.out.println(">>>>> state " + channel.getProperty().getStateInfo());
//                System.out.println(">>>>> getDisplayHigh(): "
//                        + channel.getData().getMetaData().getDisplayHigh());
                if (channel.isRunning() && (channel.getData() != null) &&  (channel.getData().getMetaData() != null)) {
                    fetchMostImportantValueOfDisplayHigh(channel.getData().getMetaData()
                                                         .getDisplayHigh());
                }
            }
            
            public void channelDataUpdate(AnyDataChannel channel) {
//                System.out.println("##### channelDataUpdate");
//                System.out.println("##### property " + channel.getProperty());
//                System.out.println("##### state " + channel.getProperty().getStateInfo());
//                System.out.println("##### getDisplayHigh(): "
//                        + channel.getData().getMetaData().getDisplayHigh());
                if (channel.isRunning() && (channel.getData() != null) &&  (channel.getData().getMetaData() != null)) {
                    fetchMostImportantValueOfDisplayHigh(channel.getData().getMetaData()
                                                         .getDisplayHigh());
                }
            }
            
            private void fetchMostImportantValueOfDisplayHigh(Double newValue) {
                if ( (holder.getValue() == null) || (holder.getValue().equals(Double.NaN))) {
                    holder.setValue(newValue);
                }
            }
        };
    }
    
    private RemoteInfo createRemoteInfo() {
        // IMPORTANT: Set EXPECTED_VALUE when using Chiller:Pressure:1 to 40.0
        //        return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", "Chiller:Pressure:1", null, null);
        
        // IMPORTANT: Set EXPECTED_VALUE to 100.0
        return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", "ConstantPV", null, null);
        //        return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", "SawCalc0", null, null);
    }
    
}
