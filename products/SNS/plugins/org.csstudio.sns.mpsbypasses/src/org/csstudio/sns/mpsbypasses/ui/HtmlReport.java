package org.csstudio.sns.mpsbypasses.ui;

import java.io.PrintStream;

import org.csstudio.sns.mpsbypasses.model.Bypass;
import org.csstudio.sns.mpsbypasses.model.BypassModel;
import org.csstudio.sns.mpsbypasses.model.BypassState;
import org.csstudio.sns.mpsbypasses.model.Request;

/** Helper for creating HTML report for {@link BypassModel}
 *  @author Delphy Armstrong - Original BypassHTML
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class HtmlReport
{
	final private static String HTML_VERSION = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"http://www.w3.org/TR/html4/loose.dtd\">";

	final private PrintStream out;
	final private BypassModel model;

	public HtmlReport(final PrintStream out, final BypassModel model)
    {
	    this.out = out;
	    this.model = model;
    }

	public void write()
    {
		out.println(HTML_VERSION);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>MPS Bypass Report</title>");
		out.println("</head>");

		out.println("<body>");

		tag("h1", "MPS Bypass Report");

		tag("h2", "Bypass Counts");
		out.println("<table border='1'>");
		printTableHeader("Bypass State", "Count");
		printTableRow(BypassState.Bypassed.toString(), Integer.toString(model.getBypassed()));
		printTableRow(BypassState.Bypassable.toString(), Integer.toString(model.getBypassable()));
		printTableRow(BypassState.NotBypassable.toString(), Integer.toString(model.getNotBypassable()));
		printTableRow(BypassState.Disconnected.toString(), Integer.toString(model.getDisconnected()));
		printTableRow(BypassState.InError.toString(), Integer.toString(model.getInError()));
		printTableRow(BypassState.All.toString(), Integer.toString(model.getTotal()));
		out.println("</table>");

		tag("h2", "Bypass Listing");
		tag("p", "Machine Mode: <em>" + model.getMachineMode() + "</em>");
		tag("p", "Bypass State: <em>" + model.getBypassFilter() + "</em>");
		tag("p", "Request Type: <em>" + model.getRequestFilter() + "</em>");

		final Bypass[] bypasses = model.getBypasses();
		if (bypasses.length <= 0)
			tag("blockquote", "No bypasses for that machine mode, bypass state and request type");
		else
		{
			out.println("<table border='1'>");
			printTableHeader("#", "Bypass", "State", "Requestor", "Request Date");
			for (int i=0; i<bypasses.length; ++i)
			{
				final Bypass bypass = bypasses[i];
				final Request request = bypass.getRequest();
				final String requestor = request == null ? "" : request.getRequestor();
				final String date = request == null ? "" : request.getDate().toString();
				printTableRow(Integer.toString(i+1),
						bypass.getName(),
						bypass.getState().toString(),
						requestor, date);
			}
			out.println("</table>");
		}

		out.println("</body>");
		out.println("</html>");
    }

	/** Append tagged html content
	 *  @param tag
	 *  @param content
	 */
	private void tag(final String tag, final String content)
	{
		out.append("<").append(tag).append(">");
		out.append(content);
		out.append("</").append(tag).append(">");
	}

	private void printTableHeader(final String... columns)
    {
		out.print("  <tr>");
		for (String column : columns)
			tag("th", column);
		out.println(" </tr>");
    }

	private void printTableRow(final String... columns)
    {
		out.print("  <tr>");
		for (String column : columns)
			tag("td", column);
		out.println(" </tr>");
    }
}
