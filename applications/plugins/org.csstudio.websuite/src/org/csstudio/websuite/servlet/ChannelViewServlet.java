
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 */

package org.csstudio.websuite.servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;

/**
 * @author Markus Moeller
 *
 */
public class ChannelViewServlet extends HttpServlet {
    
    /** Generated serial version id */
    private static final long serialVersionUID = -3099724878599407478L;
    
    /** */
    private IProcessVariableConnectionService service;
    
    /** */
    private ProcessVariableAdressFactory pvFactory;

    private boolean iPhoneRequest;
    
    @Override
	public void init(ServletConfig config) throws ServletException {
        
        super.init(config);
        
        // get a service instance (all applications using the same shared instance will share channels, too)
        service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
        
        // get a factory for process variable addresses 
        pvFactory = ProcessVariableAdressFactory.getInstance();
        
        iPhoneRequest = false;
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
        String channelName = null;
        
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            this.iPhoneRequest = userAgent.contains("iPhone");
        }

        page = new StringBuilder();
        page.append("<html>\n");
        page.append("<head>\n");
        page.append("<title>ChannelViewer</title>\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/style/webviewer.css\">");
        page.append("<meta http-equiv=\"Pragma\" content=\"no-cache\">\n");
        page.append("<meta http-equiv=\"Refresh\" content=\"31\">\n");
        page.append("</head>\n");
        page.append("<body>\n");
        
        page.append("<div class=\"reloadtime\">Reload every 31 seconds.</div><p>\n");        
        
        appendRufbereitschaftRow(page);
        
        page.append("<table style=\"border-width:medium; border-style:double\">\n");
        
        channelName = request.getParameter("channel");
        if(channelName == null)
        {
            page.append("<tr>\n");
            page.append("<td colspan=\"2\"><font color=\"#ff0000\"><b>ERROR:</b> The URL does not contain the channel name.</font></td>\n");
            page.append("</tr>\n");
        }
        else if(channelName.length() == 0)
        {
            page.append("<tr>\n");
            page.append("<td colspan=\"2\"><font color=\"#ff0000\"><b>ERROR:</b> The URL does not contain the channel name.</font></td>\n");
            page.append("</tr>\n");
        }
        else
        {
            this.appendChannelNameRow(page, channelName);
            this.appendLineRow(page);
            this.appendChannelValueRow(page, channelName);
            this.appendChannelDescriptionRow(page, channelName);
            this.appendChannelStatusRow(page, channelName);
            this.appendChannelSeverityRow(page, channelName);
            this.appendButtonRow(request, channelName, page);
        }
        
        page.append("</table>\n");
        page.append("</body>\n</html>");
        
