package org.csstudio.diag.pvfields;

import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vType;
import static org.epics.util.time.TimeDuration.ofMillis;
import static org.epics.util.time.TimeDuration.ofSeconds;

import org.csstudio.diag.pvfields.model.PVFieldListener;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VType;


public class PVField
{
    final private String name;
    final private String original_value;
    // TODO SYNC
    private String current_value = "<disconnected>";
	private PVReader<VType> pv;
    
    public PVField(final String name, final String original_value)
    {
        this.name = name;
        this.original_value = original_value;
    }

    public String getName()
    {
    	return name;
    }

    public String getOriginalValue()
    {
    	return original_value;
    }
    
    public String getCurrentValue()
    {
		return current_value;
	}

	public void start(final PVFieldListener listener)
	{
		if (pv != null)
			throw new IllegalStateException("Already started");
		
		final PVReaderListener pv_listener = new PVReaderListener()
        {
			@Override
			public void pvChanged()
			{
				final Exception ex = pv.lastException();
				if (ex != null)
					current_value = "Error: " + ex.getMessage();
				else
					current_value = VTypeHelper.toString(pv.getValue());
				listener.updateField(PVField.this);
			}
		};
		pv = PVManager.read(latestValueOf(vType(name))).timeout(ofMillis(Preferences.getTimeout())).listeners(pv_listener).maxRate(ofSeconds(0.5));
	}

	public void stop()
	{
		pv.close();
	}

	@Override
	public String toString()
	{
	    return String.format(
	            "%s: original_value=%s, current_value=%s",
	            name, original_value, current_value);
	}
}
