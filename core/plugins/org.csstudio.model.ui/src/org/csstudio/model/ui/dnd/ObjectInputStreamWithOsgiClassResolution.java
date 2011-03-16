package org.csstudio.model.ui.dnd;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.model.ui.Activator;
import org.osgi.framework.Bundle;

/**
 * De-serializes java classes looking for the classloaders in the appropriate osgi bundle.
 * 
 * @author Gabriele Carcassi
 */
public class ObjectInputStreamWithOsgiClassResolution extends ObjectInputStream {

    public ObjectInputStreamWithOsgiClassResolution(InputStream in)
       throws IOException {
        super(in);
    }
    
    private static Map<String, Class<?>> resolvedClasses = 
    	new ConcurrentHashMap<String, Class<?>>();

    private static Class<?> findClass(String className, String bundleName) {
    	try {
        	for (Bundle bundle : Activator.getDefault().getContext().getBundles()) {
        		if (bundle.getSymbolicName().equals(bundleName)) {
                	System.out.println("Bundle found " + bundle);
                	return bundle.loadClass(className);
        		}
        	}
    	} catch(ClassNotFoundException ex) {
    		// class not found
    	}
		return null;
    }
    
    private static Class<?> findClass(String className) {
    	// Let's first find a class normally
    	try {
    		return Class.forName(className);
    	} catch (ClassNotFoundException ex) {
    		// Not found continue
    	}
    	
    	// Look inside bundle that have the same name as one of the packages
    	// of the class
    	String currentBundleName = className;
    	while (currentBundleName.lastIndexOf(".") != -1) {
    		currentBundleName = currentBundleName.substring(0, currentBundleName.lastIndexOf('.'));
    		Class<?> clazz = findClass(className, currentBundleName);
    		if (clazz != null)
    			return clazz;
    	}
    	
    	// Can't find it
    	return null;
    }
    
    private static Class<?> getClass(String className) {
    	// Look if it's already resolved
    	Class<?> clazz = resolvedClasses.get(className);
    	if (clazz == null) {
    		
    		// Resolve it and cache it
    		clazz = findClass(className);
    		if (clazz != null) {
    			resolvedClasses.put(className, clazz);
    		}
    	}
    	return clazz;
    }
    
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass desc)
      throws IOException, ClassNotFoundException {
        String name = desc.getName();
        Class<?> clazz = getClass(name);
        if (clazz == null) {
        	throw new ClassNotFoundException(name);
        }
        return clazz;
    }
}
