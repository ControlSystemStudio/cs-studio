package org.csstudio.sns.mpsbypasses.ui;

import java.io.File;
import java.io.PrintStream;

import org.csstudio.apputil.ui.elog.ElogDialog;
import org.csstudio.apputil.ui.elog.SendToElogActionHelper;
import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.LogbookFactory;
import org.csstudio.sns.mpsbypasses.model.BypassModel;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Menu action to create elog entry
 *  @author Delphy Armstrong - Original MPSBypassGUI
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ELogAction extends SendToElogActionHelper
{
	final private Shell shell;
	final private BypassModel model;

	/** Initialize
	 *  @param shell
	 *  @param model
	 */
	public ELogAction(final Shell shell, final BypassModel model)
    {
		this.shell = shell;
		this.model = model;
    }

	@Override
    public void run()
    {
		try
		{
			final String message = "MPS Bypass Report";
			final String title = "MPS Bypass Report for Machine Mode '" + model.getMachineMode() + "'";
			final String body = title +"\nBypass State: " + model.getBypassFilter() +
				"\nRequest Status: " + model.getRequestFilter();

			final Dialog dialog = new ElogDialog(shell, message, title, body, null)
			{
				@Override
				// create and send an elog entry of the displayed bypass information
				public void makeElogEntry(final String logbook_name, final String user, final String password,
						final String title, final String body, final String images[]) throws Exception
				{
					// create the html page of bypass information
					final File report = File.createTempFile("BypassTable", ".html");
					final PrintStream out = new PrintStream(report);
					new HtmlReport(out, model).write();
					out.close();

				     // Connect to the Elog using the input uid and password
			        final ILogbook logbook = LogbookFactory.getInstance().connect(logbook_name, user, password);

			        // As files, submit the model's report plus maybe images that the user attached
			        final String files[] = new String[images.length + 1];
			        final String captions[] = new String[images.length + 1];
			        files[0] = report.getPath();
			        captions[0] = message;
			        for (int i=0; i<images.length; ++i)
			        {
			            files[i+1] = images[i];
			            captions[i+1] = "Image";
			        }
			        logbook.createEntry(title, body, files, captions);
			        logbook.close();

			        // Delete the report file
			        report.delete();
				}
			};
			dialog.open();
		}
		catch (Exception ex)
		{
			MessageDialog.openError(shell, "ELog Error",
				NLS.bind("Error creating ELog entry.\nException: {0}", ex.getMessage()));
		}
    }
}
