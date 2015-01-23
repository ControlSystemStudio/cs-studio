package org.csstudio.ui.util;

/**
 * Implementing some introspection functions based on the
 * class name instead of the class object. Using class
 * tokens would mean that this plugin has dependencies to
 * all plugins that have a single type.
 * 
 * @author Gabriele Carcassi
 */
public class ReflectUtil {
    
    /**
     * Analogous to Class.isInstance(Object obj).
     */
    public static boolean isInstance(Object obj, String targetClass) {
    	// TODO this does not work if targetClass is a superclass!
    	// need to crawl all implemented interfaces and superclasses... Sigh...
    	return obj.getClass().getName().equals(targetClass);
    }
    
    /**
     * Analogous to Class.isArray(). True if the class is an array
     * 
     * @param targetClass a class name
     * @return true if class name represents an array
     */
    public static boolean isArray(String targetClass) {
    	return targetClass.charAt(0) == '[';
    }
    
    /**
     * Analogous to Class.getComponentType(). Return the type
     * of the elements of the array.
     * 
     * @param targetClass a class representing an array
     * @return the class of the array
     */
    public static String getComponentType(String targetClass) {
    	if (!isArray(targetClass))
    		return null;
    	return targetClass.substring(2, targetClass.length() - 1);
    }
    
    /**
     * Returns the array class name for the given class name.
     * 
     * @param className a class name
     * @return the corresponding array class
     */
    public static String toArrayClass(String className) {
		return "[L" + className + ";";
    }

}
