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

package org.csstudio.dal.tine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.csstudio.dal.spi.DeviceFactoryService;
import org.csstudio.dal.spi.Plugs;
import org.csstudio.dal.spi.PropertyFactoryService;

public class PlugUtilities {
	
	public static void configureTINEPlug(Properties p)
	{
		String[] s = Plugs.getPlugNames(p);
		Set<String> set = new HashSet<String>(Arrays.asList(s));

		if (!set.contains(TINEPlug.PLUG_TYPE)) {
			set.add(TINEPlug.PLUG_TYPE);

			StringBuffer sb = new StringBuffer();

			for (Iterator iter = set.iterator(); iter.hasNext();) {
				if (sb.length() > 0) {
					sb.append(',');
				}

				sb.append(iter.next());
			}

			p.put(Plugs.PLUGS, sb.toString());
		}

		p.put(Plugs.PLUGS_DEFAULT, TINEPlug.PLUG_TYPE);
		p.setProperty(DeviceFactoryService.DEFAULT_APPLICATION_CONTEXT,TINEApplicationContext.class.getName());
		p.setProperty(PropertyFactoryService.DEFAULT_FACTORY_IMPL, PropertyFactoryImpl.class.getName());
		p.put(Plugs.PLUG_PROPERTY_FACTORY_CLASS + TINEPlug.PLUG_TYPE, PropertyFactoryImpl.class.getName());
		System.getProperties().setProperty(DeviceFactoryService.DEFAULT_APPLICATION_CONTEXT,TINEApplicationContext.class.getName());
	}
}
