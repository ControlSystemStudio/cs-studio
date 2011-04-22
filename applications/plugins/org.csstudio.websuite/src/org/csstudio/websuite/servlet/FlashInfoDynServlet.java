
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
import org.csstudio.websuite.utils.PageEntry;
import org.csstudio.websuite.utils.Severity;
import org.csstudio.websuite.utils.ValueReader;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 
 * @since 30.06.2010
 */
public class FlashInfoDynServlet extends HttpServlet {
    
    /** Generated serial version id */
    private static final long serialVersionUID = 118082356835958719L;

    /** Class that reads the value from the control system */
    private ValueReader valueReader;
    
    /** Content helper that stores selected PV's, etc. */
    private PageContent pageContent;
    
    /** Hostname of the web application */
    private String hostName;
    
    /** Port of the web application */
    private int port;

    /** TODO: Replace it by a preference value */
    private final int RELOAD_TIME = 30;
    
    /** Private logger for this class */
    private Logger logger;

    /**
     * 
     */
    @Override
	public void init(ServletConfig config) throws ServletException {
        
        super.init(config);
        
        logger = CentralLogger.getInstance().getLogger(this);
        valueReader = new ValueReader();
        pageContent = new PageContent();
        
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
        boolean edit = (request.getParameter("edit") != null);
        
        logger.info("User-Agent: " + request.getHeader("User-Agent"));
        
        page.append("<html>\n");
        page.append("<head>\n");
        page.append("<title>FLASH Info</title>\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/style/flashinfo.css\">\n");
        page.append("<meta http-equiv=\"Pragma\" content=\"no-cache\">\n");
        
        if(edit == false) {
            page.append("<meta http-equiv=\"Refresh\" content=\"" + RELOAD_TIME + "\">\n");
        }
        
        page.append("</head>\n");
        page.append("<body>\n");
        
        if(edit == false) {
            page.append("<div class=\"reloadtime\">Reload every " + RELOAD_TIME + " seconds.</div><p>\n");
        }
        
        page.append("<table class=\"caption\"\">\n");
        page.append("<tr><th class=\"caption\">FLASH Info</th></tr>\n");
        page.append("<tr><td class=\"caption_thin\">&nbsp;</td></tr>\n");
        page.append("</table>\n");

        if(edit == false) {
            createNormalPage(page);
        } else {
            createEditPage(page);
        }
        
        page.append("</body>\n</html>");
        
        response.getOutputStream().print(page.toString());
        response.getOutputStream().flush();
    }
    
    /**
     * 
     * @param page
     */
    private void createNormalPage(StringBuilder page) {
        
        page.append("<table class=\"main\"\">\n");

        if(pageContent.getNumberOfEntries() > 0) {
            this.appendEntryRow(page);
        } else {
            page.append("<tr>\n<td>No entry</td>\n</tr>\n");
        }
        
        this.appendEditButton(page);
        
        page.append("</table>\n");
    }
    
    /**
     * 
     * @param page
     */
    private void createEditPage(StringBuilder page) {
        
        page.append("<form action=\"\">\n");
        page.append("<table class=\"main\"\">\n");

        if(pageContent.getNumberOfEntries() > 0) {
            this.appendEditRow(page);
        } else {
            page.append("<tr>\n<td>No entry</td>\n</tr>\n");
        }
        
        // this.appendEditButton(page);
        
        page.append("</table>\n");
        page.append("</form>\n");
    }

    /**
     * 
     * @param page
     */
    private void appendEditRow(StringBuilder page) {
        
        ArrayList<PageEntry> list = pageContent.getContent();
        PageEntry pe = null;
        
        for(int i = 0;i < list.size();i++) {
            
            pe = list.get(i);
            
            // PV Name
            page.append("<tr>\n");
            page.append("<td class=\"main\">PV Name:</td>\n");
            page.append("<td class=\"main\">\n");
            page.append("<input class=\"editpage\" type=\"text\" name=\"pvName." + i + "\" value=\"" + pe.getPvName() + "\" size=\"40\" maxlength=\"40\">");
            page.append("</td>\n");
            page.append("</tr>\n");

            page.append("<tr>\n");
            page.append("<td class=\"main\">Level:</td>\n");
            page.append("<td class=\"main\">\n");
            page.append("<input class=\"editpage\" type=\"text\" name=\"label." + i + "\" value=\"" + pe.getLabel() + "\" size=\"40\" maxlength=\"40\">");
            page.append("</td>\n");
            page.append("</tr>\n");

            page.append("<tr>\n");
            page.append("<td class=\"main\">Delete:</td>\n");
            page.append("<td class=\"main\">\n");
            page.append("<input class=\"deletebox\" type=\"checkbox\" name=\"delete." + i + "\" value=\"Delete\">");
            page.append("</td>\n");
            page.append("</tr>\n");

            // A line
            appendLineRow(page);
        }

    }

    /**
     * 
     * @param page
     */
    private void appendEditButton(StringBuilder page) {
        
        page.append("<tr>\n");
        page.append("<form action=\"http://" + hostName + ":" + port + "/FlashInfoDyn\" method=\"get\">\n");
        page.append("<td colspan=\"2\" align=\"center\" valign=\"middle\" class=\"main\">\n");
        page.append("<input class=\"button\"type=\"submit\" value=\"Edit\">");
        page.append("<input type=\"hidden\" name=\"edit\" value=\"true\">");
        page.append("</td>\n");
        page.append("</form>\n");
        page.append("<tr>\n");
    }
    
    /**
     * 
     * @param page
     */
    private void appendEntryRow(StringBuilder page) {
        
        ArrayList<PageEntry> list = pageContent.getContent();
        PageEntry pe = null;
        String className = null;
        
        for(int i = 0;i < list.size();i++) {
            
            pe = list.get(i);
            
            // PV Name
            page.append("<tr>\n");
            page.append("<th colspan=\"2\" class=\"main\">" + pe.getPvName() + "</th>\n");
            page.append("</tr>\n");
            
            appendEmptyRow(page);
            
            // Label and egu
            if(pe.containsEgu() == false) {
                
                String e = valueReader.getEgu(pe.getPvName());
                pe.setEgu(e);
            }
            
            page.append("<tr>\n");
            page.append("<td class=\"main\"></td>\n");
            page.append("<td class=\"main_bold\">" + pe.getLabel() + " (" + pe.getEgu() + ")</td>\n");
            page.append("</tr>\n");

            Severity severity = valueReader.getSeverity(pe.getPvName());
            if(severity == null) {
                className = "invalid";
            } else {
                className = severity.getClassName();
            }
            
            // Severity image and value
            page.append("<tr>\n");
            page.append("<td class=\"main_image\"><img class=\"" + className + "\" src=\"/images/null.gif\"></td>\n");
            page.append("<td class=\"main\">" + valueReader.getValueAsString(pe.getPvName()) + "</td>\n");
            page.append("</tr>\n");

            appendEmptyRow(page);
            
            // A line
            appendLineRow(page);
        }
    }
    
    /**
     * 
     * @param page
     */
    private void appendEmptyRow(StringBuilder page) {
        page.append("<tr>\n");
        page.append("<td colspan=\"2\" class=\"main_thin\">&nbsp;</td>\n");
        page.append("</tr>\n");
    }

    /**
     * 
     * @param page
     */
    private void appendLineRow(StringBuilder page) {
        page.append("<tr>\n<td colspan=\"2\"><hr></td>\n</tr>\n");
    }
}
