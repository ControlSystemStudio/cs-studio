/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
/**
 * Support for control system types.
 * <p>
 * This package contains the data definitions for all the EPICS types.
 * These are in terms of Java interfaces so that each data source can
 * map directly to their own structure.
 * <p>
 * The interfaces starting with Dbr represent actual types that can be
 * taken by a data source. The other interfaces represent atomic elements
 * that can be treated separately so that generic support can be written against
 * them (i.e. one can write support for alarms regardless of the actual type).
 * <p>
 * TODO: alarm acknowledgment? Not in this package...
 * TODO: histograms for statistics on enums?
 * TODO: what about toString? Java default? Only value? Full information?
 * TODO: equals and hashcodes? Each object different? Compare on the data only?
 *       if we do, what about compare?
 */
package org.epics.pvmanager.vtype;
