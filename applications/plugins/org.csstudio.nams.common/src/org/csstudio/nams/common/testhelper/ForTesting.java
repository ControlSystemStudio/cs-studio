package org.csstudio.nams.common.testhelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks methods and classes as for testing only
 * 
 * Could be used by an AnnotationProcessor to ensure the intended use.
 * 
 * @author Gösta Steen, Tobias Rathjen
 */
@Retention(RetentionPolicy.SOURCE)
public @interface ForTesting {
    // Nothing
}
