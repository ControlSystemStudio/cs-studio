
/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 */

package org.csstudio.websuite.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.websuite.WebSuiteActivator;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * @author Markus Moeller
 *
 */
public class InfoServlet extends HttpServlet {

    /** Generated serial verison id */
    private static final long serialVersionUID = -7186174383878007383L;

    /** Web address of this application*/
    private String appAddress;

    /**
     *
     */
    @Override
	public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        final IPreferencesService pref = Platform.getPreferencesService();
        final String hostName = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.HOST_NAME, "loalhost", null);
        final String port = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.JETTY_PORT, "unknown", null);

        appAddress = hostName + ":" + port;
    }

    @Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
    throws ServletException, IOException {
        this.createPage(request, response);
    }

    @Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
    throws ServletException, IOException {
        this.createPage(request, response);
    }

    private void createPage(final HttpServletRequest request, final HttpServletResponse response)
    throws IOException {

        StringBuilder page = null;

        final String contextPath = request.getServletPath();
        this.log("Servlet path: " + contextPath);

        final String varPath = this.getServletContext().getRealPath("/var");
        this.log("/webapp/var: " + varPath);

        page = new StringBuilder();
        page.append("<html>\n");
        page.append("<head>\n");
        page.append("<title>InfoServlet</title>\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/style/webviewer.css\">\n");
        page.append("<meta http-equiv=\"Content-Type\" content=\"text/html\">\n");
        page.append("</head>\n");
        page.append("<body>\n");

        page.append("<table class=\"info\">\n");

        this.appendHeadline(page);
        this.appendLineRow(page);
        this.appendInfoAlarmViewServlet(page);
        this.appendLineRow(page);
        this.appendInfoRecordViewer(page);
        this.appendLineRow(page);
        this.appendInfoChannelViewer(page);
        this.appendLineRow(page);
        this.appendInfoIocViewerServlet(page);
        this.appendLineRow(page);
        this.appendInfoHowToServlet(page);
        this.appendLineRow(page);
        this.appendFlashInfoServlet(page);
        this.appendLineRow(page);
        this.appendHalle55Servlet(page);
        this.appendHalle55AsciiServlet(page);
        this.appendLineRow(page);
        this.appendWetterServlet(page);
        this.appendLineRow(page);
        this.appendGenericRecordInfoServlet(page);
        this.appendLineRow(page);

        page.append("</table>\n");
        page.append("</body>\n</html>");

        response.getOutputStream().print(page.toString());
        response.getOutputStream().flush();
    }

    private void appendHeadline(final StringBuilder page) {
        page.append("<tr>\n");
        page.append("<th class=\"info\">Servlets</th>\n");
        page.append("<th class=\"info\">Linkart</th>\n");
        page.append("<th class=\"info\">Beschreibung</th>\n");
        page.append("</tr>\n");
    }

    private void appendInfoAlarmViewServlet(final StringBuilder page) {
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/html/AlarmViewer.html\">AlarmViewer</a></td>\n");
        page.append("<td class=\"info\">Doc</td>");
        page.append("<td class=\"info\">Anzeige der Alarmnachrichten (wie in der Alarmtabelle in CSS)</td>");
        page.append("</tr>\n");
    }

    private void appendInfoRecordViewer(final StringBuilder page) {
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/html/RecordViewer.html\">RecordViewer</a></td>\n");
        page.append("<td class=\"info\">Doc</td>");
        page.append("<td class=\"info\">Infos zu einem Record anzeigen (auch als XML)</td>");
        page.append("</tr>\n");
    }

    private void appendInfoChannelViewer(final StringBuilder page) {
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/html/ChannelViewer.html\">ChannelViewer</a></td>\n");
        page.append("<td class=\"info\">Doc</td>");
        page.append("<td class=\"info\">Infos zu einem Channel anzeigen (fuer die Alarmbenachrichtigung per SMS)</td>");
        page.append("</tr>\n");
    }

    private void appendInfoIocViewerServlet(final StringBuilder page) {
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/html/IocViewer.html\">IocViewer</a></td>\n");
        page.append("<td class=\"info\">Doc</td>");
        page.append("<td class=\"info\">Infos zu einem IOC anzeigen (fuer die Alarmbenachrichtigung per SMS)</td>");
        page.append("</tr>\n");
    }

    private void appendInfoHowToServlet(final StringBuilder page) {
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/html/HowToViewer.html\">HowToViewer</a></td>\n");
        page.append("<td class=\"info\">Doc</td>");
        page.append("<td class=\"info\">Die HowTo-Eintraege des eLogbooks anzeigen</td>");
        page.append("</tr>\n");
    }

    private void appendFlashInfoServlet(final StringBuilder page) {
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/FlashInfo\">FlashInfo</a></td>\n");
        page.append("<td class=\"info\">Direkt</td>");
        page.append("<td class=\"info\">Zwei Record-Eintraege mit Bezug zum FLASH</td>");
        page.append("</tr>\n");
    }

    private void appendGenericRecordInfoServlet(final StringBuilder page) {
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/PersonalPVInfo\">PersonalPVInfo</a></td>\n");
        page.append("<td class=\"info\">Direkt</td>");
        page.append("<td class=\"info\">Konfigurierbare Channel-Listen</td>");
        page.append("</tr>\n");
    }

    private void appendHalle55Servlet(final StringBuilder page) {
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/Halle55\">Halle55</a></td>\n");
        page.append("<td class=\"info\">Direkt</td>");
        page.append("<td class=\"info\">Daten des Experiments in der Halle 55</td>");
        page.append("</tr>\n");
    }

    private void appendHalle55AsciiServlet(final StringBuilder page) {
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/Halle55?ASCII\">Halle55 als ASCII</a></td>\n");
        page.append("<td class=\"info\">Direkt</td>");
        page.append("<td class=\"info\">Daten des Experiments in der Halle 55 als ASCII</td>");
        page.append("</tr>\n");
    }

    private void appendWetterServlet(final StringBuilder page) {
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/Wetter\">Wetterstation</a></td>\n");
        page.append("<td class=\"info\">Direkt</td>");
        page.append("<td class=\"info\">Daten der Wetterstation</td>");
        page.append("</tr>\n");
    }

    private void appendLineRow(final StringBuilder page) {
        page.append("<tr>\n<td colspan=\"3\"><hr></td>\n</tr>\n");
    }
}
