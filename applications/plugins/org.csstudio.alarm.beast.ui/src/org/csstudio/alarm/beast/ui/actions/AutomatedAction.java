package org.csstudio.alarm.beast.ui.actions;

import java.io.IOException;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.AbstractAACommunicator;
import org.csstudio.alarm.beast.ui.AlarmTreeActionIcon;
import org.csstudio.alarm.beast.ui.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/** Automated actions executed for notifications.
 *  @author Sopra Group
 */
public class AutomatedAction extends Action
{
	final private static String CONSOLE_NAME = "Alarm Actions"; //$NON-NLS-1$
    final private Shell shell;
    final private AlarmTreeItem item;
    final private AADataStructure auto_action;
    
	private class AutomatedActionSender extends AbstractAACommunicator 
	{
		public AutomatedActionSender(String root_name) {
			super(root_name);
		}

		@Override
		protected void handleError(Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(shell,
					Messages.AutoActionError,
					NLS.bind(Messages.AutoActionErrorFmt, new Object[] {
							auto_action, "-", ex.getMessage() }));
		}

		@Override
		protected void handleNotifierResponse(String txt) {
			final MessageConsoleStream console_out = getConsole().newMessageStream();
			console_out.println(getText() + ": (" + item + ") '" + auto_action + "'");
			try {
				console_out.close();
			} catch (IOException e) {
				// Ignored
			}
		}
	}
	
	
    /** Initialize
     *  @param shell Shell to use for displayed dialog
     *  @param tree_position Origin of this command in alarm tree
     *  @param auto_action Automated action description
     */
    public AutomatedAction(final Shell shell, 
            final AlarmTreeItem tree_item,
            final AADataStructure auto_action)
    {
		this.shell = shell;
		this.item = tree_item;
		this.auto_action = auto_action;
		setText(auto_action.getTeaser());
		setImageDescriptor(AlarmTreeActionIcon.createIcon("icons/command.gif", //$NON-NLS-1$
				tree_item.getPosition()));
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public void run()
    {
		AutomatedActionSender aas = new AutomatedActionSender("");
		try {
			aas.start();
			Thread.sleep(1000);
			aas.sendAutomatedAction(item, auto_action);
			aas.stop();
		} catch (InterruptedException e) {
		}
    }
    
    /** Get a console in the Eclipse Console View for dumping the output
     *  of invoked alarm actions.
     *  <p>
     *  Code based on
     *  http://wiki.eclipse.org/FAQ_How_do_I_write_to_the_console_from_a_plug-in%3F
     *
     *  @return MessageConsole, newly created or one that already existed.
     */
    private MessageConsole getConsole()
    {
		final ConsolePlugin plugin = ConsolePlugin.getDefault();
		final IConsoleManager manager = plugin.getConsoleManager();
		final IConsole[] consoles = manager.getConsoles();
		for (int i = 0; i < consoles.length; i++)
			if (CONSOLE_NAME.equals(consoles[i].getName()))
				return (MessageConsole) consoles[i];
		// no console found, so create a new one
		final MessageConsole myConsole = new MessageConsole(CONSOLE_NAME, this.getImageDescriptor());
		// There is no default console buffer limit in chars or lines?
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=46871
		// 2k char limit, keep 1k
		myConsole.setWaterMarks(1024, 2048);
		manager.addConsoles(new IConsole[] { myConsole });
		return myConsole;
    }
    
}
