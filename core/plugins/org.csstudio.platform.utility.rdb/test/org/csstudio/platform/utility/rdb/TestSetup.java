/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb;

@SuppressWarnings("nls")
public interface TestSetup
{
    /** Must adjust these for your site! */
    final public static String URL_MYSQL =
            "jdbc:mysql://localhost/ARCHIVE?user=archive&password=$archive";
    final public static String URL = URL_MYSQL;
}
