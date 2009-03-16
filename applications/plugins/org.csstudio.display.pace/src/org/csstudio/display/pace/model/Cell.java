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
    
    /** If comment has already been logged with the limit value */
    private boolean beenLogged = false;
        
    // Cell information for the name of the person who made the change and
    // the date of the change, if the primary cell has comments.
    private PV name_pv, date_pv, comment_pv;
    
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
        String pv_name = Macro.apply(instance.getMacros(), column.getPvWithMacros());

        //  Create pvs and add listeners
        this.pv = PVFactory.createPV(pv_name);
        pv.addListener(this);
        pv_name=Macro.apply(instance.getMacros(), column.getNamePvWithMacros());
        this.name_pv = PVFactory.createPV(pv_name);
        name_pv.addListener(this);
        
        // Create the comment pvs and listeners, if they are defined in the XML file
        pv_name=Macro.apply(instance.getMacros(), column.getDatePvWithMacros());
        this.date_pv = PVFactory.createPV(pv_name);
        date_pv.addListener(this);
        pv_name=Macro.apply(instance.getMacros(), column.getCommentPvWithMacros());
        this.comment_pv = PVFactory.createPV(pv_name);
        comment_pv.addListener(this);
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
    
    // @return <code>true</code> if the name_pv, comment_pv or date_pv were found,
    // in the XML file, otherwise false.
    public boolean hasComments()
    {
        return (name_pv.getName().length()>0 || date_pv.getName().length()>0 ||
              comment_pv.getName().length()>0);
    }
    
    /** Even though a cell may be configured as writable,
     *  the underlying PV might still prohibit write access. 
     *  @return <code>true</code> for PVs that can be written.
     */
    public boolean isPVWriteAllowed()
    {
        return pv.isWriteAllowed();
    }
    
    /** @return comment PV or <code>null</code>
     */
    public PV comment_pv()
    { 
       return this.comment_pv;
    }
    
    /** @return user name PV or <code>null</code>
     */
    public PV name_pv()
    { 
       return this.name_pv;
    }
    
    /** @return user name PV or <code>null</code>
     */
    public PV date_pv()
    { 
       return this.date_pv;
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
        // When the value is save, put the beenLogged flag back to false.
        beenLogged = false;
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
        if(name_pv!=null && name_pv.getName().length()>0) name_pv.start();
        if(date_pv!=null && date_pv.getName().length()>0) date_pv.start();
        if(comment_pv!=null && comment_pv.getName().length()>0) comment_pv.start();
    }

    /** Stop the PV connection */
    public void stop()
    {
        pv.stop();
        if(name_pv!=null && name_pv.getName().length()>0) name_pv.stop();
        if(date_pv!=null && date_pv.getName().length()>0) date_pv.stop();
        if(comment_pv!=null && comment_pv.getName().length()>0) comment_pv.stop();
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
    
    // Set the beenLogged flag.
    /** @return true if Cell changes have already been logged */
    public boolean beenLogged()
    {
       return this.beenLogged;
    }
 
    /** Mark Cell changes as already logged */
    public void markAsLogged()
    {
       beenLogged = true;
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
