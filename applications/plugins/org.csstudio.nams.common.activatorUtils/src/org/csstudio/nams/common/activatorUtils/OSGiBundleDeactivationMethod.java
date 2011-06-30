
package org.csstudio.nams.common.activatorUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker-Annotation to mark the bundle-Activator OSGi-"stop"-method of the
 * bundle. The annotated method have to be public.
 * 
 * @author Matthias Zeimer
 * @version 0.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
public @interface OSGiBundleDeactivationMethod {
    // Nothing
}
