/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.model;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.display.pace.Messages;
import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVListenerAdapter;
import org.csstudio.vtype.pv.PVPool;
import org.diirt.vtype.VType;

/** One cell in the model.
 *  <p>
 *  Knows about the Instance and Column where this cell resides,
 *  connects to a PV, holds the most recent value of the PV
 *  as well as an optional user value that overrides the PV's value.
 *  <p>
 *  In addition, a cell might have "meta PVs" that contain the name
 *  of the user, date, and a comment regarding the last change
 *  of the "main" PV.
 *
 *  @author Kay Kasemir
 *  @author Delphy Nypaver Armstrong
 */
public class Cell
{
    /** Date format used for updating the last_date_pv */
    final private static DateFormat date_format =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

    final private Instance instance;

    final private Column column;

    /** Name of the cell's PV.
     *  Matches pv.getName() except for possible 'decoration'
     *  like "ca://" that the PV adds.
     */
    final private String pv_name;

    /** Control system PV for 'live' value */
    private PV pv;

    /** Most recent value received from PV */
    private volatile String current_value = null;

    /** Value that the user entered. */
    private volatile String user_value = null;

    /** Optional PVs for the name of the person who made the last change,
     *  the date of the change, and a comment.
     *  Either may be <code>null</code>
     */
    private PV last_name_pv, last_date_pv, last_comment_pv;
    private volatile String last_name = null, last_date = null, last_comment = null;

    /** Listener to PV which updates cell with value's text */
    private abstract class StringUpdatePVListener extends PVListenerAdapter
    {
        @Override
        public void valueChanged(final PV pv, final VType value)
        {
            updateString(VTypeHelper.getString(value));
            instance.getModel().fireCellUpdate(Cell.this);
        }

        @Override
        public void disconnected(PV pv)
        {
            updateString(null);
            instance.getModel().fireCellUpdate(Cell.this);
        }

        protected abstract void updateString(final String value);
    };

    private final PVListener pv_listener = new StringUpdatePVListener()
    {
        @Override
        protected void updateString(final String value)
        {
            current_value = value;
        }
    };

    private final PVListener last_name_listener = new StringUpdatePVListener()
    {
        @Override
        protected void updateString(final String value)
        {
            last_name = value;
        }
    };

    private final PVListener last_date_listener = new StringUpdatePVListener()
    {
        @Override
        protected void updateString(final String value)
        {
            last_date = value;
        }
    };

    private final PVListener last_comment_listener = new StringUpdatePVListener()
    {
        @Override
        protected void updateString(final String value)
        {
            last_comment = value;
        }
    };

    /** Initialize
     *  @param instance Instance (row) that holds this cell
     *                  and provides the macro substitutions for the cell
     *  @param column   Column that holds this cell
     *                  and provides the macro-ized PV name
     *                  for all cells in the column
     *  @throws Exception on error in macros
     */
    public Cell(final Instance instance, final Column column) throws Exception
    {
        this.instance = instance;
        this.column = column;
        pv_name = MacroUtil.replaceMacros(column.getPvWithMacros(), instance.getMacros());
    }

    /** @return Instance (row) that contains this cell */
    public Instance getInstance()
    {
        return instance;
    }

    /** @return Column that contains this cell */
    public Column getColumn()
    {
        return column;
    }

    /** @return <code>true</code> for read-only cell */
    public boolean isReadOnly()
    {
        return pv != null  &&  column.isReadonly();
    }

    /** Even though a cell may be configured as writable,
     *  the underlying PV might still prohibit write access.
     *  @return <code>true</code> for PVs that can be written.
     */
    public boolean isPVWriteAllowed()
    {
        return pv != null  && !pv.isReadonly();
    }

    /** If the user entered a value, that's it.
     *  Otherwise it's the PV's value, or UNKNOWN
     *  if we have nothing.
     *  @return Value of this cell
     */
    public String getValue()
    {
        if (user_value != null)
            return user_value;
        if (current_value != null)
            return current_value;
        return Messages.UnknownValue;
    }

    /** @return Original value of PV or <code>null</code>
     */
    public String getCurrentValue()
    {
        return current_value;
    }

    /** Set a user-specified value.
     *  <p>
     *  If this value matches the PV's value, we revert to the PV's value.
     *  Otherwise this defines a new value that the user entered to
     *  replace the original value of the PV.
     *  @param value Value that the user entered for this cell
     */
    public void setUserValue(final String value)
    {
        if (value.equals(current_value))
            user_value = null;
        else
            user_value = value;
        instance.getModel().fireCellUpdate(this);
    }

    /** @return Value that user entered to replace the original value,
     *          or <code>null</code>
     */
    public String getUserValue()
    {
        return user_value;
    }

