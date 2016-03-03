/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.dal.tine.demo;

import org.csstudio.dal.tine.TINEApplicationContext;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueAdapter;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.LongProperty;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.impl.DefaultApplicationContext;
import org.csstudio.dal.spi.DefaultPropertyFactoryService;
import org.csstudio.dal.spi.PropertyFactory;


public class SimpleDemo {

//    public static void main(String[] args) throws RemoteException, InstantiationException {
//        try {
//
//            // Choose TINe channel name. Name must be of form:
//            // <protocol, always TINE>/<context name>/<group name>/<device name>/<property name>
////            String name = "TINE/WORKSHOP/WKSineGen/Device 3/Amplitude";
//            String name = "TINE/DORIS/DORISDATA/V2 Rlf/DoArcTemp";
//
//            // Create application context
//            DefaultApplicationContext ctx= new TINEApplicationContext("SimpleDemo");
//
//            // print out configuration, which can be used on arbitrary application context
//            ctx.getConfiguration().store(System.out,"TINE application context configuration");
//
//
//            // creates factory, which will provide TINE channels
//            PropertyFactory propertyFactory = DefaultPropertyFactoryService.
//                getPropertyFactoryService().getPropertyFactory(ctx, null);
//
//            // We request double channel with specified name
//            final DoubleProperty property = propertyFactory.getProperty(name, DoubleProperty.class, null);
//
//            // We register listener, which will receive value updates
//
//            property.addDynamicValueListener(new DynamicValueAdapter() {
//
//                @Override
//                public void valueUpdated(DynamicValueEvent arg0) {
//                    System.out.println("UP: "+arg0.getValue());
//                }
//
//                @Override
//                public void valueChanged(DynamicValueEvent arg0) {
//                    System.out.println("CH: "+arg0.getValue());
//                }
//
//            });
//
//            final double max = property.getMaximum();
//
//            new Thread(new Runnable() {
//                public void run() {
//                    int i=0;
//                    while(i++<10) {
//                        try {
//                            double d= Math.random()*max;
//                            System.out.println("SET: "+d);
//                            property.setValue(d);
//                            System.out.println("GET: "+property.getValue());
//                        } catch (DataExchangeException e1) {
//                            e1.printStackTrace();
//                        }
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                    System.out.println("Done.");
//                    System.exit(0);
//
//                }
//            }).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



    public static void main(String[] args) throws RemoteException, InstantiationException {
        try {

            // Choose TINe channel name. Name must be of form:
            // <protocol, always TINE>/<context name>/<group name>/<device name>/<property name>
//            String name = "TINE/WORKSHOP/WKSineGen/Device 3/Amplitude";
//            String name = "DESY2/PEStrahlung/Platz-451/D3PlatzDesc";
            String name = "TTF2/QUENCHDETECT/C1.ACC1/ERROR";

            // Create application context
            DefaultApplicationContext ctx= new TINEApplicationContext("SimpleDemo");

            // print out configuration, which can be used on arbitrary application context
            ctx.getConfiguration().store(System.out,"TINE application context configuration");


            // creates factory, which will provide TINE channels
            PropertyFactory propertyFactory = DefaultPropertyFactoryService.
                getPropertyFactoryService().getPropertyFactory(ctx, null);

            // We request double channel with specified name
            final LongProperty property = propertyFactory.getProperty(name, LongProperty.class, null);

            // We register listener, which will receive value updates

            property.addDynamicValueListener(new DynamicValueAdapter() {

                @Override
                public void valueUpdated(DynamicValueEvent arg0) {
                    System.out.println("UP: "+arg0.getValue());
                }

                @Override
                public void valueChanged(DynamicValueEvent arg0) {
                    System.out.println("CH: "+arg0.getValue());
                }

            });

            new Thread(new Runnable() {
                public void run() {
                    int i=0;
                    while(i++<10) {
                        try {
//                            double d= Math.random()*max;
//                            System.out.println("SET: "+d);
//                            property.setValue(d);
                            System.out.println("GET: "+property.getValue());
                        } catch (DataExchangeException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    System.out.println("Done.");
                    System.exit(0);

                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
