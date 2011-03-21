
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;

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

    /** The name of this page content */
    private String pageContentName;
    
    /** The content of the page */
    private ArrayList<PageEntry> content;
    
    /**
     * 
     */
    public PageContent() {
        
        logger = CentralLogger.getInstance().getLogger(this);
        content = new ArrayList<PageEntry>();
        pageContentName = null;
    }
    
    /**
     * 
     * @param name
     */
    public PageContent(String name) {
        
        this();
        pageContentName = name;
    }
    
    /**
     * This constructor loads the content of the PageContent object from a file.
     * 
     * @param file
     */
    public PageContent(File file) {
        
        this();
        content = loadContentFile(file);
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
    public String getPageContentName() {
        return this.pageContentName;
    }

    /**
     * 
     * @param entry
     */
    public void addPageEntry(PageEntry entry) {
        content.add(entry);
    }
    
    /**
     * 
     * @return
     */
    private ArrayList<PageEntry> loadContentFile(File file) {
        
        Properties p = new Properties();
        ArrayList<PageEntry> result = new ArrayList<PageEntry>();
        FileInputStream in = null;
        String pvName = null;
        String egu = null;
        String label = null;
        int index = 0;
        boolean moreElements = true;
        
//        System.out.println(file.getName());
//        System.out.println(file.getAbsolutePath());
        
        try {
            in = new FileInputStream(file.getPath());
            p.load(in);
            
            if(p.containsKey("name")) {
                pageContentName = p.getProperty("name", file.getName());
            } else {
                pageContentName = file.getName();
            }
            
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
        } finally {
            if(in!=null){try{in.close();}catch(Exception e){/* Can be ignored */}in=null;}
        }
        
        return result;
    }
    
    /**
     * 
     * @return
     */
    public boolean storeContentFile(File file) {
        
        Properties p = new Properties();
        FileOutputStream out = null;
        boolean success = false;
        
        p.setProperty("name", pageContentName);
        
        int n = 0;
        for(PageEntry pageEntry : content) {
            
            p.setProperty("pvName." + n, pageEntry.getPvName());
            
            if(pageEntry.containsEgu()) {
                p.setProperty("egu." + n, pageEntry.getEgu());
            }
            
            p.setProperty("label." + n, pageEntry.getLabel());
            
            n++;
        }
        
        try {
            out = new FileOutputStream(file.getPath());
            p.store(out, null);
            success = true;
        } catch(FileNotFoundException fnfe) {
            logger.error("[*** FileNotFoundException ***]: " + fnfe.getMessage());
        } catch(IOException ioe) {
            logger.error("[*** IOException ***]: " + ioe.getMessage());
        } finally {
            if(out!=null){try{out.close();}catch(Exception e){/* Can be ignored */}out=null;}
        }
        
        return success;
    }
}
