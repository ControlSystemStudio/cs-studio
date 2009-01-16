package org.csstudio.display.pace.model;

import org.csstudio.platform.data.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/** One cell in the model: PV, its most recent value, user's value, ...
 *  @author Kay Kasemir
 *  @author Delphy Nypaver Armstrong
 */
@SuppressWarnings("nls")
public class Cell implements PVListener
{
    private static final String UNKNOWN = "--";

    final private Instance instance;

    final private Column column;
    
    /** Control system PV for 'live' value */
    final private PV pv;
    
    /** Most recent value received from PV */
    private String current_value = UNKNOWN;

    /** Initialize
     *  @param instance Instance (row) that holds this cell
     *  @param column Column that holds this cell
     *  @throws Exception on error
     */
    public Cell(final Instance instance, final Column column) throws Exception
    {
        this.instance = instance;
        this.column = column;
        final String pv_name = Macro.apply(instance.getMacros(), column.getPvWithMacros());
        this.pv = PVFactory.createPV(pv_name);
        pv.addListener(this);
    }

    /** @return <code>true</code> for read-only cell */
    public boolean isReadOnly()
    {
        return column.isReadonly();
    }
    
    /** @return PV name */
    public String getPV()
    {
        return pv.getName();
    }

    public boolean isEdited()
    {
        return false;
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

    public void pvDisconnected(final PV pv)
    {
        current_value = UNKNOWN;
        instance.getModel().fireCellUpdate(this);
    }

    public void pvValueUpdate(final PV pv)
    {
        current_value = ValueUtil.getString(pv.getValue());
        instance.getModel().fireCellUpdate(this);
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        return "Cell " + pv.getName() + " = " + current_value;
    }
}
