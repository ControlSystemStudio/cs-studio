package org.csstudio.model;

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
}
