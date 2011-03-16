package org.csstudio.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;

/** Helper for converting control system items
 *
 *  @author Gabriele Carcassi
 *  @author Kay Kasemir
 */
public class ControlSystemObjectAdapter
{
    // TODO Can the Class<?>[] be more specific?
    /** Obtain serializable types for an object
     *  @param obj
     *  @return
     */
    public static Class<?>[] getSerializableTypes(Object obj)
    {
        // Check known types
    	if (obj instanceof DeviceName)
    		return new Class<?>[] { DeviceName.class };
    	else if (obj instanceof ProcessVariableName)
    		return new Class<?>[] { ProcessVariableName.class };
    	else if (Platform.isRunning())
    	{   // Check for adapters in platform
    	    final ControlSystemObject adapted =
    	        (ControlSystemObject) Platform.getAdapterManager().getAdapter(obj, ControlSystemObject.class);
    	    if (adapted != null)
    	        return adapted.getSerializableTypes();
    	}
    	// No serializable types found
    	return new Class<?>[] {};
    }

    /** Convert object to desired type
     *  @param obj Object
     *  @param targetClass Desired control system object class
     *  @return Object that matches the <code>targetClass</code> or <code>null</code>
     */
    public static Object convert(final Object obj, final Class<?> targetClass)
    {
        // Does object already match the desired class?
    	if (targetClass.isInstance(obj))
    		return obj;
    	else if (Platform.isRunning())
    	{   // Check for adapters in platform
    	    final Object adapted =
    	        Platform.getAdapterManager().getAdapter(obj, targetClass);
    	    return adapted; // may actually be null
    	}
    	// Cannot adapt obj to targetClass
    	return null;
    }
    
    /**
     * Returns all class names that an object of that class can be
     * converted to.
     * 
     * @param clazz a class
     * @return all the class names with registered adapterFactories
     */
    public static String[] getAdaptableTypes(Class<?> clazz)
    {
    	if (Platform.isRunning()){
    		// Check for adapters in platform
    	    return Platform.getAdapterManager().computeAdapterTypes(clazz);
    	}
    	
    	// No types found
    	return new String[0];
    }
    
    // Implementing some introspection functions based on the
    // class name instead of the class object. Using class
    // tokens would mean that this plugin has dependencies to
    // all plugins that have a single type.
    
    /**
     * Analogous to Class.isInstance(Object obj).
     */
    private static boolean isInstance(Object obj, String targetClass) {
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
    private static boolean isArray(String targetClass) {
    	return targetClass.charAt(0) == '[';
    }
    
    /**
     * Analogous to Class.getComponentType(). Return the type
     * of the elements of the array.
     * 
     * @param targetClass a class representing an array
     * @return the class of the array
     */
    private static String getComponentType(String targetClass) {
    	if (!isArray(targetClass))
    		return null;
    	return targetClass.substring(2, targetClass.length() - 1);
    }
    
    /** 
     * Adapts an object to the desired type. This method, on top of
     * the standard adapter facility, adds support for arrays. If any of the
     * arguments represents an array, it will
     * use object to object adaptation to create it.
     * 
     * @param obj Object to adapt
     * @param targetClass Desired class name
     * @return Object that matches the <code>targetClass</code> or <code>null</code>
     */
    public static Object convert(Object obj, String targetClass) {
    	// If object is of the right class, do not adapt
    	if (isInstance(obj, targetClass)) {
    		return obj;
    	}
    	
    	// If object is of the right class, but the target is an array,
    	// return a single element array
    	if (isArray(targetClass) && isInstance(obj, getComponentType(targetClass))) {
    		Object[] result = (Object[]) Array.newInstance(obj.getClass(), 1);
    		result[0] = obj;
    		return result;
    	}
    	
    	// If the object to adapt is an array,
    	// adapt each element
    	if (obj instanceof Object[]) {
    		Object[] elementsToAdapt = (Object[]) obj;
    		
    		// If target class is not an array, and
    		// there is only one element, adapt only that
    		// and return it
    		if (!isArray(targetClass)) {
    			if (elementsToAdapt.length == 0) {
    				return null;
    			} else if (elementsToAdapt.length == 1) {
    				return convert(elementsToAdapt[0], targetClass);
    			} else {
    				throw new IllegalArgumentException("Trying to adapt an array " + obj + " to a single object of type " + targetClass);
    			}
    		}
    		
    		// Target class is an array
    		List<Object> adaptedElements = new ArrayList<Object>();
    		String adaptedElementType = getComponentType(targetClass);
    		Object[] savedArrayToGetType = null;
    		for (Object element : elementsToAdapt) {
    			// Try to use the conversion to array first
    			Object[] newAdaptedElements = (Object[]) convert(element, targetClass);
    			if (newAdaptedElements != null) {
    				adaptedElements.addAll(Arrays.asList(newAdaptedElements));
    				savedArrayToGetType = newAdaptedElements;
    			} else {
        			// If no conversion to array is found, try converting the single element
    				Object newAdaptedElement = convert(element, adaptedElementType);
    				if (newAdaptedElement != null) {
    					adaptedElements.add(newAdaptedElement);
    				}
    			}
    		}
    		if (adaptedElements.size() == 0) {
    			// We got no result (and we got no class to create
    			// the array of the proper type). This can only mean that all the elements
    			// where adapted to empty arrays. See if you got a saved array,
    			// and use that.
    			if (savedArrayToGetType != null) {
    				return Array.newInstance(savedArrayToGetType.getClass().getComponentType(), 0);
    			} else {
    				return null;
    			}
    		} else {
	    		Object[] result = (Object[]) Array.newInstance(adaptedElements.get(0).getClass(), adaptedElements.size());
	    		return adaptedElements.toArray(result);
    		}
    	}
    	
    	// Time to try out the registered adapterFactories to the platform
    	if (Platform.isRunning()) {
    	    final Object adapted =
    	        Platform.getAdapterManager().loadAdapter(obj, targetClass);
    	    if (adapted != null)
    	    	return adapted;
    	}
    	
    	// No adapter was found. Check if target class is array, and try the single
    	// element conversion
    	if (isArray(targetClass)) {
    		Object newElement = convert(obj, getComponentType(targetClass));
    		if (newElement != null) {
        		Object[] result = (Object[]) Array.newInstance(newElement.getClass(), 1);
        		result[0] = newElement;
        		return result;
    		}
    	}

    	// Really cannot adapt, sorry!
    	return null;
    }

}
