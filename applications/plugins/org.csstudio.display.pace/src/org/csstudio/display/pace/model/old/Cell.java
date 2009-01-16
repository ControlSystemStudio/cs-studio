package org.csstudio.display.pace.model.old;
import org.csstudio.platform.data.IValue;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.csstudio.utility.pv.epics.EPICS_V3_PV;
import org.csstudio.platform.data.ValueUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
/**
 * Cell
 * <p>
 * Stores information on the cells of the table..
 * 
 * @author Delphy Nypaver Armstrong
 */
public class Cell
{
   public enum Access
   {
      ReadOnly,
      ReadWrite
   }
   
   final private Access access;
   final private String name;
   final private EPICS_V3_PV pv;
   public boolean changed = false;
   final private PVListener pv_listener = new PVListener()
   {
      public void pvDisconnected(PV pv)
      {
         handleNewValue(null);
      }
      
      public void pvValueUpdate(PV pv)
      {
         handleNewValue(pv.getValue());
      }
   };
   
   /** Most recent value received from PV or <code>null</code> */
   private volatile IValue current_value;
   
   /** Value entered by user or <code>null</code> */
   private String user_value;
   
   /** Constructor
    *  @param acc The read/write access of the cell.
    *  @param PVname The name of the pv being displayed in the cell.
    *  
    *  Initialize the cell for table display.
    */
   public Cell(Access acc, String PVname)
   {
      access=acc;
      pv = new EPICS_V3_PV(PVname);
      name = "";
   }

   /** Constructor
    *  @param CellName The name of the cell.
    *  
    *  Reinitialize the cell for table display.
    */
   public Cell(String CellName)
   {
      access=Cell.Access.ReadOnly;
      name = CellName;
      pv = null;
   }
   
   /** @return The name of the pv being displayed in the cell. */
   public String getPvName()
   {
      if(pv==null) return "";
      return pv.getName();
   }

   /** @returnn the read/write access of the cell. */
   public Access getAccess()
   {
      return access;
   }

   /** @return Current value from PV */
   public String getCurrentValue()
   { 
      if(pv==null) return name;
      
      if (current_value == null) {
         return "--";
      }
      return ValueUtil.getString(current_value);
   }
   
   /** caput sets the current_value 
    * @param value Input value sent to channel access.
    */
   protected void handleNewValue(IValue value)
   {
      System.out.println(Thread.currentThread().getName() + " " + getPvName() + " Received New Value " + ValueUtil.getString(value));
      current_value = value;
   }
   
   /** calls caput 
   *   @param new_value Value to send to channel access.
   *   @param s Parent shell.
   */
   public void pvSetValue(String new_value, Shell s)
   {
      try
      {
         pv.setValue(new_value);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         MessageDialog.openError(s, "Error", ex.getMessage());
         return;
      }
   }
     
   /** Check if the value was edited.
    *  @return true if entered value differs from PV's value (as String)
    */
   public boolean hasUserValue()
   {
      if (user_value == null)
         return false;
      return ! ValueUtil.getString(current_value).equals(user_value);
   }
   
   /** @return Value entered by user or <code>null</code>
    *  @see #hasUserValue()
    */
   public String getUserValue()
   {
      return user_value;
   }

   /** Store the value input by the user on the screen. 
   *   @param value User input value.
   */
   public void setUserValue(String value)
   {
      user_value = value;
   }
   
   /** Write user-entered value to PV and reset the edit state 
   *   @param s Parent shell.
   */
   public void writeUserValue(Shell s)
   {
      try
      {
         pv.setValue(user_value);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         MessageDialog.openError(s, "Error", ex.getMessage());
         return;
      }
      user_value = null;
   }
   
   /**  Send the input value to channel access. 
   *   @param s Parent shell.
   *   @param value Input limit value.
   */
   public void writeMultiValue(Shell s, String value)
   {
      try
      {
         pv.setValue(value);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         MessageDialog.openError(s, "Error", ex.getMessage());
         return;
      }
   }
   
   /**  Start the pv listener. */
   public void start() throws Exception
   {
      pv.addListener(pv_listener);
      pv.start();
   }

   /**  Stop the pv listener. */
   public void stop() throws Exception
   {
      pv.stop();
      pv.removeListener(pv_listener);
   }
   
   public boolean isChanged()
   {
      changed =  ! ValueUtil.getString(current_value).equals(user_value);
      return changed;
   }
   
}
