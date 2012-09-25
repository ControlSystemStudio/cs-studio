/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.lang.annotation.Documented;

/**
 * Annotation to flag which fields are considered part of the metadata.
 * Metadata changes at a much slower rate than data, typically does not
 * change during a session and should be optimized accordingly.
 * <p>
 * In Epics V3, these fields are fetched once at each connection, while
 * in Epics V4 are monitored as the rest.
 * 
 * @author carcassi
 */
@Documented
public @interface Metadata {

}
