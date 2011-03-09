/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable.model;

/** One row in the RDBTable
 *  @author Kay Kasemir
 */
public class RDBTableRow
{
    /** Model that contains this row */
    final RDBTableModel model;
    
    /** Columns in this row */
    final private String columns[];

    /**  Was this row read from the RDB, or added to the model later on? */
    final private boolean read_from_rdb;

    /** Was anything in this row edited? */
    private boolean was_modified;

    /** Was this row marked for deletion? */
    private boolean was_deleted = false;
   
    /** Initialize
     *  @param model Model that contains this row
     *  @param column_data Strings for the columns in this row
     *  @param read_from_rdb <code>true</code> if this row was read from the RDB,
     *                       <code>false</code> if it was added to the model later on
     */
    protected RDBTableRow(final RDBTableModel model,
            final String column_data[], final boolean read_from_rdb)
    {
        this.model = model;
        this.columns = column_data;
        this.read_from_rdb = read_from_rdb;
        was_modified = !read_from_rdb;
    }

    /** @param column Column index
     *  @return Value of that column
     */
    public String getColumn(final int column)
    {
        return columns[column];
    }

    /** @param column Column index
     *  @param new_value New value for that column
     */
    public void setColumn(final int column, final String new_value)
    {
        if (! new_value.equals(columns[column]))
        {
            columns[column] = new_value;
            was_modified = true;
            model.fireRowChanged(this);
        }
    }

    /** Mark row for deletion */
    public void delete()
    {
        was_deleted = true;
        model.fireRowChanged(this);
    }

    /** @return Was this row read from the RDB, or added to the model later on? */
    public boolean wasReadFromRDB()
    {
        return read_from_rdb;
    }
    
    /** @return <code>true</code> when row was deleted */
    public boolean wasDeleted()
    {
        return was_deleted;
    }
    
    /** @return <code>true</code> when row was edited after
     *          reading original RDB data
     */
    public boolean wasModified()
    {
        return was_modified ;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        final StringBuilder txt = new StringBuilder();
        for (String col : columns)
        {
            if (txt.length() > 0)
                txt.append(", ");
            txt.append(col);
        }
        return txt.toString();
    }
}
