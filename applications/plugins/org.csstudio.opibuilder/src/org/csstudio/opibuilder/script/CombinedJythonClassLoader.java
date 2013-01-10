package org.csstudio.opibuilder.script;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.python.core.Py;
import org.python.core.imp;

/**
 * A class loader try to use plugin class loader first, then try to use Jython
 * class loader.
 * 
 * @author Xihui Chen
 * 
 */
public class CombinedJythonClassLoader extends ClassLoader {

	private ClassLoader defaultClassLoader = OPIBuilderPlugin.getDefault()
			.getClass().getClassLoader();


	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		// A hack to temporarily set class loader to null, which is the original status 
		//of SystemState.
		ClassLoader saveClassLoader = Py.getSystemState().getClassLoader();
		Py.getSystemState().setClassLoader(null);
		try {			
			return defaultClassLoader.loadClass(name);			
		} catch (Exception e) {
			return imp.getSyspathJavaLoader().loadClass(name);			
		}finally {
			Py.getSystemState().setClassLoader(saveClassLoader);
		}
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		return defaultClassLoader.getResources(name);
	}
	
}