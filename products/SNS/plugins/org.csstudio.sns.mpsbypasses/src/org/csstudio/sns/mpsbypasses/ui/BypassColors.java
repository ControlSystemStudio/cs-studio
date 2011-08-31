package org.csstudio.sns.mpsbypasses.ui;

import org.csstudio.sns.mpsbypasses.model.Bypass;
import org.csstudio.sns.mpsbypasses.model.BypassState;
import org.csstudio.sns.mpsbypasses.model.Request;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** Creates bypass display colors used for indicating the state of a {@link Bypass}
 *  combined with its {@link Request}
 * 		
 *  @author Delphy Armstrong - Original Author
 *  @author Kay Kasemir
 */
public class BypassColors
{
	final private Color orange;
	final private Color lavender;
	final private Color gold;
	final private Color silver;
	final private Color blue;
	final private Color red;

	/** create the Bypass display colors */
	public BypassColors(final Composite parent) 
	{
		final Display display = parent.getDisplay();
		orange = new Color(display, 255, 140, 0);
		lavender = new Color(display, 224, 102, 255);
		gold = new Color(display, 218, 165, 32);
		silver = new Color(display, 192, 192, 192);
		blue = new Color(display, 0, 191, 255);
		red = new Color(display, 255, 0, 0);
		parent.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				dispose();
			}
		});
	}

	/** dispose of the bypass colors when the display containing them is disposed of */
	private void dispose()
	{
		orange.dispose();
		lavender.dispose();
		gold.dispose();
		silver.dispose();
		blue.dispose();
		red.dispose();
	}

	/** Return the appropriate color based on the bypass state and bypass request status. 
	 *  See color code table in the online help document for an explanation.
	 *  @param bypass_state {@link BypassState}
	 *  @param requested Was the bypass requested?
	 */
	public Color getBypassColor(final BypassState bypass_state, final boolean requested)
	{
		switch (bypass_state)
		{
		case Bypassed:
			if (requested) 
				return silver; 
			return orange;
		case Bypassable:
			if (requested)
				return lavender;
			return blue;
		case InError:
			return red;
		case NotBypassable:
		case Disconnected:
			if (requested) 
				return gold;
			return blue;
		default:
			return null;
		}
	}
}