        response.getOutputStream().print(page.toString());
        response.getOutputStream().flush();
    }
    
    private void appendRufbereitschaftRow(StringBuilder page)
    {
        IProcessVariableAddress pv = null;
        String value;
        
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://Rufbereitschaft");

        page.append("<p>\n");
        page.append("<div class=\"rufbereitschaft\">Rufbereitschaft hat: ");
        
        try
        {
            value = service.readValueSynchronously(pv, ValueType.STRING);
        }
        catch(ConnectionException ce)
        {
            value = "Not available";
        }
        
        page.append(value + "</div><br>\n");
    }

    private void appendChannelNameRow(StringBuilder page, String channelName)
    {
        page.append("<tr>\n");
        
        if(iPhoneRequest) {
            // page.append("&nbsp;&nbsp;<a href=\"desyarchiver://" + pe.getPvName().replaceAll("\\:", "\\\\:") + "\" target=\"_blank\">iPhone-Plot</a>\n");
            page.append("<th colspan=\"2\" align=\"center\">" + channelName + "<br>\n");
            page.append("<a href=\"desyarchiver://" + channelName + "\" target=\"_blank\">iPhone-Plot</a>\n");
            page.append("</th>\n");
        } else {
            page.append("<th colspan=\"2\" align=\"center\">" + channelName + "</th>\n");
        }
        
        page.append("</tr>\n");
    }

    private void appendChannelValueRow(StringBuilder page, String channelName)
    {
        IProcessVariableAddress pv = null;
        String value = null;
        
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + channelName);

        page.append("<tr>\n");

        // Read value synchronously
        try
        {
            page.append("<td><b>Value:&nbsp;</b></td>\n");

            value = service.readValueSynchronously(pv, ValueType.STRING);
            if(value == null)
            {
                page.append("<td><font color=\"#ff0000\">null</font></td>\n");
            }
            else
            {
                page.append("<td>" + value + "</td>\n");
            }
        }
        catch(ConnectionException ce)
        {
            page.append("<td><font color=\"#ff0000\"><b>ERROR</b></font></td>\n");
        }

        page.append("</tr>\n");
    }
    
    private void appendChannelDescriptionRow(StringBuilder page, String channelName)
    {
        IProcessVariableAddress pv = null;
        String value = null;
        
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + channelName + ".DESC");

        page.append("<tr>\n");

        // Read value synchronously
        try
        {
            page.append("<td><b>Description:&nbsp;</b></td>\n");

            value = service.readValueSynchronously(pv, ValueType.STRING);
            if(value == null)
            {
                page.append("<td><font color=\"#ff0000\">null</font></td>\n");
            }
            else
            {
                page.append("<td>" + value + "</td>\n");
            }
        }
        catch(ConnectionException ce)
        {
            page.append("<td><font color=\"#ff0000\"><b>ERROR</b></font></td>\n");
        }
        
        page.append("</tr>\n");
    }

    private void appendChannelStatusRow(StringBuilder page, String channelName)
    {
        IProcessVariableAddress pv = null;
        String value = null;
        
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + channelName + ".STAT");

        page.append("<tr>\n");

        // Read value synchronously
        try
        {
            page.append("<td><b>Status:&nbsp;</b></td>\n");

            value = service.readValueSynchronously(pv, ValueType.STRING);
            if(value == null)
            {
                page.append("<td><font color=\"#ff0000\">null</font></td>\n");
            }
            else
            {
                page.append("<td>" + value + "</td>\n");
            }
        }
        catch(ConnectionException ce)
        {
            page.append("<td><font color=\"#ff0000\"><b>ERROR</b></font></td>\n");
        }
        
        page.append("</tr>\n");
    }

    private void appendChannelSeverityRow(StringBuilder page, String channelName)
    {
        IProcessVariableAddress pv = null;
        String value = null;
        
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + channelName + ".SEVR");

        page.append("<tr>\n");

        // Read value synchronously
        try
        {
            page.append("<td><b>Severity:&nbsp;</b></td>\n");

            value = service.readValueSynchronously(pv, ValueType.STRING);
            if(value == null)
            {
                page.append("<td><font color=\"#ff0000\">null</font></td>\n");
            }
            else
            {
                page.append("<td>" + value + "</td>\n");
            }
        }
        catch(ConnectionException ce)
        {
            page.append("<td><font color=\"#ff0000\"><b>ERROR</b></font></td>\n");
        }
        
        page.append("</tr>\n");
    }
    
    private void appendLineRow(StringBuilder page)
    {
        page.append("<tr>\n<td colspan=\"2\"><hr></td>\n</tr>\n");
    }
    
    private void appendButtonRow(HttpServletRequest request, String channelName, StringBuilder page)
    {
        appendLineRow(page);
        page.append("<tr>\n");
        page.append("<form action=\"" + request.getRequestURL().toString() + "?channel=" + channelName + "\" method=\"post\">\n");
        page.append("<td colspan=\"2\" align=\"center\" valign=\"middle\"><input class=\"button\" type=\"submit\" value=\"Reload\"></td>\n");
        page.append("</form>\n");
        page.append("</tr>\n");
    }
}
