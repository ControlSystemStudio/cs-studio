package org.csstudio.utility.logsender;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/** GUI of the log sender
 *  @author Kay Kasemir
 */
public class GUI
{
    private Text text;
    private Combo level;
    private Button send;

    enum Level
    {
        Fatal, Error, Warning, Info, Debug
    }
    
    public GUI(final Composite parent)
    {
        createComponents(parent);
        send.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                sendMessage();
            }
        });
    }

    private void createComponents(final Composite parent)
    {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        parent.setLayout(layout);
        
        // Text:  __________________
        // Level: [level]     [send]
        Label l = new Label(parent, 0);
        l.setText(Messages.Text);
        l.setLayoutData(new GridData());
        
        text = new Text(parent, SWT.BORDER);
        text.setToolTipText(Messages.TextTT);
        text.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));
    
        // New row
        l = new Label(parent, 0);
        l.setText(Messages.Level);
        l.setLayoutData(new GridData());
        
        level = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
        level.setToolTipText(Messages.LevelTT);
        level.setLayoutData(new GridData(0, 0, true, false));
        for (Level lvl : Level.values())
            level.add(lvl.name());
        level.select(Level.Info.ordinal());
    
        send = new Button(parent, SWT.PUSH);
        send.setText(Messages.Send);
        send.setToolTipText(Messages.SendTT);
        send.setLayoutData(new GridData());
    }

    /** Make log entry with current GUI content */
    private void sendMessage()
    {
        final Logger logger = CentralLogger.getInstance().getLogger(this);
        final String message = text.getText().trim();
        if (level.getSelectionIndex() == Level.Debug.ordinal())
        {
            logger.debug(message);
            return;
        }
        else if (level.getSelectionIndex() == Level.Info.ordinal())
        {
            logger.info(message);
            return;
        }
        else if (level.getSelectionIndex() == Level.Warning.ordinal())
        {
            logger.warn(message);
            return;
        }
        else if (level.getSelectionIndex() == Level.Fatal.ordinal())
        {
            logger.fatal(message);
            return;
        }
        // else: Should be error. Use that as default
        logger.error(message);
    }

    public void setFocus()
    {
        text.setFocus();
    }
}
