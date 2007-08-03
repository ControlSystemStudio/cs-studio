
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

public class LogbookEntry
{
    private Hashtable<String, String>   content = null;
    private String[]                    keyList = null;
    private String                      logbook = null;
    
    public static final String PROPERTY_IDENTIFYER             = "IDENTIFYER";
    public static final String PROPERTY_ACCOUNTNAME            = "ACCOUNTNAME";
    public static final String PROPERTY_LOGGROUP               = "LOGGROUP";
    public static final String PROPERTY_ENTRYDATE              = "ENTRYDATE";
    public static final String PROPERTY_EVENTFROM              = "EVENTFROM";
    public static final String PROPERTY_EVENTUNTIL             = "EVENTUNTIL";
    public static final String PROPERTY_TITLE                  = "DESCSHORT";
    public static final String PROPERTY_TEXT                   = "DESCLONG";
    public static final String PROPERTY_MULTIMEDIAIDENTIFYER   = "MULTIMEDIAIDENTIFYER";
    public static final String PROPERTY_ERRORIDENTIFYER        = "ERRORIDENTIFYER";
    public static final String PROPERTY_PREVIOUSOPERATORLOG    = "PREVIOUSOPERATORLOG";
    public static final String PROPERTY_STOCKTRANSACTION       = "STOCKTRANSACTION";
    public static final String PROPERTY_HTMLLINK               = "HTMLLINK";
    public static final String PROPERTY_PROJECT                = "PROJECT";
    public static final String PROPERTY_DEVICE                 = "DEVICE";
    public static final String PROPERTY_LOCATION               = "LOCATION";
    public static final String PROPERTY_KEYWORDS               = "KEYWORDS";
    public static final String PROPERTY_SENDEMAILTO            = "SENDEMAILTO";
    public static final String PROPERTY_MAINTENANCE            = "MAINTENANCE";
    public static final String PROPERTY_LOGSEVERITY            = "LOGSEVERITY";
    
    private final int PROPERTYCOUNT = 20;
    
    public LogbookEntry()
    {
        init();
    }
    
    private void init()
    {
        content = null;
        
        content = new Hashtable<String, String>(20);
        
        keyList = new String[20];
        
        keyList[0] = PROPERTY_IDENTIFYER;
        keyList[1] = PROPERTY_ACCOUNTNAME;
        keyList[2] = PROPERTY_LOGGROUP;
        keyList[3] = PROPERTY_ENTRYDATE;
        keyList[4] = PROPERTY_EVENTFROM;
        keyList[5] = PROPERTY_EVENTUNTIL;
        keyList[6] = PROPERTY_TITLE;
        keyList[7] = PROPERTY_TEXT;
        keyList[8] = PROPERTY_MULTIMEDIAIDENTIFYER;
        keyList[9] = PROPERTY_ERRORIDENTIFYER;
        keyList[10] = PROPERTY_PREVIOUSOPERATORLOG;
        keyList[11] = PROPERTY_STOCKTRANSACTION;
        keyList[12] = PROPERTY_HTMLLINK;
        keyList[13] = PROPERTY_PROJECT;
        keyList[14] = PROPERTY_DEVICE;
        keyList[15] = PROPERTY_LOCATION;
        keyList[16] = PROPERTY_KEYWORDS;
        keyList[17] = PROPERTY_SENDEMAILTO;
        keyList[18] = PROPERTY_MAINTENANCE;
        keyList[19] = PROPERTY_LOGSEVERITY;
    }
    
    private LogbookEntry(Hashtable<String, String> c)
    {
        String name = null;
        
        init();
        
        Enumeration<String> en = c.keys();
        
        while(en.hasMoreElements())
        {
            name = en.nextElement();
            
            content.put(name, c.get(name));
        }
    }
    
    public String getLogbookProperty(String name)
    {
        String result = null;
        
        if(content.containsKey(name.toUpperCase()))
        {
            result = content.get(name);
        }
            
        return result;
    }
    
    public String setLogbookProperty(String name, String value)
    {
        if(content.containsKey(name))
        {
            content.remove(name);
        }
        
        return content.put(name, value);
    }

    public void setLogbookName(String name)
    {
        logbook = name;
    }
    
    public String getLogbookName()
    {
        return logbook;
    }
    
    public LogbookEntry createNewInstanceFromContent()
    {
        return new LogbookEntry(content);
    }
    
    public String createXmlFromContent()
    {
        StringBuffer    xml     = new StringBuffer();
        String          temp    = null;
        
        xml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<ROWSET>\n <ROW num=\"1\">\n");
        
        for(int i = 0;i < PROPERTYCOUNT;i++)
        {
            if(content.containsKey(keyList[i]))
            {
                temp = "  <" + keyList[i] + ">" + content.get(keyList[i]) + "</" + keyList[i] + ">\n";
                xml.append(temp);
            }
        }

        xml.append(" </ROW>\n</ROWSET>\n");
        
        return xml.toString();
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
