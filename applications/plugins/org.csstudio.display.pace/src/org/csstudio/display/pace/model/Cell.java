package org.csstudio.display.pace.model;

import org.csstudio.display.pace.Messages;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/** One cell in the model.
 *  Knows about the Instance and Column where this cell resides,
 *  connects to a PV, holds the most recent value of the PV
 *  as well as an optional user value that overrides the PV's value
 *  
 *  @author Kay Kasemir
 *  @author Delphy Nypaver Armstrong
 *  
 *   reviewed by Delphy 01/29/09
 */
public class Cell implements PVListener, IProcessVariable
{
    final private Instance instance;

    final private Column column;
    
    /** Control system PV for 'live' value */
    final private PV pv;
    
    /** Most recent value received from PV */
    private volatile String current_value = null;

    /** Value that the user entered. */
    private volatile String user_value = null;
    
    /** Initialize
     *  @param instance Instance (row) that holds this cell
     *                  and provides the macro substitutions for the cell
     *  @param column   Column that holds this cell
     *                  and provides the macro-ized PV name
     *                  for all cells in the column
     *  @throws Exception on error in macro substitution or PV creation
     */
    public Cell(final Instance instance, final Column column) throws Exception
    {
        this.instance = instance;
        this.column = column;
        final String pv_name = Macro.apply(instance.getMacros(), column.getPvWithMacros());
        this.pv = PVFactory.createPV(pv_name);
        pv.addListener(this);
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
        return column.isReadonly();
    }
    
    /** Even though a cell may be configured as writable,
     *  the underlying PV might still prohibit write access. 
     *  @return <code>true</code> for PVs that can be written.
     */
    public boolean isPVWriteAllowed()
    {
        return pv.isWriteAllowed();
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

    /** Clear a user-specified value, revert to the PV's original value. */
    public void clearUserValue()
    {
        user_value = null;
        instance.getModel().fireCellUpdate(this);
    }
    
    /** Save value entered by user to PV
     *  @throws Exception on error
     */
    public void saveUserValue() throws Exception
    {
        if (!isEdited())
            return;
        pv.setValue(user_value);
    }

    /** @return <code>true</code> if user entered a value */
    public boolean isEdited()
    {
        return user_value != null;
    }

    /** Start the PV connection */
    public void start() throws Exception
    {
        pv.start();
    }

    /** Stop the PV connection */
    public void stop()
    {
        pv.stop();
    }

    // PVListener
    public void pvDisconnected(final PV pv)
    {
        current_value = null;
        instance.getModel().fireCellUpdate(this);
    }

    // PVListener
    public void pvValueUpdate(final PV pv)
    {
        current_value = ValueUtil.getString(pv.getValue());
        instance.getModel().fireCellUpdate(this);
    }

    // IProcessVariable
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter)
    {
        return null;
    }

    /** @return PV name
     *  @see IProcessVariable
     */
    public String getName()
    {
        return pv.getName();
    }

    // IProcessVariable
    public String getTypeId()
    {
        return IProcessVariable.TYPE_ID;
    }

    /** @return String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "Cell " + pv.getName() + " = " + getValue();
    }
}
