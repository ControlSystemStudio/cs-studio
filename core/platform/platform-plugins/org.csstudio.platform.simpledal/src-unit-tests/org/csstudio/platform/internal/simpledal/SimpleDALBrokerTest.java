package org.csstudio.platform.internal.simpledal;

import junit.framework.TestCase;

import org.csstudio.dal.DalPlugin;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseEvent;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.RemoteInfo;

public class SimpleDALBrokerTest extends TestCase {

    private DalPlugin dalPlugin;

    @Override
    protected void setUp() throws Exception {
        dalPlugin = new DalPlugin();
    }

    public void testBasic() {
        RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"EPICS", "PV_AI_01", null, null);
        ConnectionParameters cp = new ConnectionParameters(ri, Double.class);

        double result = Double.NaN;
        try {
            result = (Double) dalPlugin.getSimpleDALBroker().getValue(ri);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        System.out.println("Result is = "+result);

        assertTrue(true);
    }

    public void testDoubleChannel() {
        RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"EPICS", "PV_AI_01", null, null);
        ConnectionParameters cp = new ConnectionParameters(ri, Double.class);
        double addValue = 5.0;

        double initialValue = Double.NaN;
        try {
            initialValue = (Double) dalPlugin.getSimpleDALBroker().getValue(cp);
        } catch (Exception e) {
            fail();
        }
        assertTrue(initialValue != Double.NaN);

        try {
            dalPlugin.getSimpleDALBroker().setValue(ri, new Double(initialValue+addValue));
        } catch (Exception e) {
            fail();
        }

        double newValue = Double.NaN;
        try {
            newValue = (Double) dalPlugin.getSimpleDALBroker().getValue(ri);
        } catch (Exception e) {
            fail();
        }

        assertEquals(initialValue+addValue, newValue);

        newValue = Double.NaN;
        try {
            newValue = (Double) dalPlugin.getSimpleDALBroker().getValue(ri, Double.class);
        } catch (Exception e) {
            fail();
        }

        assertEquals(initialValue+addValue, newValue);

        Request<Double> request = null;
        try {
            request = dalPlugin.getSimpleDALBroker().setValueAsync(cp, new Double(initialValue+2*addValue), new ResponseListener<Double>() {

                public void responseError(ResponseEvent<Double> event) {
                    System.out.println(">>> testDoubleProperty/setValueAsync/responseError");
                }

                public void responseReceived(ResponseEvent<Double> event) {
                    System.out.println(">>> testDoubleProperty/setValueAsync/responseReceived");
                }

            });
        } catch (Exception e) {
            fail();
        }

        while(!request.isCompleted()) {
            System.out.println(">>> WAITING...");
            Thread.yield();
        }

        request = null;
        newValue = Double.NaN;
        try {
            request = dalPlugin.getSimpleDALBroker().getValueAsync(cp, new ResponseListener<Double>() {

                public void responseError(ResponseEvent<Double> event) {
                    System.out.println(">>> testDoubleProperty/getValueAsync/responseError");
                }

                public void responseReceived(ResponseEvent<Double> event) {
                    System.out.println(">>> testDoubleProperty/getValueAsync/responseReceived");
                }

            });
        } catch (Exception e) {
            fail();
        }

        while(!request.isCompleted()) {
            System.out.println(">>> WAITING...");
            Thread.yield();
        }

        newValue = request.getLastResponse().getValue();

        assertEquals(initialValue+2*addValue, newValue);
    }

    @SuppressWarnings("unchecked")
    public void testChannelListener() {
        RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
        ConnectionParameters cp = new ConnectionParameters(ri, Double.class);
        double addValue = 5.0;

        try {
            dalPlugin.getSimpleDALBroker().registerListener(cp, new ChannelListener() {

                public void channelDataUpdate(AnyDataChannel channel) {
                    System.out.println(">>> testChannelListener/ChannelListener/channelDataUpdate");
                }

                public void channelStateUpdate(AnyDataChannel channel) {
                    System.out.println(">>> testChannelListener/ChannelListener/channelStateUpdate");
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            dalPlugin.getSimpleDALBroker().registerListener(cp, new DynamicValueListener() {

                public void conditionChange(DynamicValueEvent event) {
                    System.out.println(">>> testChannelListener/DynamicValueListener/conditionChange");
                }

                public void errorResponse(DynamicValueEvent event) {
                    System.out.println(">>> testChannelListener/DynamicValueListener/errorResponse");

                }

                public void timelagStarts(DynamicValueEvent event) {
                    System.out.println(">>> testChannelListener/DynamicValueListener/timelagStarts");
                }

                public void timelagStops(DynamicValueEvent event) {
                    System.out.println(">>> testChannelListener/DynamicValueListener/timelagStops");
                }

                public void timeoutStarts(DynamicValueEvent event) {
                    System.out.println(">>> testChannelListener/DynamicValueListener/timeoutStarts");
                }

                public void timeoutStops(DynamicValueEvent event) {
                    System.out.println(">>> testChannelListener/DynamicValueListener/timeoutStops");
                }

                public void valueChanged(DynamicValueEvent event) {
                    System.out.println(">>> testChannelListener/DynamicValueListener/valueChanged");
                }

                public void valueUpdated(DynamicValueEvent event) {
                    System.out.println(">>> testChannelListener/DynamicValueListener/valueUpdated");
                }

            });
        } catch (Exception e1) {
            fail();
        }

        double initialValue = Double.NaN;
        try {
            initialValue = (Double) dalPlugin.getSimpleDALBroker().getValue(cp);
        } catch (Exception e) {
            fail();
        }
        assertTrue(initialValue != Double.NaN);

        System.out.println(">>>>>> SYNC SET");
        try {
            dalPlugin.getSimpleDALBroker().setValue(ri, new Double(initialValue+addValue));
        } catch (Exception e) {
            fail();
        }

        double newValue = Double.NaN;
        try {
            newValue = (Double) dalPlugin.getSimpleDALBroker().getValue(ri);
        } catch (Exception e) {
            fail();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        assertEquals(initialValue+addValue, newValue);

        System.out.println(">>>>>> ASYNC SET");
        Request<Double> request = null;
        try {
            request = dalPlugin.getSimpleDALBroker().setValueAsync(cp, new Double(initialValue+2*addValue), new ResponseListener<Double>() {

                public void responseError(ResponseEvent<Double> event) {
                    System.out.println(">>> testChannelListener/setValueAsync/responseError");
                }

                public void responseReceived(ResponseEvent<Double> event) {
                    System.out.println(">>> testChannelListener/setValueAsync/responseReceived");
                }

            });
        } catch (Exception e) {
            fail();
        }

        while(!request.isCompleted()) {
            System.out.println(">>> WAITING...");
            Thread.yield();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println(">>>>>> ASYNC GET");
        request = null;
        newValue = Double.NaN;
        try {
            request = dalPlugin.getSimpleDALBroker().getValueAsync(cp, new ResponseListener<Double>() {

                public void responseError(ResponseEvent<Double> event) {
                    System.out.println(">>> testChannelListener/getValueAsync/responseError");
                }

                public void responseReceived(ResponseEvent<Double> event) {
                    System.out.println(">>> testChannelListener/getValueAsync/responseReceived");
                }

            });
        } catch (Exception e) {
            fail();
        }

        while(!request.isCompleted()) {
            System.out.println(">>> WAITING...");
            Thread.yield();
        }

        newValue = request.getLastResponse().getValue();

        assertEquals(initialValue+2*addValue, newValue);
    }

}
