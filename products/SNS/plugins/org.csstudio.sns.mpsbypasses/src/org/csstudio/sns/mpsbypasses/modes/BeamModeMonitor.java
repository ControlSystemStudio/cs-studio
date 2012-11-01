package org.csstudio.sns.mpsbypasses.modes;

import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.data.ExpressionLanguage.vEnum;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import org.epics.pvmanager.expression.DesiredRateExpressionListImpl;
import org.epics.util.time.TimeDuration;

/** Read beam mode from MPS PVs
 *
 *  <p>This is convoluted because instead of one PV to indicate the current mode,
 *  there are N PVs to reflect the on/off state of the possible modes,
 *  with only one PV supposed to be active at a given time.
 *
 *  <p>Additionally, the sense of 'active' differs for the RDTL vs. Switch mode PVs.
 *
 *  @author Delphy Armstrong - Original RTDL_Switch_Modes
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BeamModeMonitor
{
    final private Logger logger = Logger.getLogger(getClass().getName());

    final private BeamMode[] modes = BeamMode.values();
    final private BeamModeListener listener;

	private PVReader<List<VEnum>> rtdl_reader, switch_reader;

	private volatile BeamMode rtdl_mode = null;
	private volatile BeamMode switch_mode = null;

	/** Initialize
	 *  @param listener
	 */
	public BeamModeMonitor(final BeamModeListener listener)
	{
	    this.listener = listener;
	}

	/** Connect PVs
	 *  @throws Exception on error
	 */
	public void start() throws Exception
	{
        // Handle 'RTDL' PVs
	    DesiredRateExpressionList<VEnum> channels = new DesiredRateExpressionListImpl<VEnum>();
	    for (int i=0; i<modes.length; ++i)
	        channels.and(latestValueOf(vEnum("ICS_MPS:RTDL_BmMd:" + modes[i].getSignal())));
	    rtdl_reader = PVManager.read(listOf(channels)).maxRate(TimeDuration.ofSeconds(1.0));
	    rtdl_reader.addPVReaderListener(new PVReaderListener()
	    {
	        @Override
	        public void pvChanged()
	        {
	            final BeamMode mode;
	            final Exception error = rtdl_reader.lastException();
	            if (error != null)
	            {
	                logger.log(Level.WARNING, "RTDL Reader Error", error);
	                mode = null;
	            }
	            else
	                mode = getSelectedMode(rtdl_reader.getValue(), 1);
	            logger.log(Level.FINE, "RTDL Mode: {0}", mode);
	            updateModes(mode, switch_mode);
	        }
	    });

        // Handle 'Switch' PVs
	    channels = new DesiredRateExpressionListImpl<VEnum>();
	    for (int i=0; i<modes.length; ++i)
	        channels.and(latestValueOf(vEnum("ICS_MPS:Switch_BmMd:" + modes[i].getSignal())));
	    switch_reader = PVManager.read(listOf(channels)).maxRate(TimeDuration.ofSeconds(1.0));
	    switch_reader.addPVReaderListener(new PVReaderListener()
	    {
	        @Override
	        public void pvChanged()
	        {
	            final BeamMode mode;
	            final Exception error = switch_reader.lastException();
	            if (error != null)
	            {
	                logger.log(Level.WARNING, "Switch Reader Error", error);
	                mode = null;
	            }
	            else
	                mode = getSelectedMode(switch_reader.getValue(), 0);
                logger.log(Level.FINE, "Swtich Mode: {0}", mode);
                updateModes(rtdl_mode, mode);
	        }
	    });
	}

	/** Determine which of the values indicates an active mode
	 *  @param values Values of the mode PVs
	 *  @param active_value Value that indicates the active mode
	 *  @return Selected {@link BeamMode} or <code>null</code>
	 */
	private BeamMode getSelectedMode(final List<VEnum> values, final int active_value)
	{
	    if (values == null)
	        return null;

	    if (values.size() != modes.length)
	        throw new IllegalStateException();

	    int active = -1;
	    for (int i=0; i<modes.length; ++i)
	    {
	        if (values.get(i).getIndex() == active_value)
	        {
	            if (active >= 0)
	            {
	                Logger.getLogger(getClass().getName()).
	                    log(Level.WARNING,
	                        "Both {0} and {1} active at the same time",
	                        new Object[] { modes[active], modes[i] });
	                return null;
	            }
	            active = i;
	        }
	    }

	    if (active >= 0)
	        return modes[active];
	    return null;
	}

	/** Disconnect PVs */
	public void stop()
	{
	    switch_reader.close();
	    rtdl_reader.close();
		updateModes(null, null);
	}

	/** Update modes and notify listeners on change
	 *  @param new_rtdl_mode
	 *  @param new_switch_mode
	 */
	private void updateModes(final BeamMode new_rtdl_mode, final BeamMode new_switch_mode)
    {
		if (new_rtdl_mode == rtdl_mode  &&  new_switch_mode == switch_mode)
			return;
		rtdl_mode = new_rtdl_mode;
		switch_mode = new_switch_mode;
		listener.beamModeUpdate(new_rtdl_mode, new_switch_mode);
    }
}
