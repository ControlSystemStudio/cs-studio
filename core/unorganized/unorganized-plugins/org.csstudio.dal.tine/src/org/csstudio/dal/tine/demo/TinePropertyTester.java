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
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.spi.DefaultPropertyFactoryService;
import org.csstudio.dal.spi.PropertyFactory;


public class TinePropertyTester {

	AbstractApplicationContext ctx;
	PropertyFactory f;


	public TinePropertyTester() {

		this.ctx= new TINEApplicationContext("TinePropertyTester");
		this.f = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(this.ctx, null);

	}

	public void shutdown() {
		this.ctx.destroy();
	}


	public void testProperty(final String name) throws RemoteException, InstantiationException {
		final DynamicValueProperty<?> p = this.f.getProperty(name);

		//System.out.print(p.getUniqueName());
		//System.out.print("; ");
		//System.out.print(p.getDataType().getSimpleName());
		//System.out.print("; ");
		//System.out.print(((TFormat)p.getCharacteristic("dataFormat")).toString());
		//System.out.print("; ");
		//System.out.print(((TArrayType)p.getCharacteristic("arrayType")).toString());
		//System.out.print("; \'");
		final Object o= p.getValue();
		if (o.getClass().isArray()) {
			if (o.getClass().getComponentType().isPrimitive()){
				if (o.getClass().getComponentType().equals(double.class)) {
					//System.out.print(Arrays.toString((double[])o));
				} else if (o.getClass().getComponentType().equals(long.class)) {
					//System.out.print(Arrays.toString((long[])o));
				}
			} else {
				//System.out.print(Arrays.toString((Object[])o));
			}
		} else {
			//System.out.print(o);
		}
		//System.out.print("'");
		//System.out.println();
		//System.out.flush();

		this.f.getPropertyFamily().destroy(p);
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		/*String[] names = {
				"TINE/DORIS/DORISDATA/V2 Rlf/DoArcTemp",
				"TINE/PETRA/PETRASTATE/undefined/DECLSTATE",
				"TINE/PETRA/PETRASTATE/undefined/DECLLSTATE",
				"TINE/PETRA/PETRASTATE/Ppramp/EVENTCOUNTER",
				"TINE/PETRA/PETRASTATE/Peamp/TIMECOUNTER",
				"TINE/PETRA/PETRASTATE/#32/DEVICES",
				"TINE/PETRA/PeBeam/#0/PeMagSoll.NAM",
				"TINE/DORIS/DORISDATA/V2 Rlf/DoArcTemp",
				"TINE/DESY2/CTStrahlung/Platz-281/CTPlatzDesc",
				"TINE/PETRA/ELWISCavitysvr/Cavity/Report",
				"TINE/HERA/APC/#0/ANNOTATE",
				"TINE/PETRA/PETRASTATE/ppstandby/ARCHSTATE",


		};*/

		final String[] names = {
				"TINE/TEST/SineQMExp/SineGen0/Amplitude"
		};

		final TinePropertyTester t= new TinePropertyTester();

		for (final String name : names) {
			try {
				t.testProperty(name);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		//System.out.println("DONE");
		t.shutdown();
		System.exit(0);

	}

}
