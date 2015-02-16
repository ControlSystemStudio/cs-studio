/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values;

/** Localization.
 *  <p>
 *  Not really using messages.properties etc.,
 *  just hard coded so that this works as a plain
 *  library, but at least a start towards localization.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Messages
{
    final public static String ColumnSeperator = "\t";
    final public static String ValueSevrStatSeparator = " [";
    final public static String SevrStatSeparator = ", ";
    final public static String SevrStatEnd = "]";
    final public static String ArrayElementSeparator = ", ";
    final public static String NoValue = "#N/A";
    final public static String Infinite = "Inf";
    final public static String NaN = "NaN";

    final public static String SevOK = "OK";
    final public static String SevMinor = "MINOR";
    final public static String SevMajor = "MAJOR";
    final public static String SevInvalid = "INVALID";

    final public static String MiniMaxiFormat = " [ {0} ... {1} ]";
}
