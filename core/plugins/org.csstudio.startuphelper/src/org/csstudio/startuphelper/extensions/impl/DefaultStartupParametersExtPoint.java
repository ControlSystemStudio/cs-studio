package org.csstudio.startuphelper.extensions.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.csstudio.startuphelper.extensions.StartupParametersExtPoint;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * <code>DefaultStartupParametersExtPoint</code> loads the system properties
 * and combines them together with program arguments. The program arguments
 * are dissected and each argument which has a '=' sign in its definition is 
 * treated as key-value pair. If there is no '=' sign or there are more than one
 * in the argument, the argument is still stored in the parameters map as the key
 * and its value is Boolean.TRUE. 
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DefaultStartupParametersExtPoint implements StartupParametersExtPoint {

	
	/*
	 * (non-Javadoc)
	 * @see org.csstudio.startup.extensions.StartupParametersExtPoint#readStartupParameters(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext)
	 */
	public Map<String, Object> readStartupParameters(Display display, IApplicationContext context) throws Exception {

		Map<String, Object> parameters = new HashMap<String, Object>();
		Properties properties = System.getProperties();
		Iterator<Object> it = properties.keySet().iterator();
		String key;
		while (it.hasNext()) {
			key = (String)it.next();
			parameters.put(key, properties.get(key));
		}
		final String args[] = (String []) context.getArguments().get("application.args"); //$NON-NLS-1$
        
		String[] arg;
		for (int i = 0; i < args.length; i++) {
			arg = args[i].split("=");
			if (arg.length == 2) {
				parameters.put(arg[0], arg[1]);
			} else {
				parameters.put(args[i], Boolean.TRUE);
			}
		}
		return parameters;
	}
	
}
