
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
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.csstudio.websuite.epics.IocRequester;
import org.csstudio.websuite.utils.Facility;
import org.csstudio.websuite.utils.FacilityContainer;
import org.csstudio.websuite.utils.IocName;

/**
 * @author mmoeller
 * @version 1.0
 * @since 29.08.2012
 */
public class IocListServlet extends HttpServlet {

    private static final long serialVersionUID = 11115065481631948L;
    
    private IocRequester iocReq;
    
    private FacilityContainer facilities;
    
    public IocListServlet(String path) {
        super();
        facilities = new FacilityContainer(path);
        iocReq = new IocRequester();
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
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
        
        String facility = request.getParameter("facility");
        boolean showFacilityList = false;
        
        if (facility != null) {
            if (facility.trim().isEmpty()) {
                showFacilityList = true;
            }
        } else {
            showFacilityList = true;
        }
        
        if (showFacilityList) {
            createFacilityList(response);
        } else {
            createIocPage(response, facility);
        }
    }
    
    private void createFacilityList(HttpServletResponse response) 
            throws IOException {
        
        StringBuilder page = new StringBuilder();
        
        page.append("<html>\n");
        page.append("<head>\n");
        page.append("<title>Facility List</title>\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/style/webviewer.css\">");
        page.append("<meta http-equiv=\"Pragma\" content=\"no-cache\">\n");
        page.append("</head>\n");
        page.append("<body>\n");
        
        page.append("<table border=\"0\">\n");
        
        appendHeadLineRow(page);
        
        Facility[] allFacilities = facilities.getAllFacilities();
        for (Facility f : allFacilities) {
            this.appendFacilityNameAsLink(page, f.getFacilityName());
        }
        
        page.append("</table>\n");
        page.append("</body>\n</html>");

        response.getOutputStream().print(page.toString());
        response.getOutputStream().flush();

    }
    
    private void createIocPage(HttpServletResponse response, String facilityName)
            throws IOException {
        
        StringBuilder page = new StringBuilder();
        
        page.append("<html>\n");
        page.append("<head>\n");
        page.append("<title>Ioc List for " + facilityName + "</title>\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/style/webviewer.css\">");
        page.append("<meta http-equiv=\"Pragma\" content=\"no-cache\">\n");
        page.append("<meta http-equiv=\"Refresh\" content=\"31\">\n");
        page.append("</head>\n");
        page.append("<body>\n");
        
        page.append("<div class=\"reloadtime\">Reload every 31 seconds.</div><p>\n");
        this.appendRufbereitschaftRow(page);
        
        page.append("<table style=\"border-width:medium; border-style:double;\">\n");

        Facility facility = facilities.getFacility(facilityName);
        if (facility != null) {
            this.appendFacilityNameRow(page, facility.getFacilityName());
            this.appendLineRow(page);
            IocName[] allIocs = facility.getAllIocNames();
            for (IocName i : allIocs) {
                this.appendIocNameRow(page, i.getIocName());
            }
            this.appendLineRow(page);
        } else {
            this.appendErrorRow(page, "Facility '" + facilityName + "' is unknown!");
        }
        
        page.append("</table>\n");
        page.append("</body>\n</html>");

        response.getOutputStream().print(page.toString());
        response.getOutputStream().flush();
    }
    
    private void appendFacilityNameAsLink(StringBuilder page, String facility) {
        page.append("<tr>\n");
        page.append("<td colspan=\"2\" align=\"center\">");
        page.append("<a href=\"./IocList?facility=" + facility + "\">" + facility + "</a>");
        page.append("</td>\n");
        page.append("</tr>\n");
    }

    private void appendLineRow(StringBuilder page) {
        page.append("<tr>\n<td colspan=\"2\"><hr></td>\n</tr>\n");
    }

    private void appendRufbereitschaftRow(StringBuilder page) {
        page.append("<p>\n");
        page.append("<div class=\"rufbereitschaft\">Rufbereitschaft hat: ");
        page.append(iocReq.askOnCallDuty() + "</div><br>\n");
    }

    private void appendHeadLineRow(StringBuilder page) {
        page.append("<tr>\n<th colspan=\"2\">Choose a facility</th>\n</tr>\n");
        page.append("<tr>\n<td colspan=\"2\"></td>\n</tr>\n");
    }

    private void appendFacilityNameRow(StringBuilder page, String facility) {
        page.append("<tr>\n");
        page.append("<th colspan=\"2\" align=\"center\">" + facility + "</th>\n");
        page.append("</tr>\n");
    }
    
    private void appendIocNameRow(StringBuilder page, String iocName) {
        boolean stateA = iocReq.askFirstRedundantIoc(iocName);
        boolean stateB = iocReq.askSecondRedundantIoc(iocName);
        if (stateA && stateB) {
            this.appendIOCRedundantRow(page, iocName, stateA, stateB);
        } else {
            this.appendIOCAliveRow(page, iocName);
        }
    }
    
    private void appendIOCRedundantRow(StringBuilder page, String iocName, boolean stateA, boolean stateB) {
        page.append("<tr>\n");
        page.append("<td align=\"left\"><b>" + iocName + "</b></td>\n");
        page.append("<td align=\"center\">");
        if (stateA) {
            page.append("IOC A&nbsp;&nbsp;<img class=\"ledon\" src=\"/images/null.gif\">&nbsp;&nbsp;&nbsp;&nbsp;");
        } else {
            page.append("IOC A&nbsp;&nbsp;<img class=\"noioc\" src=\"/images/null.gif\">&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        if (stateB) {
            page.append("IOC B&nbsp;&nbsp;<img class=\"redundant\" src=\"/images/null.gif\">");
        } else {
            page.append("IOC B&nbsp;&nbsp;<img class=\"noioc\" src=\"/images/null.gif\">");
        }
        page.append("</td>\n");
        page.append("</tr>\n");
    }

    private void appendIOCAliveRow(StringBuilder page, String iocName) {
        page.append("<tr>\n");
        page.append("<td align=\"left\"><b>" + iocName + "</b></td>\n");
        if(iocReq.isIocAlive(iocName)) {
            page.append("<td align=\"center\"><img class=\"ledon\" src=\"/images/null.gif\"></td>\n");
        } else {
            page.append("<td align=\"center\"><img class=\"noioc\" src=\"/images/null.gif\"></td>\n");
        }
        page.append("</tr>\n");
    }
    
    private void appendErrorRow(StringBuilder page, String text) {
        page.append("<tr>\n");
        page.append("<td colspan=\"2\">");
        page.append("<font color=\"#ff0000\"><b>ERROR:</b> " + text + "</font></td>\n");
        page.append("</tr>\n");
    }
}
