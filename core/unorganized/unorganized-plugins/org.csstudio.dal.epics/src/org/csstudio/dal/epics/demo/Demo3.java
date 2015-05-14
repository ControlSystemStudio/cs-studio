package org.csstudio.dal.epics.demo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.DynamicValueAdapter;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.EnumProperty;
import org.csstudio.dal.LongProperty;
import org.csstudio.dal.StringProperty;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.ConnectionEvent;
import org.csstudio.dal.context.LinkAdapter;
import org.csstudio.dal.epics.EPICSApplicationContext;
import org.csstudio.dal.spi.DefaultPropertyFactoryService;
import org.csstudio.dal.spi.LinkPolicy;
import org.csstudio.dal.spi.PropertyFactory;

public class Demo3 {

    public static void main(final String[] args) throws Exception {

        final String propertyName = "manyChannel_002";
        final AbstractApplicationContext ctx = new EPICSApplicationContext("DEMO");
        final PropertyFactory pf = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx, LinkPolicy.ASYNC_LINK_POLICY,"EPICS");

        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                final EnumProperty pr1 = pf.getProperty(propertyName, EnumProperty.class, null);
                pr1.addDynamicValueListener(new DynamicValueAdapter<Long, EnumProperty>(){
                    @Override
                    public void valueChanged(final DynamicValueEvent<Long, EnumProperty> event) {
                        System.out.println("enum "+event.getValue());
                    }
                });

                pr1.addLinkListener(new LinkAdapter<EnumProperty>(){
                    @Override
                    public void connected(final ConnectionEvent<EnumProperty> e) {
                        System.out.println(e.getState());
                    }
                });
                pr1.addPropertyChangeListener(new PropertyChangeListener(){
                    @Override
                    public void propertyChange(final PropertyChangeEvent arg0) {
                        System.out.println(arg0.getPropertyName());
                    }
                });
                System.out.println("Enum property done.");
                } catch (final Exception e){}
            }
        }).start();


        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                final StringProperty pr2 = pf.getProperty(propertyName, StringProperty.class, null);

                pr2.addDynamicValueListener(new DynamicValueAdapter<String, StringProperty>(){
                    @Override
                    public void valueChanged(final DynamicValueEvent<String, StringProperty> event) {
                        System.out.println("string " + event.getValue());
                    }
                });

                pr2.addLinkListener(new LinkAdapter<StringProperty>(){
                    @Override
                    public void connected(final ConnectionEvent<StringProperty> e) {
                        System.out.println(e.getState());
                    }
                });
                pr2.addPropertyChangeListener(new PropertyChangeListener(){
                    @Override
                    public void propertyChange(final PropertyChangeEvent arg0) {
                        System.out.println(arg0.getPropertyName());
                    }
                });

                System.out.println("String property done.");
                } catch (final Exception e){}

        }}).start();

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                final DoubleProperty pr3 = pf.getProperty(propertyName, DoubleProperty.class, null);

                pr3.addDynamicValueListener(new DynamicValueAdapter<Double, DoubleProperty>(){
                    @Override
                    public void valueChanged(final DynamicValueEvent<Double, DoubleProperty> event) {
                        System.out.println("double "+ event.getValue());
                    }
                });

                pr3.addLinkListener(new LinkAdapter<DoubleProperty>(){
                    @Override
                    public void connected(final ConnectionEvent<DoubleProperty> e) {
                        System.out.println(e.getState());
                    }
                });
                pr3.addPropertyChangeListener(new PropertyChangeListener(){
                    @Override
                    public void propertyChange(final PropertyChangeEvent arg0) {
                        System.out.println(arg0.getPropertyName());
                    }
                });

                System.out.println("Double property done.");
                } catch (final Exception e){}
            }
        }).start();


        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                final LongProperty pr4 = pf.getProperty(propertyName, LongProperty.class, null);

                pr4.addDynamicValueListener(new DynamicValueAdapter<Long, LongProperty>(){
                    @Override
                    public void valueChanged(final DynamicValueEvent<Long, LongProperty> event) {
                        System.out.println("long " + event.getValue());
                    }
                });

                pr4.addLinkListener(new LinkAdapter<LongProperty>(){
                    @Override
                    public void connected(final ConnectionEvent<LongProperty> e) {
                        System.out.println(e.getState());
                    }
                });
                pr4.addPropertyChangeListener(new PropertyChangeListener(){
                    @Override
                    public void propertyChange(final PropertyChangeEvent arg0) {
                        System.out.println(arg0.getPropertyName());
                    }
                });

                System.out.println("Long property done.");
                } catch (final Exception e){}

            }
        }).start();


//        for (int i = 0; i < 6; i++) {
//            pr1.getCharacteristicAsynchronously("SEVR", new ResponseListener(){
//                public void responseError(ResponseEvent event) {
//                    System.out.println(event.getResponse().getError());
//                }
//                public void responseReceived(ResponseEvent event) {
//                    System.out.println(event.getResponse().getValue());
//
//                }
//            });
//        }
    }
}
