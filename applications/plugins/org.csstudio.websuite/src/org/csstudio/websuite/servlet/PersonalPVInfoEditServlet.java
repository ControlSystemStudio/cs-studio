
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.websuite.WebSuiteActivator;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.csstudio.websuite.utils.EditAction;
import org.csstudio.websuite.utils.PageContent;
import org.csstudio.websuite.utils.PageContentContainer;
import org.csstudio.websuite.utils.PageEntry;
import org.csstudio.websuite.utils.RequestParameter;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 
 * @since 29.10.2010
 */
public class PersonalPVInfoEditServlet extends HttpServlet {

    /** Generated serial version id */
    private static final long serialVersionUID = -7565507781142748381L;
    
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
        RequestParameter param = new RequestParameter(request);
        EditAction action = EditAction.ACTION_INVALID;
        
        PageContent pageContent = null;
        if(param.containsParameter("content") && param.hasParameterAnyValue("content")) {
                
            pageContent = pageContentContainer.getPageContent(param.getParameter("content"));
            if(pageContent == null) {
                pageContent = createPageContent(param);
                pageContentContainer.add(pageContent);
            }
                // java.net.URLEncoder.encode(s, "ISO-8859-1");
        }
        
        if(param.hasParameterAnyValue("action")) {
            action = EditAction.getByName(param.getParameter("action"));
        }
        
        if(pageContent == null) {
            
              if(action != EditAction.ACTION_NEW) {
                
                String url = "http://" + hostName + ":" + port + "/PersonalPVInfo";
                response.sendRedirect(url);
            }
        }
        
        if(action == EditAction.ACTION_CANCEL) {
            
            pageContentContainer.reloadContentFile(pageContent.getPageContentName());
            String url = "http://" + hostName + ":" + port + "/PersonalPVInfoList?content=" + pageContent.getPageContentName();
            response.sendRedirect(url);
        }

        if(action == EditAction.ACTION_ADD) {
            
            pageContent = addPageEntry(param, pageContent);
        }
        
        if(action == EditAction.ACTION_OK) {
            
            pageContent = createPageContent(param);
            pageContentContainer.replace(pageContent);
            pageContent = pageContentContainer.getPageContent(pageContent.getPageContentName());
            String url = "http://" + hostName + ":" + port + "/PersonalPVInfoList?content=" + pageContent.getPageContentName();
            response.sendRedirect(url);
            
            return;
        }

        if(action == EditAction.ACTION_DELETE) {
            
            pageContentContainer.remove(pageContent.getPageContentName());
            String url = "http://" + hostName + ":" + port + "/PersonalPVInfo";
            response.sendRedirect(url);
        }
        
        logger.info("User-Agent: " + request.getHeader("User-Agent"));
        
        page.append("<html>\n");
        page.append("<head>\n");
        page.append("<title>Personal PV Info Edit</title>\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/style/flashinfo.css\">\n");
        page.append("<meta http-equiv=\"Pragma\" content=\"no-cache\">\n");
                
        page.append("</head>\n");
        page.append("<body>\n");
                
        page.append("<table class=\"caption\"\">\n");
                
        page.append("<tr><td class=\"caption_thin\">&nbsp;</td></tr>\n");
        
        createEditPage(pageContent, page, action);
        
        page.append("</table>\n");        
        page.append("</body>\n</html>");
        
