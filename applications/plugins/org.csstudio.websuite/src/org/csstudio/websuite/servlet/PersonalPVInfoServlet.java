
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
import java.util.ArrayList;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.websuite.WebSuiteActivator;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.csstudio.websuite.utils.PageContent;
import org.csstudio.websuite.utils.PageContentContainer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * This is the main servlet. Always it will be called first. It shows the list of available configurations.
 * The user can choose the configuration of interest.
 * 
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 
 * @since 30.06.2010
 */
public class PersonalPVInfoServlet extends HttpServlet {
    
    /** Generated serial version id */
    private static final long serialVersionUID = 118082356835958719L;

    /** Content helper that stores selected PV's, etc. */
    private PageContentContainer pageContentContainer;
    
    /** Hostname of the web application */
    private String hostName;
    
    /** Port of the web application */
    private int port;

    /** Private logger for this class */
    private Logger logger;

    /**
     * 
     */
    @Override
	public void init(ServletConfig config) throws ServletException {
        
        super.init(config);
        
        logger = CentralLogger.getInstance().getLogger(this);
        pageContentContainer = PageContentContainer.getInstance();
        
        IPreferencesService pref = Platform.getPreferencesService();
        hostName = pref.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.HOST_NAME, "loalhost", null);
        port = pref.getInt(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.JETTY_PORT, 8080, null);
    }
    
    /**
     * 
     */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        this.createPage(request, response);
    }
    
    /**
     * 
     */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        this.createPage(request, response);
    }

    /**
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void createPage(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        StringBuilder page = new StringBuilder();
        
        logger.info("User-Agent: " + request.getHeader("User-Agent"));
        
        page.append("<html>\n");
        page.append("<head>\n");
        page.append("<title>Personal PV Info</title>\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/style/flashinfo.css\">\n");
        page.append("<meta http-equiv=\"Pragma\" content=\"no-cache\">\n");
                
        page.append("</head>\n");
        page.append("<body>\n");
                
        page.append("<table class=\"caption\"\">\n");
        page.append("<tr><th class=\"caption\">List of Configurations</th></tr>\n");
        
        page.append("<tr><td class=\"caption_thin\">&nbsp;</td></tr>\n");
        page.append("</table>\n");
        
        createListPage(request, page);
        
        page.append("</body>\n</html>");
        
        response.getOutputStream().print(page.toString());
        response.getOutputStream().flush();
    }
        
    /**
     * 
     * @param page
     */
    private void createListPage(HttpServletRequest request, StringBuilder page) {
        
        page.append("<table class=\"main\"\">\n");
        
        ArrayList<PageContent> list = pageContentContainer.getAllPageContents();
//        String url = request.getRequestURL().toString();
//        if(url.endsWith("/")) {
//            url = url.substring(0, url.length() - 1);
//        }
        String url = "http://" + hostName + ":" + port + "/PersonalPVInfoList";
        
        for(PageContent o : list) {
            
            page.append("<tr>\n<td class=\"main\">");
            page.append("<a href=\"" + url + "?content=" + o.getPageContentName() + "\">");
            page.append(o.getPageContentName());
            page.append("</a>");
            page.append("</td>\n</tr>\n");
            page.append("<tr><td>&nbsp;</td>\n</tr>\n");
        }
        
        if(list.size() > 0) {
            appendLineRow(page);
        }
        
        appendNewButton(page);
        
        page.append("</table>\n");
    }
    
    /**
     * 
     * @param page
     */
    private void appendNewButton(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<form action=\"http://" + hostName + ":" + port + "/PersonalPVInfoEdit\" method=\"get\">\n");
        page.append("<td align=\"center\" valign=\"middle\" class=\"main\">\n");
        page.append("<input class=\"button\" type=\"submit\" value=\"New\">");
        page.append("<input type=\"hidden\" name=\"action\" value=\"new\">");
        page.append("</td>\n");
        page.append("</form>\n");
        page.append("<tr>\n");
    }
    
    /**
     * 
     * @param page
     */
    private void appendLineRow(StringBuilder page) {
        page.append("<tr>\n<td><hr></td>\n</tr>\n");
    }
}
