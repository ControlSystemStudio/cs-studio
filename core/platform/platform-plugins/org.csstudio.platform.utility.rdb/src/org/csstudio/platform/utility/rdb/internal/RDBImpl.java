/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb.internal;

import java.sql.Connection;

import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** Specific RDB implementation
 *  @author Kay Kasemir
 */
public interface RDBImpl
{
    /** @return Dialect info. */
    public Dialect getDialect();

    /** Create the database connection.
     *  @param url RDB URL
     *  @param user User name or <code>null</code> if part of url
     *  @param password Password or <code>null</code> if part of url
     *  @return JDBC connection
     *  @throws Exception on error
     */
    public Connection connect(final String url,
            final String user, final String password) throws Exception;

    /** Derived classes must implement this to provide a statement that's
     *  suitable for testing the connection state.
     *  @return SQL for statement that gives a cheap way of testing the
     *          connection state
     */
    public String getConnectionTestQuery();
}