        response.getOutputStream().print(page.toString());
        response.getOutputStream().flush();
    }
    
    /**
     * 
     * @param page
     */
    private void createEditPage(PageContent pageContent, StringBuilder page, EditAction action) {
        
        page.append("<form action=\"http://" + hostName + ":" + port + "/PersonalPVInfoEdit\" method=\"get\">\n");
        page.append("<table class=\"main\"\">\n");
        
        if(action == EditAction.ACTION_NEW) {
            appendNewEntryRow(page, 0, true);
        } else {
            if((pageContent.getNumberOfEntries() > 0) || (pageContent.getPageContentName().length() > 0)) {
                this.appendEditRow(pageContent, page);
            } else {
                page.append("<tr>\n<td>No entry</td>\n</tr>\n");
            }
        }
        
        if(action != EditAction.ACTION_NEW) {
            appendNewEntryRow(page, pageContent.getNumberOfEntries(), false);
        }
        
        this.appendEditButtonRow(page);
        
        page.append("</table>\n");
        page.append("</form>\n");
    }

    /**
     * 
     * @param page
     */
    private void appendEditRow(PageContent pageContent, StringBuilder page) {
        
        ArrayList<PageEntry> list = pageContent.getContent();
        PageEntry pe = null;
        
        page.append("<tr>\n");
        page.append("<td class=\"main\">Konfiguration:</td>\n");
        page.append("<td class=\"main\">");

        if(pageContent != null) {
            page.append("<input class=\"editpage\" type=\"text\" name=\"content\" readonly value=\"" + pageContent.getPageContentName() + "\"\n");
        } else {
            page.append("<input class=\"editpage\" type=\"text\" name=\"content\" value=\"\" size=\"40\" maxlength=\"40\">");
        }
        
        page.append("</td>\n");
        page.append("</tr>\n");
        
        if(pageContent.getNumberOfEntries() > 0) {
            appendLineRow(page);
        }
        
        for(int i = 0;i < list.size();i++) {
            
            pe = list.get(i);
            
            // PV Name
            page.append("<tr>\n");
            page.append("<td class=\"main\">PV Name:</td>\n");
            page.append("<td class=\"main\">");
            page.append("<input class=\"editpage\" type=\"text\" name=\"pvName." + i + "\" value=\"" + pe.getPvName() + "\" size=\"40\" maxlength=\"40\">");
            page.append("</td>\n");
            page.append("</tr>\n");

            page.append("<tr>\n");
            page.append("<td class=\"main\">Label:</td>\n");
            page.append("<td class=\"main\">");
            page.append("<input class=\"editpage\" type=\"text\" name=\"label." + i + "\" value=\"" + pe.getLabel() + "\" size=\"40\" maxlength=\"40\">");
            page.append("</td>\n");
            page.append("</tr>\n");

            page.append("<tr>\n");
            page.append("<td class=\"main\">Remove:</td>\n");
            page.append("<td class=\"main\">");
            page.append("<input class=\"deletebox\" type=\"checkbox\" name=\"remove." + i + "\" value=\"Remove\">");
            page.append("</td>\n");
            page.append("</tr>\n");

            // A line
            if(i < (list.size() - 1)) {
                appendLineRow(page);
            }
        }
    }

    /**
     * 
     * @param page
     */
    private void appendNewEntryRow(StringBuilder page, int index, boolean withConfigInput) {
        
        if(withConfigInput) {
            page.append("<tr>\n");
            page.append("<td class=\"main\">Konfiguration:</td>\n");
            page.append("<td class=\"main\">");
    
            page.append("<input class=\"editpage\" type=\"text\" name=\"content\" value=\"\" size=\"40\" maxlength=\"40\">");
            
            page.append("</td>\n");
            page.append("</tr>\n");
        }

        appendLineRow(page);
        
        // PV Name
        page.append("<tr>\n");
        page.append("<td class=\"main\">PV Name:</td>\n");
        page.append("<td class=\"main\">");
        page.append("<input class=\"editpage\" type=\"text\" name=\"pvName." + index + "\" value=\"\" size=\"40\" maxlength=\"40\">");
        page.append("</td>\n");
        page.append("</tr>\n");

        appendEmptyRow(page);

        page.append("<tr>\n");
        page.append("<td class=\"main\">Label:</td>\n");
        page.append("<td class=\"main\">");
        page.append("<input class=\"editpage\" type=\"text\" name=\"label." + index + "\" value=\"\" size=\"40\" maxlength=\"40\">");
        page.append("</td>\n");
        page.append("</tr>\n");
    }

    /**
     * Creates the three buttons for the edit page.
     * 
     * @param page
     */
    private void appendEditButtonRow(StringBuilder page) {
    
        page.append("<table class=\"main\"\">\n");
        page.append("<tr>");
        
        page.append("<td class=\"main\" width=\"50%\" align=\"center\" valign=\"middle\">\n");
        page.append("<input class=\"button\" type=\"submit\" name=\"action\" value=\"Add\">\n");
        page.append("</td>\n");
                
        page.append("<td class=\"main\" width=\"50%\" align=\"center\" valign=\"middle\">\n");
        page.append("<input class=\"button\" type=\"submit\" name=\"action\" value=\"Delete\">\n");
        page.append("</td>\n");

        page.append("</tr>\n");

        page.append("<tr>\n");
        page.append("<td class=\"main\" colspan=\"2\"><hr></td>\n");
        page.append("</tr>\n");

        page.append("<tr>\n");

        page.append("<td class=\"main\" width=\"50%\" align=\"center\" valign=\"middle\">\n");
        page.append("<input class=\"button\" type=\"submit\" name=\"action\" value=\"OK\">\n");
        page.append("</td>\n");

        page.append("<td class=\"main\" width=\"50%\" align=\"center\" valign=\"middle\">\n");
        page.append("<input class=\"button\" type=\"submit\" name=\"action\" value=\"Cancel\">\n");
        page.append("</td>\n");
        
        page.append("</tr>\n");
        page.append("</table>\n");
    }

    /**
     * 
     * @param page
     */
    private void appendLineRow(StringBuilder page) {
        page.append("<tr>\n<td colspan=\"2\"><hr></td>\n</tr>\n");
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
     */
    @SuppressWarnings("unused")
    private void createErrorPage(StringBuilder page,  String errorMessage) {
        
        page.append("<tr>");
        
        page.append("<td class=\"main\" align=\"center\" valign=\"middle\">\n");
        page.append("<font color=\"red\">" + errorMessage + "</font>\n");
        page.append("</td>\n");
                
        page.append("</tr>\n");

    }
        
    /**
     * 
     * @param param
     * @param pageContent
     * @return
     */
    private PageContent addPageEntry(RequestParameter param, PageContent pageContent) {
        
        PageEntry pe = new PageEntry();
        String key = null;
        int count = pageContent.getNumberOfEntries();
        
        HashMap<String, String> entry = param.getParameterByKeyIndex(count);
        if(entry.isEmpty() == false) {
            
            Iterator<String> iter = entry.keySet().iterator();
            pe = new PageEntry();
            while(iter.hasNext()) {
                
                key = iter.next();
                if(key.startsWith("pvName")) {
                    pe.setPvName(entry.get(key));
                } else if(key.startsWith("label")) {
                    pe.setLabel(entry.get(key));
                }
            }
            
            if(pe.containsData()) {
                pageContent.addPageEntry(pe);
            }
        }
        
        return pageContent;
    }
    
    /**
     * Returns the PageContent object that contains the data of responsed form data.
     * 
     * @param param
     * @return
     */
    private PageContent createPageContent(RequestParameter param) {
        
        HashMap<String, String> pageEntry = null;
        PageContent pageContent = null;
        PageEntry pe = new PageEntry();
        String name = null;
        String key = null;
        
        if(param.containsParameter("content")) {
            
            name = param.getParameter("content");
            if(name.length() == 0) {
                
                // If there is no name, return
                return pageContent;
            }
        }
        
        pageContent = new PageContent(name);
        int n = 0;
        do {
            
            pageEntry = param.getParameterByKeyIndex(n);
            if(pageEntry.isEmpty() == false) {
                
                Set<String> keys = pageEntry.keySet();
                if(keys.contains("remove." + n) == false) {
                    
                    Iterator<String> iter = keys.iterator();
                    pe = new PageEntry();
                    while(iter.hasNext()) {
                        
                        key = iter.next();
                        if(key.startsWith("pvName")) {
                            pe.setPvName(pageEntry.get(key));
                        } else if(key.startsWith("label")) {
                            pe.setLabel(pageEntry.get(key));
                        }
                    }
                    
                    if(pe.containsData()) {
                        pageContent.addPageEntry(pe);
                    }
                }
                
                n++;
            }
            
        }while(pageEntry.isEmpty() == false);
        
        return pageContent;
    }
}
