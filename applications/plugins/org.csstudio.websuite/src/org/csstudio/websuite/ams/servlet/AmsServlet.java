
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

package org.csstudio.websuite.ams.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.websuite.WebSuiteActivator;
import org.csstudio.websuite.ams.AmsConfigurationService;
import org.csstudio.websuite.ams.AmsDatabaseProperty;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * @author mmoeller
 * @version 1.0
 * @since 06.08.2012
 */
public class AmsServlet extends HttpServlet {
    
    private static final long serialVersionUID = 6350847993882589956L;
    
    private AmsDatabaseProperty amsDbProp;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        IPreferencesService prefs = Platform.getPreferencesService();
        String dbType = prefs.getString(WebSuiteActivator.PLUGIN_ID,
                                       PreferenceConstants.AMS_DATABASE_TYPE,
                                       "", null);
        String dbUrl = prefs.getString(WebSuiteActivator.PLUGIN_ID,
                                       PreferenceConstants.AMS_DATABASE_URL,
                                       "", null);
        String dbUser = prefs.getString(WebSuiteActivator.PLUGIN_ID,
                                        PreferenceConstants.AMS_DATABASE_USER,
                                        "", null);
        String dbPassword = prefs.getString(WebSuiteActivator.PLUGIN_ID,
                                        PreferenceConstants.AMS_DATABASE_PASSWORD,
                                        "", null);
        amsDbProp = new AmsDatabaseProperty(Enum.valueOf(DatabaseType.class, dbType),
                                            dbUrl,
                                            dbUser,
                                            dbPassword);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/plain");
        
        AmsConfigurationService ams = new AmsConfigurationService(WebSuiteActivator.getBundleContext(),
                                                                  amsDbProp);
        Configuration config = ams.getConfiguration();
        ams.close();
        
        Document xmlDoc = new Document();
        Element root = new Element("ams");

        Element elUsers = new Element("users");
        Collection<AlarmbearbeiterDTO> users = config.gibAlleAlarmbearbeiter();
        Iterator<AlarmbearbeiterDTO> iter = users.iterator();
        while (iter.hasNext()) {
            Element item  = new Element("item");
            AlarmbearbeiterDTO o = iter.next();
            item.setAttribute("name", o.getUserName());
            item.setAttribute("id", String.valueOf(o.getUserId()));
            elUsers.addContent(item);
        }
        
        Element elGroups = new Element("groups");
        Collection<AlarmbearbeiterGruppenDTO> groups = config.gibAlleAlarmbearbeiterGruppen();
        Iterator<AlarmbearbeiterGruppenDTO> groupIter = groups.iterator();
        while (groupIter.hasNext()) {
            Element item  = new Element("item");
            AlarmbearbeiterGruppenDTO o = groupIter.next();
            item.setAttribute("name", o.getUserGroupName());
            item.setAttribute("id", String.valueOf(o.getUserGroupId()));
            elGroups.addContent(item);
        }
        
        root.addContent(elUsers);
        root.addContent(elGroups);
        xmlDoc.setRootElement(root);

        XMLOutputter xmlOut = new XMLOutputter();
        xmlOut.output(xmlDoc, response.getWriter());
    }
}
