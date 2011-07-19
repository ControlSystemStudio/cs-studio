
package org.csstudio.nams.common.activatorUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker-Annotation to mark a method that handles the application-clean-up of a
 * RCP-application. The annotated method have to be public.
 * 
 * @author Matthias Zeimer
 * @version 0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
public @interface ApplicationShutdownMethod {
    // Nothing
}
