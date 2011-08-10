
package org.csstudio.nams.common.activatorUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates given param to be required and injecting of null is not permitted.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.PARAMETER })
public @interface Required {
    // Nothing
}
