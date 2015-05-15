/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

/** Helpers for using JUnit, Hamcrest, ...
 *
 *  <p>Eclipse already provides a JUnit (4) plugin,
 *  which in turn includes org.hamcrest.
 *  That, however, is only hamcrest-core.
 *
 *  <p>To really use hamcrest, it's nice to also have
 *  hamcrest-library.
 *  Simply adding hamcrest-library.jar via a new plugin is,
 *  however, much harder than originally expected.
 *
 *  <p>The Eclipse junit.jar and hamcrest-core.jar files are signed,
 *  and cannot be extended with an unsigned hamcrest-library.jar
 *  that uses the same package names.
 *
 *  <p>When completely ignoring the Eclipse junit.jar and hamcrest-core.jar files
 *  and instead using a different, unsigned copy of those jars,
 *  one can add hamcrest-library.jar, but those will then only work
 *  for plain JUnit tests, not for JUnit plug-in tests.
 *
 *  <p>This plugin uses a very lame middle way:
 *  It imports the Eclipse junit and hamcrest-core plugins,
 *  and provides its own Hamcrest {@link org.hamcrest.Matcher}s
 *  as needed for the testing of CSS code.
 *  The names of those matchers are the same as in hamcrest-library
 *  (<code>closeTo</code>, ...), but obviously not in the 'correct'
 *  package.
 *
 *  <p>Hopefully one day this plugin becomes either obsolete,
 *  or it can simply include the full hamcrest-library.
 *
 * @author Kay Kasemir
 */
package org.csstudio.utility.test;

