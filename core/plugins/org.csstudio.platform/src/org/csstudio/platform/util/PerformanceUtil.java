package org.csstudio.platform.util;

import java.util.HashMap;

import org.csstudio.platform.logging.CentralLogger;

public class PerformanceUtil {
	private boolean _verbose = true;

	private static PerformanceUtil _instance;

	private HashMap<Class, Integer> _createdInstances;
	
	private HashMap<Class, Integer> _finalizedInstances;

	private PerformanceUtil() {
		_createdInstances = new HashMap<Class, Integer>();
		_finalizedInstances = new HashMap<Class, Integer>();
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
		for(Class c : _createdInstances.keySet()) {
			int created = _createdInstances.get(c);
			int finalized = _finalizedInstances.containsKey(c) ? _finalizedInstances.get(c) : 0;
			int diff = created-finalized;
			
			CentralLogger.getInstance().info(
					null, diff+ " instances of type ["
							+ c + "] are alive (constructor calls: " + created
							+ "; finalize() calls: "+finalized+")");
		}
	}
}
