package org.csstudio.dal.epics.demo;

import org.csstudio.dal.StringProperty;
import org.csstudio.dal.epics.EPICSApplicationContext;
import org.csstudio.dal.spi.DefaultPropertyFactoryService;
import org.csstudio.dal.spi.LinkPolicy;
import org.csstudio.dal.spi.PropertyFactory;

public class WaveformDemo {

    public static void main(final String[] args) throws Exception {
        final char c = 0;

        System.out.println(">>>"+c);
        final EPICSApplicationContext ctx = new EPICSApplicationContext("Test");
        final PropertyFactory pf = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx, LinkPolicy.SYNC_LINK_POLICY);

        final StringProperty sp = pf.getProperty("wave", StringProperty.class, null);
        System.out.println("Waveform value: " + sp.getValue());
//        String val = "2345678900;
//        System.out.println("Set value: " + val);
//        sp.setValue(val);
//        Thread.sleep(2000);
//        System.out.println("New Value: " + sp.getValue());
    }
}
