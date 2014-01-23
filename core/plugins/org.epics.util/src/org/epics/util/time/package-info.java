/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
/**
 * Contains basic common classes to handle time at nanosecond precision.
 * <p>
 * <h3>JSR 310 compatibility</h3>
 * Java 8 will introduce a better time definition that is going to be very
 * similar to these class. That effort is unfortunately too unstable to use
 * directly. When it will be released, the plan is to phase out this package
 * and use the standard where possible. Same definitions and conventions
 * are taken from JSR 310 to make future conversion easier.
 * 
 */
package org.epics.util.time;
