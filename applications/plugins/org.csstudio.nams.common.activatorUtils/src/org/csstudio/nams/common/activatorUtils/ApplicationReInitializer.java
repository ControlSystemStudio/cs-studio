
package org.csstudio.nams.common.activatorUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker-Annotation to mark application-re-initialiser of a RCP-application
 * which will be called if an application step requests an re-configuration on
 * returning {@link ApplicationStepResult#RECONFIGURE}. The annotated method
 * have to be public.
 * 
 * The method may additionally offers services to the OSGi-Registry.
 * 
 * @author Matthias Zeimer
 * @version 0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
public @interface ApplicationReInitializer {
    // Nothing
}
