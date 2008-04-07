
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
 *
 */

package org.csstudio.alarm.jms2ora.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

public class Configuration
{
    private Properties properties = null;
    private String fileName = null;
    private String[] urlList = null;
    private String[] topicList = null;
    private String xmppUser = null;
    private String xmppPassword = null;
    private String oracleUser = null;
    private String oraclePassword = null;
    private String quotaRecords[] = null;
    
    public Configuration(String fn)
    {
        fileName = fn;
        
        readConfigFile();
    }
    
    private boolean readConfigFile()
    {
        StringTokenizer token = null;
        FileInputStream fis = null;
        File configFile = null;
        String delimiter = null;
        String hostLine = null;
        String topicLine = null;
        String recordLine = null;
        boolean result = true;

        properties = new Properties();

        configFile = new File(fileName);
        
        try
        {
            fis = new FileInputStream(configFile);
            properties.load(fis);            
        }
        catch(FileNotFoundException fnfe)
        {
            System.out.println(" *** FileNotFoundException *** : The configuration file " + fileName + " is not available.\n");
            
            result = false;
        }
        catch(IOException ioe)
        {
            System.out.println(" *** IOException *** : " + ioe.getMessage());
            
            result = false;
        }
        finally
        {
            if(fis != null)
            {
                try
                {
                    fis.close();
                }
                catch(IOException ioe) { }
            }
            
            configFile = null;
        }

        if(!result)
        {
            return result;
        }
        
        if(!properties.containsKey("delimiter"))
        {
            delimiter = ";";
        }
        else
        {
            delimiter = properties.getProperty("delimiter").trim();
        }
        
        // Get host names and topic names
        if(!properties.containsKey("hosts") || !properties.containsKey("topics"))
        {
            System.out.println(" ** ERROR ** : the property hosts and/or topics was not found in the configuration file.");

            return false;
        }

        hostLine = properties.getProperty("hosts").trim();
        topicLine = properties.getProperty("topics").trim();
        
        if(hostLine.length() == 0 || topicLine.length() == 0)
        {
            System.out.println(" ** ERROR ** : No hosts and/or topics were found in the configuration file.\n");
        
            return false;
        }
        
        token = new StringTokenizer(hostLine, delimiter);
        
        if(token.countTokens() == 0)
        {
            System.out.println(" ** ERROR ** : No hosts were found in the configuration file.\n");
            
            token = null;
            
            return false;
        }
        
        urlList = new String[token.countTokens()];
        
        int cnt = 0;
        while(token.hasMoreTokens())
        {
            urlList[cnt++] = token.nextToken().trim();
        }
        
        token = null;
        
        token = new StringTokenizer(topicLine, delimiter);
        
        if(token.countTokens() == 0)
        {
            System.out.println(" ** ERROR ** : No topics were found in the configuration file.\n");
            
            token = null;
            
            return false;
        }

        topicList = new String[token.countTokens()];
        
        cnt = 0;
        
        while(token.hasMoreTokens())
        {
            topicList[cnt++] = token.nextToken().trim();
        }
        
        token = null;
        
        // Get user name and password for XMPP
        if(!properties.containsKey("xmppuser") || !properties.containsKey("xmpppassword"))
        {
            System.out.println(" ** ERROR ** : No user name and/or password for the XMPP login were found in the configuration file.\n");
            
            return false;
        }
        
        xmppUser = properties.getProperty("xmppuser").trim();
        xmppPassword = properties.getProperty("xmpppassword").trim();

        // Get user name and password for ORACLE
        if(!properties.containsKey("oracleuser") || !properties.containsKey("oraclepassword"))
        {
            System.out.println(" ** ERROR ** : No user name and/or password for the ORACLE login were found in the configuration file.\n");
            
            return false;
        }
        
        oracleUser = properties.getProperty("oracleuser").trim();
        oraclePassword = properties.getProperty("oraclepassword").trim();

        // Get the list of record names for the database quota
        if(properties.containsKey("quotarecordname"))
        {
            token = null;
            
            recordLine = properties.getProperty("quotarecordname").trim();
            
            token = new StringTokenizer(recordLine, delimiter);
            
            if(token.countTokens() > 0)
            {
                quotaRecords = new String[token.countTokens()];
            
                cnt = 0;
            
                while(token.hasMoreTokens())
                {
                    quotaRecords[cnt++] = token.nextToken().trim();
                }    
            }
        }
        
        return result;
    }
    
    public String[] getUrlList()
    {
        return urlList;
    }
    
    public String[] getTopicList()
    {
        return topicList;
    }
    
    public String getXmppUser()
    {
        return xmppUser;
    }

    public String getXmppPassword()
    {
        return xmppPassword;
    }

    public String getOracleUser()
    {
        return oracleUser;
    }

    public String getOraclePassword()
    {
        return oraclePassword;
    }

    public String[] getQuotaRecords()
    {
        return quotaRecords;
    }

    public void setQuotaRecord(String[] quotaRecord)
    {
        this.quotaRecords = quotaRecord;
    }
}
