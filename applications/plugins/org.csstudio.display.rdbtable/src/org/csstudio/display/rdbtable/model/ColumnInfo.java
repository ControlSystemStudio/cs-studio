/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable.model;

/** Information about one table column.
 *  @author Kay Kasemir
 */
public class ColumnInfo
{
    final private String header;
    final private int width;
    
    /** Initialize
     *  @param header Column name (header)
     *  @param width Column widths (in percent)
     */
    public ColumnInfo(final String header, final int width)
    {
        this.header = header;
        this.width = width;
    }

    /** @return Column name (header) */
    protected String getHeader()
    {
        return header;
    }

    /** @return Column widths (in percent) */
    protected int getWidth()
    {
        return width;
    }
}
