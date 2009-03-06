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
public class SubCell 
{
   
   private volatile String pv_value = null;
   public PV pv_name = null;
   private Instance instance;
   private Column column;
   
    public SubCell(Instance instance, Column column, String pvname, PVListener listener) throws Exception
   {
       pv_name = PVFactory.createPV(pvname);
       pv_name.addListener(listener);
       this.instance = instance;
       this.column = column;
    }

   /** @return Value that user entered to replace the original value,
    *          or <code>null</code>
    */
   public String getPvValue()
   {
      if(pv_value==null)
         pv_value = ValueUtil.getString(pv_name.getValue());
       return pv_value;
   }

   public String getPvName()
   {
       return pv_name.getName();
   }

   public void setPvValue(String value)
   {
     pv_value = value;
   }

   // PVListener
   public void pvDisconnected(final PV pv)
   {
       pv_value = null;
   }

   // PVListener
   public void pvValueUpdate(final PV pv)
   {
       pv_value = ValueUtil.getString(pv.getValue());
   }
   
   /** Start the PV connection */
   public void start() throws Exception
   {
       pv_name.start();
   }

   /** Stop the PV connection */
   public void stop()
   {
       pv_name.stop();
   }
}