    private String original_pv_value = null;
    private String original_name_value = null;
    private String original_date_value = null;

    /** Save value entered by user to PV
     *
     *  <p>On success, this should be followed by a call to <code>clearUserValue</code>,
     *  or rolled back via a call to <code>revertOriginalValue</code>
     *
     *  @param user_name Name of the user to be logged for cells with
     *                   a last user meta PV
     *  @throws Exception on error
     */
    public void saveUserValue(final String user_name) throws Exception
    {
        if (!isEdited())
            return;
        if (pv != null)
        {
            if (! isPVWriteAllowed())
                throw new Exception(pv.getName() + " is read-only");
            original_pv_value = current_value;
            pv.write(user_value);
        }
        if (last_name_pv != null)
        {
            original_name_value = last_name;
            last_name_pv.write(user_name);
        }
        if (last_date_pv != null)
        {
            original_date_value = last_date;
            last_date_pv.write(date_format.format(new Date()));
        }
    }

    /** Revert to the state before a user value was saved
     *  @throws Exception on error
     */
    public void revertOriginalValue() throws Exception
    {
        if (pv != null  &&  original_pv_value != null)
            pv.write(original_pv_value);
        if (last_name_pv != null  &&  original_name_value != null)
            last_name_pv.write(original_name_value);
        if (last_date_pv != null  &&  original_date_value != null)
            last_date_pv.write(original_date_value);
    }

    /** Clear a user-specified value */
    public void clearUserValue()
    {
        user_value = null;
        original_pv_value = null;
        original_name_value = null;
        original_date_value = null;
        instance.getModel().fireCellUpdate(this);
    }

    /** @return <code>true</code> if user entered a value */
    public boolean isEdited()
    {
        return user_value != null;
    }


    /** @return <code>true</code> if the cell has meta information about
     *  the last change
     *  @see #getLastComment()
     *  @see #getLastDate()
     *  @see #getLastUser()
     */
    public boolean hasMetaInformation()
    {
        return last_name_pv != null || last_date_pv != null ||
              last_comment_pv != null;
    }

    /** @return User name for last change to the main PV */
    public String getLastUser()
    {
        return getOptionalValue(last_name);
    }

    /** @return Date of last change to the main PV */
    public String getLastDate()
    {
        return getOptionalValue(last_date);
    }

    /** @return Comment for last change to the main PV */
    public String getLastComment()
    {
        return getOptionalValue(last_comment);
    }

    /** Get value of optional PV
     *  @param optional_pv PV to check, may be <code>null</code>
     *  @return Last value, never <code>null</code>
     */
    private String getOptionalValue(final String string)
    {
        if (string == null  ||  string.isEmpty())
            return Messages.UnknownValue;
        return string;
    }

    /** Start the PV connection */
    public void start() throws Exception
    {
        //  Create the main PV and add listener
        if (pv_name.length() <= 0)
            pv = null;
        else
        {
            pv = PVPool.getPV(pv_name);
            pv.addListener(pv_listener);
        }

        // Create the optional comment pvs.
        String name=MacroUtil.replaceMacros(column.getNamePvWithMacros(), instance.getMacros());
        if (name.length() <= 0)
            last_name_pv = null;
        else
        {
            last_name_pv = PVPool.getPV(name);
            last_name_pv.addListener(last_name_listener);
        }
        name = MacroUtil.replaceMacros(column.getDatePvWithMacros(), instance.getMacros());
        if (name.length() <= 0)
            last_date_pv = null;
        else
        {
            last_date_pv = PVPool.getPV(name);
            last_date_pv.addListener(last_date_listener);
        }

        name = MacroUtil.replaceMacros(column.getCommentPvWithMacros(), instance.getMacros());
        if (name.length() <= 0)
            last_comment_pv = null;
        else
        {
            last_comment_pv = PVPool.getPV(name);
            last_comment_pv.addListener(last_comment_listener);
        }
    }

    /** Stop the PV connection */
    public void stop()
    {
        if (last_comment_pv != null)
            PVPool.releasePV(last_comment_pv);
        if (last_date_pv != null)
            PVPool.releasePV(last_date_pv);
        if (last_name_pv != null)
            PVPool.releasePV(last_name_pv);
        if (pv != null)
            PVPool.releasePV(pv);

        last_comment_pv = null;
        last_date_pv = null;
        last_name_pv = null;
        pv = null;
    }

    /** @return PV name */
    public String getName()
    {
        return pv_name;
    }

    /** @return Name of comment PV or "" */
    public String getCommentPVName()
    {
        if (last_comment_pv == null)
            return ""; //$NON-NLS-1$
        return last_comment_pv.getName();
    }

    /** @return String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "Cell " + pv_name + " = " + getValue();
    }
}
