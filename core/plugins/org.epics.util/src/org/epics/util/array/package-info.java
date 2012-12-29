/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/**
 * Set of classes to handle iteration and read-only references to
 * collections and arrays of primitives, without having to code for each
 * individual case.
 * <p>
 * The design is loosely inspired by the Collection framework, but does not
 * directly implement it. Due to the invariant nature of Java generics,
 * it would make the usage of inheritance awkward. See {@link org.epics.util.array.IteratorNumber}
 * for more details.
 */
package org.epics.util.array;
