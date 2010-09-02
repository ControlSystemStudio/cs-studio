
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

package org.csstudio.websuite.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

/**
 * TODO (Markus Moeller) : 
 * 
 * @author Markus Moeller
 * @version 
 * @since 30.06.2010
 */
public class PageContent {
    
    /** Private logger for this class */
    private Logger logger;

    /** The content of the page */
    private ArrayList<PageEntry> content;
    
    /** Path to the workspace folder */
    private String workspacePath;
    
    private String FILE_SEPARATOR;

    /**
     * Standard constructor
     */
    public PageContent() {
        
        logger = CentralLogger.getInstance().getLogger(this);
        
        content = new ArrayList<PageEntry>();
        
        FILE_SEPARATOR = System.getProperty("file.separator");
        
        IPath location = Platform.getLocation();
        workspacePath = location.toOSString();
        if(workspacePath.endsWith(FILE_SEPARATOR) == false) {
            
            workspacePath += FILE_SEPARATOR;
        }
        
        logger.info(workspacePath);
        
        content = loadContentFile();
    }
    
    /**
     * Returns the number of elements.
     * 
     * @return
     */
    public int getNumberOfEntries() {
        return content.size();
    }
    
    /**
     * Returns the ArrayList object that contains the content of the page.
     * 
     * @return
     */
    public ArrayList<PageEntry> getContent() {
        return content;
    }
    
    /**
     * 
     * @return
     */
    private ArrayList<PageEntry> loadContentFile() {
        
        Properties p = new Properties();
        ArrayList<PageEntry> result = new ArrayList<PageEntry>();
        FileInputStream in = null;
        String pvName = null;
        String egu = null;
        String label = null;
        String fileName = workspacePath + "flashinfo.properties";
        int index = 0;
        boolean moreElements = true;
        
        try {
            in = new FileInputStream(fileName);
            p.load(in);
            
            do {
                if(p.containsKey("pvName." + index)
                   && p.containsKey("label." + index)) {
                    
                    pvName = p.getProperty("pvName." + index);
                    
                    if(p.containsKey("egu." + index)) {
                        egu = p.getProperty("egu." + index);
                    } else {
                        egu = "";
                    }
                    
                    label = p.getProperty("label." + index);
                    result.add(new PageEntry(pvName, egu, label));
                    
                    index++;
                }
                else {
                    moreElements = false;
                }
                    
            } while(moreElements);
        } catch (IOException ioe) {
            logger.error("[*** IOException ***]: " + ioe.getMessage());
        }
        finally {
            if(in!=null){try{in.close();}catch(Exception e){}in=null;}
        }
        
        return result;
    }
}
