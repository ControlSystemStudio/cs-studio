
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.websuite.WebSuiteActivator;
import org.csstudio.websuite.dao.DatabaseHandler;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.csstudio.websuite.utils.HowToBlockingList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * @author Markus Moeller
 *
 */
public class HowToViewServletHtml extends HttpServlet
{
    /** Generated serial version id */
    private static final long serialVersionUID = -8359344448026481735L;

    /** */
    private DatabaseHandler dbHandler;
    
    /** */
    private HowToBlockingList blockingList;
    
    /**
     * 
     */
    @Override
	public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        
        IPreferencesService pref = Platform.getPreferencesService();
        
        String url = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.DATABASE_URL, "", null);
        String user = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.DATABASE_USER, "", null);
        String password = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.DATABASE_PASSWORD, "", null);
        
        try
        {
            dbHandler = new DatabaseHandler(url, user, password);
        }
        catch(SQLException sqle)
        {
            dbHandler = null;
            log("[*** SQLException ***]: " + sqle.getMessage());
        }
        
        String block = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.HOWTO_BLOCKING_LIST, "", null);
        blockingList = new HowToBlockingList(block);
    }
    
    /**
     * 
     */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.createPage(request, response);
    }
    
    /**
     * 
     */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.createPage(request, response);
    }

    /**
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void createPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        StringBuilder page = null;
        String value = null;
        
        page = new StringBuilder();
        page.append("<html>\n");
        page.append("<head>\n");
        page.append("<title>HowTo Viewer</title>\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/style/webviewer.css\">");
        page.append("<meta http-equiv=\"Pragma\" content=\"no-cache\">\n");
        page.append("</head>\n");
        page.append("<body>\n");
                
        page.append("<table class=\"howto\">\n");
        
        // Get number of the HowTo entry that we have to show
        // HowTo?ACTION=VIEW&VALUE=<nr>
        value = request.getParameter("VALUE");
        if(value == null)
        {
            value = request.getParameter("value");
        }
        
        if(value == null)
        {
            page.append("<tr>\n");
            page.append("<td><font color=\"#ff0000\"><b>ERROR:</b> The URL does not contain the HowTo number.</font></td>\n");
            page.append("</tr>\n");
        }
        else if(value.length() == 0)
        {
            page.append("<tr>\n");
            page.append("<td><font color=\"#ff0000\"><b>ERROR:</b> The URL does not contain the HowTo number.</font></td>\n");
            page.append("</tr>\n");
        }
        else
        {
            this.appendCaption(page, value);
            this.appendSeperator(page);
            this.appendHowToText(page, value);
        }

        page.append("</table>\n");
        page.append("</body>\n</html>");

        response.getOutputStream().print(page.toString());
        response.getOutputStream().flush();
    }

    /**
     * 
     * @param page
     * @param value
     */
    private void appendCaption(StringBuilder page, String value)
    {
        page.append("<tr>\n");
        page.append("<th align=\"center\" valign=\"middle\">HowTo No. " + value + "</th>\n");
        page.append("</tr>\n");
    }

    private void appendSeperator(StringBuilder page)
    {
        page.append("<tr>\n");
        page.append("<th><hr></th>\n");
        page.append("</tr>\n");
    }

    /**
     * 
     * @param page
     * @param value
     */
    private void appendHowToText(StringBuilder page, String value)
    {
        String text = null;
        
        if(dbHandler == null)
        {
            page.append("<tr>\n");
            page.append("<td><font color=\"#ff0000\"><b>ERROR:</b> Cannot connect to the database.</font></td>\n");
            page.append("</tr>\n");
            
            return;
        }
        
        if(blockingList.blockEntry(value))
        {
            page.append("<tr>\n");
            page.append("<td align=\"center\"><font color=\"#ff0000\">The entry " + value + " is blocked.</font></td>\n");
            page.append("</tr>\n");
            
            return;
        }
        
        try
        {
            text = dbHandler.getHowToEntryText(value);
            
            page.append("<tr>\n");
            page.append("<td class=\"howto\">\n" + text + "\n</td>\n");
            page.append("</tr>\n");
        }
        catch(SQLException sqle)
        {
            page.append("<tr>\n");
            page.append("<td><font color=\"#ff0000\"><b>ERROR:</b> [*** SQLException ***]: " + sqle.getMessage() + "</font></td>\n");
            page.append("</tr>\n");
        }
    }
}
