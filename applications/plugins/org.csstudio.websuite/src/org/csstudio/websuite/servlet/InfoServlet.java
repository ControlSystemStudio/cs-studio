
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
	public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        IPreferencesService pref = Platform.getPreferencesService();
        String hostName = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.HOST_NAME, "loalhost", null);
        String port = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.JETTY_PORT, "unknown", null);
        
        appAddress = hostName + ":" + port;
    }
    
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        this.createPage(request, response);
    }
    
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        this.createPage(request, response);
    }
    
    private void createPage(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
        
        StringBuilder page = null;
        
        String contextPath = request.getServletPath();
        this.log("Servlet path: " + contextPath);
        
        String varPath = this.getServletContext().getRealPath("/var");
        this.log("/webapp/var: " + varPath);

        page = new StringBuilder();
        page.append("<html>\n");
        page.append("<head>\n");
        page.append("<title>InfoServlet</title>\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/style/webviewer.css\">\n");
        page.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
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
        
        page.append("</table>\n");
        page.append("</body>\n</html>");
        
        response.getOutputStream().print(page.toString());
        response.getOutputStream().flush();
    }
    
    private void appendHeadline(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<td>Servlets</td>\n");
        page.append("</tr>\n");
    }

    private void appendInfoAlarmViewServlet(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/AlarmViewer\">http://" + appAddress + "/AlarmViewer" + "</a></td>\n");
        page.append("</tr>\n");

        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/AlarmViewer?cmd=list\">http://" + appAddress + "/AlarmViewer?cmd=list" + "</a></td>\n");
        page.append("</tr>\n");
        
        page.append("<tr>\n");
        page.append("<td class=\"info\">http://" + appAddress + "/AlarmViewer[?topics=Topic1[,Topic2,...]]</td>\n");
        page.append("</tr>\n");

        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/AlarmViewerXml\">http://" + appAddress + "/AlarmViewerXml" + "</a></td>\n");
        page.append("</tr>\n");

        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/AlarmViewerXml?cmd=list\">http://" + appAddress + "/AlarmViewerXml?cmd=list" + "</a></td>\n");
        page.append("</tr>\n");

        page.append("<tr>\n");
        page.append("<td class=\"info\">http://" + appAddress + "/AlarmViewerXml[?topics=Topic1[,Topic2,...]]</td>\n");
        page.append("</tr>\n");
    }
    
    private void appendInfoRecordViewer(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<td class=\"info\">http://" + appAddress + "/RecordViewer?name=&lt;PV name&gt;</td>\n");
        page.append("</tr>\n");
        
        page.append("<tr>\n");
        page.append("<td class=\"info\">http://" + appAddress + "/RecordViewerXml?name=&lt;PV name&gt;</td>\n");
        page.append("</tr>\n");
    }

    private void appendInfoChannelViewer(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<td class=\"info\">http://" + appAddress + "/ChannelViewer?channel=&lt;PV name&gt;</td>\n");
        page.append("</tr>\n");
    }

    private void appendInfoIocViewerServlet(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<td class=\"info\">http://" + appAddress + "/IocViewer?ioc=&lt;IOC name&gt;</td>\n");
        page.append("</tr>\n");
    }

    private void appendInfoHowToServlet(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<td class=\"info\">http://" + appAddress + "/HowToViewer?value=&lt;HowTo ID&gt;</td>\n");
        page.append("</tr>\n");
    }
    
    private void appendFlashInfoServlet(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/FlashInfo\">http://" + appAddress + "/FlashInfo" + "</a></td>\n");
        page.append("</tr>\n");
    }

    private void appendGenericRecordInfoServlet(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/PersonalPVInfo\">http://" + appAddress + "/PersonalPVInfo" + "</a></td>\n");
        page.append("</tr>\n");
    }

    private void appendHalle55Servlet(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/Halle55\">http://" + appAddress + "/Halle55" + "</a></td>\n");
        page.append("</tr>\n");
    }

    private void appendHalle55AsciiServlet(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/Halle55?ASCII\">http://" + appAddress + "/Halle55?ASCII" + "</a></td>\n");
        page.append("</tr>\n");
    }

    private void appendWetterServlet(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<td class=\"info\"><a href=\"http://" + appAddress + "/Wetter\">http://" + appAddress + "/Wetter" + "</a></td>\n");
        page.append("</tr>\n");
    }

    private void appendLineRow(StringBuilder page) {
        
        page.append("<tr>\n<td colspan=\"2\"><hr></td>\n</tr>\n");
    }
}
