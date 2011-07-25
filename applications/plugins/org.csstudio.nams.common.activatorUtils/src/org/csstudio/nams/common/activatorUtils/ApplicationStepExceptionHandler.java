
package org.csstudio.nams.common.activatorUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker-Annotation to mark a method that handles exceptions and error occured
 * and not handled in an application step of a RCP-application. The annotated
 * method have to be public.
 * 
 * The method may throw the handling (or wrapped) exception/error again, if it
 * can not be handled; this will cause the application to go done. To
 * successfully handle, it must return a value of {@link ApplicationStepResult}.
 * 
 * @author Matthias Zeimer
 * @version 0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
public @interface ApplicationStepExceptionHandler {
    // Nothing
}
