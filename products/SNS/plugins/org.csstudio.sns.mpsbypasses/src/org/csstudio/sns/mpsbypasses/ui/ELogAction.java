package org.csstudio.sns.mpsbypasses.ui;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;

import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.ui.LogEntryBuilderDialog;
import org.csstudio.sns.mpsbypasses.Plugin;
import org.csstudio.sns.mpsbypasses.model.BypassModel;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

/** Menu action to create elog entry
 *  @author Delphy Armstrong - Original MPSBypassGUI
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ELogAction extends Action
{
	final private Shell shell;
	final private BypassModel model;

	/** Initialize
	 *  @param shell
	 *  @param model
	 */
	public ELogAction(final Shell shell, final BypassModel model)
    {
	    super("Send to Logbook", Plugin.getImageDescription("icons/logentry.png"));
		this.shell = shell;
		this.model = model;
    }

	@Override
    public void run()
    {
		try
		{
			// Create the html page of bypass information
		    final ByteArrayBuffer html_buf = new ByteArrayBuffer();
			final PrintStream out = new PrintStream(html_buf);
			new HtmlReport(out, model).write();
			out.close();

			// Turn into attachment
			final AttachmentBuilder attachment = AttachmentBuilder
			        .attachment("BypassTable.html")
			        .contentType("html")
			        .inputStream(new ByteArrayInputStream(html_buf.getRawData()));
			
			// Create log entry
		    final LogEntryBuilder entry_builder = LogEntryBuilder
			    .withText("MPS Bypass Report" +
			    		  "\nMachine Mode '" + model.getMachineMode() + "'" +
			    		  "\nBypass State: " + model.getBypassFilter() +
                          "\nRequest Status: " + model.getRequestFilter() +
                          "\n\nSee attachment for report")
                .attach(attachment);
			final LogEntryBuilderDialog dialog = new LogEntryBuilderDialog(shell, entry_builder);
			dialog.open();
		}
		catch (Exception ex)
		{
		    ExceptionDetailsErrorDialog.openError(shell, "Error", ex);
		}
    }
}
