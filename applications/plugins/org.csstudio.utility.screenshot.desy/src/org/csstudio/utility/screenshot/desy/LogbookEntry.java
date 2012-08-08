
/* 
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.utility.screenshot.desy;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The class represents an entry in the logbook. It holds all available data for a specific
 * entry.
 * 
 *  @author Markus Moeller
 *
 */
public class LogbookEntry
{
    private Hashtable<String, String> content;
    private String logbook;
        
    public LogbookEntry() {
        init();
    }
    
    private void init() {
        content = new Hashtable<String, String>(PropertyNames.PROPERTYCOUNT);
    }
    
    private LogbookEntry(Hashtable<String, String> c) {
        this();
        Enumeration<String> en = c.keys();
        while(en.hasMoreElements()) {
            String name = en.nextElement();
            content.put(name, c.get(name));
        }
    }
    
    public String getLogbookProperty(String name) {
        String result = null;
        if(content.containsKey(name.toUpperCase())) {
            result = content.get(name);
        }
        return result;
    }
    
    public String setLogbookProperty(String name, String value) {
        if(content.containsKey(name)) {
            content.remove(name);
        }
        return content.put(name, value);
    }

    public void setLogbookName(String name) {
        logbook = name;
    }
    
    public String getLogbookName() {
        return logbook;
    }
    
    public LogbookEntry createNewInstanceFromContent() {
        return new LogbookEntry(content);
    }
    
    public String createXmlFromContent() {
        StringBuffer xml = new StringBuffer();
        String temp = null;
        xml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<ROWSET>\n <ROW num=\"1\">\n");
        String[] keyList = PropertyNames.getKeyList();
        for(int i = 0;i < PropertyNames.PROPERTYCOUNT;i++) {
            if(content.containsKey(keyList[i])) {
                temp = "  <" + keyList[i] + ">" + content.get(keyList[i]) + "</" + keyList[i] + ">\n";
                xml.append(temp);
            }
        }
        xml.append(" </ROW>\n</ROWSET>\n");
        return xml.toString();
    }
    
    public String createPropertiesList() {
        StringBuffer properties = new StringBuffer();
        String temp = null;
        if (logbook != null) {
            properties.append("LOGBOOKNAME=" + logbook + "\n");
        }
        String[] keyList = PropertyNames.getKeyList();
        for(int i = 0;i < PropertyNames.PROPERTYCOUNT;i++) {
            if(content.containsKey(keyList[i])) {
                temp = keyList[i] + "=" + content.get(keyList[i]) + "\n";
                properties.append(temp);
            }
        }
        return properties.toString();
    }

/*    
    <?xml version="1.0" encoding="ISO-8859-1"?>
    <ROWSET>
      <ROW num="1">
      <IDENTIFYER>eLog:040714-08:53:03</IDENTIFYER>
      <ACCOUNTNAME>Der Autor</ACCOUNTNAME>
      <LOGGROUP>MKS-2</LOGGROUP>
      <ENTRYDATE>2004-07-14 08:53:03</ENTRYDATE>
      <EVENTFROM>2004-07-14 08:53:03</EVENTFROM>
      <EVENTUNTIL>2004-07-14 08:53:03</EVENTUNTIL>
      <DESCSHORT>The Subject</DESCSHORT>
      <DESCLONG>The message body</DESCLONG>
      <MULTIMEDIAIDENTIFYER></MULTIMEDIAIDENTIFYER>
      <ERRORIDENTIFYER></ERRORIDENTIFYER>
      <PREVIOUSOPERATORLOG></PREVIOUSOPERATORLOG>
      <STOCKTRANSACTION></STOCKTRANSACTION>
      <HTMLLINK></HTMLLINK>
      <PROJECT></PROJECT>
      <DEVICE></DEVICE>
      <LOCATION></LOCATION>
      <KEYWORDS>Keywords</KEYWORDS>
      <SENDEMAILTO></SENDEMAILTO>
      <MAINTENANCE></MAINTENANCE>
      <LOGSEVERITY>INFO</LOGSEVERITY>
      </ROW>
    </ROWSET>
*/
}
