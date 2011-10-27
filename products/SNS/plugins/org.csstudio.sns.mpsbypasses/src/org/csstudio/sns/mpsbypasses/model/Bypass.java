package org.csstudio.sns.mpsbypasses.model;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/** Info about one Bypass
 * 
 *  <p>Combines the 'live' info from PVs with the 'static'
 *  info from the RDB
 *
 *  <p>Given a base PV name like 'Ring_Vac:SGV_AB:FPL_Ring',
 *  it will connect to the PVs 'Ring_Vac:SGV_AB:FPL_Ring_sw_jump_status'
 *  and 'Ring_Vac:SGV_AB:FPL_Ring_swmask'
 *  to determine if the bypass is possible (jumper)
 *  and actually masked (software mask),
 *  summarizing that as the live {@link BypassState}
 * 
 *  @author Delphy Armstrong - Original MPSBypassInfo
 *  @author Kay Kasemir
 */
public class Bypass implements PVListener
{
	final private String name;
	final private String chain;
	final private Request request;
	final private BypassListener listener;
	
	final private PV jumper_pv, mask_pv;
	private volatile BypassState state = BypassState.Disconnected;
	
	/** Initialize
	 *  @param pv_basename Base name, e.g. "Ring_Vac:SGV_AB:FPL_Ring"
	 *  @param request Who requested the bypass? <code>null</code> if not requested.
	 *  @param listener {@link BypassListener}
	 *  @throws Exception on error
	 */
	public Bypass(final String pv_basename, final Request request,
			final BypassListener listener) throws Exception
	{
		// Given a name like "Ring_Vac:SGV_AB:FPL_Ring",
		// extract the bypass name "Ring_Vac:SGV_AB"
		// and the MPS chain "FPL Ring"
		final int last_sep = pv_basename.lastIndexOf(":");
		if (last_sep > 0)
		{
			name = pv_basename.substring(0, last_sep);
			chain = pv_basename.substring(last_sep+1).replace('_', ' ');
		}
		else
		{
			name = pv_basename;
			chain = "?";
		}

		this.request = request;
		this.listener = listener;
		
		jumper_pv = PVFactory.createPV(pv_basename + "_sw_jump_status");
		mask_pv = PVFactory.createPV(pv_basename + "_swmask");
	}
	
	/** Create a pseudo-Bypass that is used to display
	 *  messages in the bypass table
	 *  @param message Message
	 *  @param detail Detail that will show in ()
	 */
	public Bypass(final String message, final String detail)
	{
		name = message;
		chain = detail;
		request = null;
		listener = null;
		jumper_pv = null;
		mask_pv = null;
	}

	/** @return Bypass name, for example "Ring_Vac:SGV_AB" */
	public String getName()
	{
		return name;
	}

	/** @return MPS chain, for example "FPL_Ring" */
	public String getMPSChain()
	{
		return chain;
	}
	
	/** @return Bypass name and chain, for example "Ring_Vac:SGV_AB (FPL Ring)" */
	public String getFullName()
	{
		return name + " (" + chain + ")";
	}

	
	/** @return Request for this bypass or <code>null</code> */
	public Request getRequest()
	{
		return request;
	}
	
	/** @return Bypass state */
	public BypassState getState()
	{
		return state;
	}
	
	/** Connect to PVs */
	public void start() throws Exception
	{
		if (jumper_pv == null)
			return;
		jumper_pv.addListener(this);
		mask_pv.addListener(this);

		jumper_pv.start();
		mask_pv.start();
	}

	/** Disconnect PVs */
	public void stop()
	{
		if (jumper_pv == null)
			return;
		jumper_pv.removeListener(this);
		mask_pv.removeListener(this);
		jumper_pv.stop();
		mask_pv.stop();
		
		state = BypassState.Disconnected;
		// Does NOT notify listener
		// because the way this is used the listener
		// will soon see a different list of bypasses
		// or close down.
		// Either way, no update needed.
	}
	
	/** @see PVListener */
	@Override
    public void pvValueUpdate(final PV pv)
    {
		updateState(jumper_pv.getValue(), mask_pv.getValue());
    }

	/** @see PVListener */
	@Override
    public void pvDisconnected(final PV pv)
    {
		updateState(jumper_pv.getValue(), mask_pv.getValue());
    }

	/** Update alarm state from current values of PVs 
	 * @param jumper
	 * @param mask 
	 */
	private void updateState(final IValue jumper, final IValue mask)
    {
		// Anything unknown?
		if (mask == null  ||  jumper == null  ||
			!mask_pv.isConnected()  ||  !jumper_pv.isConnected())
		{
			state = BypassState.Disconnected;
		}
		else
		{	// Determine state
			final boolean jumpered = ValueUtil.getDouble(jumper) > 0.0;
			final boolean masked = ValueUtil.getDouble(mask) > 0.0;
			
			if (jumpered)
			{
				if (masked)
					state = BypassState.Bypassed;
				else
					state = BypassState.Bypassable;
			}
			else
			{
				if (masked)
					state = BypassState.InError;
				else
					state = BypassState.NotBypassable;
			}
		}
		
	    // send update
		listener.bypassChanged(this);
    }

	/** @return Debug representation */
	@Override
    public String toString()
    {
	    return "Bypass " + name + ", state " + state +
	        ", requested by " + (request != null ? request : "nobody");
    }
}
