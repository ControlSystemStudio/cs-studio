
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.websuite.servlet;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.csstudio.websuite.WebSuiteActivator;
import org.csstudio.websuite.dao.DatabaseHandler;
import org.csstudio.websuite.dao.HowToEntry;
import org.csstudio.websuite.dao.HowToSearchWords;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.csstudio.websuite.utils.HowToBlockingList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * @author mmoeller
 * @version 1.0
 * @since 04.07.2012
 */
public class HowToSearchServlet extends HttpServlet {

    private static final long serialVersionUID = 9053027041680871187L;
    
    /** */
    private DatabaseHandler dbHandler;
    
    /** */
    private HowToBlockingList blockingList;

    @Override
    public void init(ServletConfig config) throws ServletException {
        
        super.init(config);
        
        IPreferencesService pref = Platform.getPreferencesService();
        
        String url = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.DATABASE_URL, "", null);
        String user = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.DATABASE_USER, "", null);
        String password = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.DATABASE_PASSWORD, "", null);
        
        try {
            dbHandler = new DatabaseHandler(url, user, password);
        } catch(SQLException sqle) {
            dbHandler = null;
            log("[*** SQLException ***]: " + sqle.getMessage());
        }
        
        String block = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.HOWTO_BLOCKING_LIST, "", null);
        blockingList = new HowToBlockingList(block);
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
        
        String value = request.getParameter("keywords");
        HowToSearchWords keywords = new HowToSearchWords(value);
        value = request.getParameter("search_mode");
        if (value == null) {
            value = "OR";
        }
        
        StringBuilder page = new StringBuilder();

        addPageHeader(page);
        addPageCaption(page, "HowTo Liste");

        page.append("<p><a href=\"./HowToSearch\">Neue Suche</a></p>");
        
        startTable(page);
        startTableRow(page);
        addTableHeadColumn(page, "HowTo Id");
        addTableHeadColumn(page, "Description");
        addTableHeadColumn(page, "Link");
        endTableRow(page);
        startTableRow(page);
        addTableDataColumn(page, 3, "<hr>");
        endTableRow(page);
        
        boolean orMode = value.contains("OR");
        HowToEntry[] entries = null;
        
        try {
            entries = dbHandler.searchHowToEntryText(keywords, orMode);
            addEntries(page, entries);
        } catch (SQLException sqle) {
            log("[*** SQLException ***]: " + sqle.getMessage());
        }
        
        endTable(page);
        page.append("<p><a href=\"./HowToSearch\">Neue Suche</a></p>");
        addPageFooter(page);
        
        response.getWriter().print(page.toString());
    }
    
    private void addEntries(StringBuilder page, HowToEntry[] entries) {
        
        for (HowToEntry o : entries) {
            startTableRow(page);
            addTableDataColumn(page, String.valueOf(o.getHowToId()));
            
            if (!blockingList.blockEntry(o.getHowToId())) {
                addTableDataColumn(page, o.getShortDescription());
                addTableDataColumn(page, "&nbsp;&nbsp;<a href=\"./HowToViewer?value="
                                         + String.valueOf(o.getHowToId())
                                         + "\" target=\"_blank\">Ansehen</a>");
            } else {
                addTableDataColumn(page, "<font color=\"red\"><b>BLOCKED</b></font>");
                addTableDataColumn(page, "&nbsp;");
            }
            
            endTableRow(page);
            
            startTableRow(page);
            addTableDataColumn(page, 3, "<hr>");
            endTableRow(page);
        }
    }
    
    private void addPageHeader(StringBuilder page) {
        page.append("<html>\n");
        page.append("<head>\n");
        page.append(" <title>HowTo Liste</title>\n");
        page.append(" <link rel=\"stylesheet\" type=\"text/css\" href=\"/style/webviewer.css\">\n");
        page.append(" <meta http-equiv=\"Pragma\" content=\"no-cache\">\n");
        page.append("</head>\n");
        page.append("<body>\n");
        page.append("<center>\n");
    }
    
    private void addPageFooter(StringBuilder page) {
        page.append("</center>\n</body>\n</html>\n");
    }
    
    private void addPageCaption(StringBuilder page, String caption) {
        page.append("<h1>" + caption + "</h1>\n");
    }
    
    private void startTable(StringBuilder page) {
        page.append("<table class=\"howto_list\">\n");
    }
    
    private void endTable(StringBuilder page) {
        page.append("</table>\n");

    }
    
    private void startTableRow(StringBuilder page) {
        page.append("<tr>\n");
    }
    
    private void endTableRow(StringBuilder page) {
        page.append("</tr>\n");
    }

    private void addTableDataColumn(StringBuilder page, String text) {
        page.append("<td class=\"howto_list\">" + text + "</td>\n");
    }
    
    private void addTableDataColumn(StringBuilder page, int colspan, String text) {
        page.append("<td class=\"howto_list\" colspan=\"" + colspan + "\">" + text + "</td>\n");
    }

    private void addTableHeadColumn(StringBuilder page, String text) {
        page.append("<th class=\"howto_list\">" + text + "</th>\n");
    }
}
