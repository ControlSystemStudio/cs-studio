package org.csstudio.dal.epics;

import java.util.Arrays;

import org.csstudio.dal.DoubleSeqAccess;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.SequencePropertyCharacteristics;
import org.csstudio.dal.spi.DefaultPropertyFactoryService;
import org.csstudio.dal.spi.LinkPolicy;
import org.csstudio.dal.spi.PropertyFactory;

public class WaveformTest {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {

			final String[] names= new String[]{"T:2000_wf","T:2000s_wf","T:4000_wf","T:4000s_wf","T:8000_wf","T:8000s_wf"};
			//String[] names= new String[]{"T:2000_wf","T:2000s_wf","T:4000_wf","T:4000s_wf","T:8000_wf","T:8000s_wf","T:8000c_wf"};
			final EPICSApplicationContext ctx= new EPICSApplicationContext("Test");
			//AbstractApplicationContext ctx = new DefaultApplicationContext("Test");
//			/ctx.getConfiguration().put(Plugs.PLUG_PROPERTY_FACTORY_CLASS, "org.csstudio.dal.epics.PropertyFactoryImpl");
			final PropertyFactory fac = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx, LinkPolicy.SYNC_LINK_POLICY);

			for (final String name : names) {
				System.out.println("*** "+name+" ***");
				final DynamicValueProperty<?> p= fac.getProperty(name);

				System.out.println("class: "+p.getClass().getName());
				System.out.println("RTYP: "+Arrays.toString((String[])p.getCharacteristic("RTYP")));
				System.out.println("fieldType: "+p.getCharacteristic("fieldType"));
				System.out.println("size: "+p.getCharacteristic(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH));
				System.out.println("value: "+p.getValue());

				final DoubleSeqAccess dsa= p.getDataAccess(DoubleSeqAccess.class);
				final double[] dd= dsa.getValue();
				System.out.println("value size: "+dd.length);
				//System.out.println(Arrays.toString(dd));

				fac.getPropertyFamily().destroy(p);

			}

		} catch (final Exception e) {
			e.printStackTrace();
		}

		System.exit(0);

	}

}
