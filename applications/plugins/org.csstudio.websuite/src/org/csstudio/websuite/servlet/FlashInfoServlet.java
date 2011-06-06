
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

import org.csstudio.websuite.utils.Severity;
import org.csstudio.websuite.utils.ValueReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (Markus Moeller) : 
 * 
 * @author Markus Moeller
 * @version 
 * @since 25.06.2010
 */
public class FlashInfoServlet extends HttpServlet {
    
    /** Generated serial version id */
    private static final long serialVersionUID = -8317323822124937701L;
    
    /** Class that reads the value from the control system */
    private ValueReader valueReader;
    
    /** TODO: Replace it by a preference value */
    private final int RELOAD_TIME = 30;
    
    /** Private logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger(FlashInfoServlet.class);
    /**
     * 
     */
    @Override
	public void init(ServletConfig config) throws ServletException {
        
        super.init(config);
        valueReader = new ValueReader();
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
        
        StringBuilder page = null;
        
        LOG.info("User-Agent: {}", request.getHeader("User-Agent"));
        
        page = new StringBuilder();
        page.append("<html>\n");
        page.append("<head>\n");
        page.append("<title>FLASH Info</title>\n");
        page.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/style/flashinfo.css\">\n");
        page.append("<meta http-equiv=\"Pragma\" content=\"no-cache\">\n");
        page.append("<meta http-equiv=\"Refresh\" content=\"" + RELOAD_TIME + "\">\n");
        page.append("</head>\n");
        page.append("<body>\n");
        
        page.append("<div class=\"reloadtime\">Reload every " + RELOAD_TIME + " seconds.</div><p>\n");

        page.append("<table class=\"caption\"\">\n");
        page.append("<tr><th class=\"caption\">FLASH Info</th></tr>\n");
        page.append("<tr><td class=\"caption_thin\">&nbsp;</td></tr>\n");
        page.append("</table>\n");

        page.append("<table class=\"main\"\">\n");

        this.appendEndcap1Info(page);
        this.appendLineRow(page);
        this.appendEndcap2Info(page);

        page.append("</table>\n");
        page.append("</body>\n</html>");
        
        response.getOutputStream().print(page.toString());
        response.getOutputStream().flush();
    }
    
    private void appendEndcap1Info(StringBuilder page) {
        
        String className = null;
        
        page.append("<tr>\n");
        page.append("<th colspan=\"2\" class=\"main\">ENDCAP 1</th>\n");
        page.append("</tr>\n");

        page.append("<tr>\n");
        page.append("<td colspan=\"2\" class=\"main_thin\">&nbsp;</td>\n");
        page.append("</tr>\n");

        page.append("<tr>\n");
        page.append("<td class=\"main\"></td>\n");
        page.append("<td class=\"main_bold\">Level (%)</td>\n");
        page.append("</tr>\n");
        
        Severity severity = valueReader.getSeverity("TMTSL1R44_ai");
        if(severity == null) {
            className = "invalid";
        } else {
            className = severity.getClassName();
        }
        
        page.append("<tr>\n");
        page.append("<td class=\"main_image\"><img class=\"" + className + "\" src=\"/images/null.gif\"></td>\n");
        page.append("<td class=\"main\">" + valueReader.getValueAsString("TMTSL1R44_ai") + "</td>\n");
        page.append("</tr>\n");
        
        page.append("<tr>\n");
        page.append("<td colspan=\"2\" class=\"main_thin\">&nbsp;</td>\n");
        page.append("</tr>\n");

        page.append("<tr>\n");
        page.append("<td class=\"main_image\"></td>\n");
        page.append("<td class=\"main_bold\">Druck (mbar)</td>\n");
        page.append("</tr>\n");

        severity = valueReader.getSeverity("TMTSP1R44_ai");
        if(severity == null) {
            className = "invalid";
        } else {
            className = severity.getClassName();
        }

        page.append("<tr>\n");
        page.append("<td class=\"main_image\"><img class=\"" + className + "\" src=\"/images/null.gif\"></td>\n");
        page.append("<td class=\"main\">" + valueReader.getValueAsString("TMTSP1R44_ai") + "</td>\n");
        page.append("</tr>\n");
    }
    
    private void appendEndcap2Info(StringBuilder page) {
        
        String className = null;

        page.append("<tr>\n");
        page.append("<th colspan=\"2\" class=\"main\">ENDCAP 2</th>\n");
        page.append("</tr>\n");
        
        page.append("<tr>\n");
        page.append("<td colspan=\"2\" class=\"main_thin\">&nbsp;</td>\n");
        page.append("</tr>\n");

        page.append("<tr>\n");
        page.append("<td class=\"main\"></td>\n");
        page.append("<td class=\"main_bold\">Level (%)</td>\n");
        page.append("</tr>\n");

        Severity severity = valueReader.getSeverity("TMTSL1R26_ai");
        if(severity == null) {
            className = "invalid";
        } else {
            className = severity.getClassName();
        }        

        page.append("<tr>\n");
        page.append("<td class=\"main_image\"><img class=\"" + className + "\" src=\"/images/null.gif\"></td>\n");
        page.append("<td class=\"main\">" + valueReader.getValueAsString("TMTSL1R26_ai") + "</td>\n");
        page.append("</tr>\n");
        
        page.append("<tr>\n");
        page.append("<td colspan=\"2\" class=\"main_thin\">&nbsp;</td>\n");
        page.append("</tr>\n");

        page.append("<tr>\n");
        page.append("<td class=\"main\"></td>\n");
        page.append("<td class=\"main_bold\">Druck (mbar)</td>\n");
        page.append("</tr>\n");

        severity = valueReader.getSeverity("TMTSP1R26_ai");
        if(severity == null) {
            className = "invalid";
        } else {
            className = severity.getClassName();
        }        

        page.append("<tr>\n");
        page.append("<td class=\"main_image\"><img class=\"" + className + "\" src=\"/images/null.gif\"></td>\n");
        page.append("<td class=\"main\">" + valueReader.getValueAsString("TMTSP1R26_ai") + "</td>\n");
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
