/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.platform.util;

import java.util.HashMap;

import org.csstudio.platform.logging.CentralLogger;

public class PerformanceUtil {
	private boolean _verbose = false;

	private static PerformanceUtil _instance;

	private HashMap<Class, Integer> _createdInstances;

	private HashMap<Class, Integer> _finalizedInstances;

	private PerformanceUtil() {
		_createdInstances = new HashMap<Class, Integer>();
		_finalizedInstances = new HashMap<Class, Integer>();
	}

	public static int countAll() {
		int count = 0;
		for (Class c : getInstance()._createdInstances.keySet()) {
			int created = getInstance()._createdInstances.get(c);
			int finalized = getInstance()._finalizedInstances.containsKey(c) ? getInstance()._finalizedInstances
					.get(c)
					: 0;
			int diff = created - finalized;

			count += diff;
		}

		return count;
	}

	public static String print() {
		StringBuffer sb = new StringBuffer();

		for (Class c : getInstance()._createdInstances.keySet()) {
			sb.append(c.getName());
			sb.append(": ");
			sb.append(getInstance()._createdInstances.get(c));
			sb.append("\r\n");
		}

		return sb.toString();
	}

	public static synchronized PerformanceUtil getInstance() {
		if (_instance == null) {
			_instance = new PerformanceUtil();
		}
		return _instance;
	}

	public void constructorCalled(Object o) {
		Class clazz = o.getClass();
		int count = 0;

		synchronized (_createdInstances) {
			if (_createdInstances.containsKey(clazz)) {
				count = _createdInstances.get(clazz);
			}

			count++;

			_createdInstances.put(clazz, count);

			if (_verbose) {
				CentralLogger.getInstance().debug(
						null,
						"CONSTRUCTED instance [" + o.hashCode() + "] of type ["
								+ clazz + "]!");
			}
		}
	}

	public void finalizedCalled(Object o) {
		Class clazz = o.getClass();
		int count = 0;

		synchronized (_finalizedInstances) {

			if (_finalizedInstances.containsKey(clazz)) {
				count = _finalizedInstances.get(clazz);
			}

			count++;
			_finalizedInstances.put(clazz, count);

			if (_verbose) {
				CentralLogger.getInstance().debug(
						null,
						"FINALIZED instance [" + o.hashCode() + "] of type ["
								+ clazz + "]!");
			}
		}
	}

	public void printStates() {
		for (Class c : _createdInstances.keySet()) {
			int created = _createdInstances.get(c);
			int finalized = _finalizedInstances.containsKey(c) ? _finalizedInstances
					.get(c)
					: 0;
			int diff = created - finalized;

			CentralLogger.getInstance().info(
					null,
					diff + " instances of type [" + c
							+ "] are alive (constructor calls: " + created
							+ "; finalize() calls: " + finalized + ")");
		}
	}
}
