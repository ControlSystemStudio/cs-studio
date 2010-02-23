package org.csstudio.trends.databrowser.ui;

import org.csstudio.trends.databrowser.Messages;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Label;

/** Button (Draw2d) for triggering start/end time configuration
 *  @author Kay Kasemir
 */
public class TimeConfigButton extends Button
{
    /** Listener to invoke on button presses */
    private PlotListener listener = null;
    
    /** Initialize */
    public TimeConfigButton()
    {
        Label text = new Label(Messages.StartEndDialogBtn);
        setContents(text);
        setToolTip(new Label(Messages.StartEndDialogTT));
        addActionListener(new ActionListener()
        {
            public void actionPerformed(final ActionEvent event)
            {
                if (listener != null)
                    listener.timeConfigRequested();
            }
        });
    }

    /** Add a listener that will be informed about scroll on/off requests */
    public void addPlotListener(final PlotListener listener)
    {
        if (this.listener != null)
            throw new IllegalStateException();
        this.listener = listener;
    }
}
