/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import java.lang.annotation.Documented;

/**
 * Annotation to flag which fields are considered part of the metadata.
 * In Epics V3, these fields are fetched once at each connection, while
 * in Epics V5 are monitored as the rest.
 * 
 * @author carcassi
 */
@Documented
public @interface Metadata {

}
