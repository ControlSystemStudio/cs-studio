
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 
 * @since 14.10.2010
 */
public class PageContentContainer {
    
    private static PageContentContainer instance = null;
    
    /** Private logger for this class */
    private Logger logger;

    /** */
    private HashMap<String, PageContent> content;

    /** Path to the workspace folder */
    private String workspacePath;

    private String FILE_SEPARATOR;

    /**
     * 
     */
    private PageContentContainer() {
        
        logger = CentralLogger.getInstance().getLogger(this);
        content = new HashMap<String, PageContent>();
        
        FILE_SEPARATOR = System.getProperty("file.separator");
        
        IPath location = Platform.getLocation();
        workspacePath = location.toOSString();
        if(workspacePath.endsWith(FILE_SEPARATOR) == false) {
            
            workspacePath += FILE_SEPARATOR;
        }
        
        logger.info(workspacePath);

        loadContentFiles();
    }
    
    /**
     * 
     * @return
     */
    public static synchronized PageContentContainer getInstance() {
        
        if(instance == null) {
            instance = new PageContentContainer();
        }
        
        return instance;
    }
    
    /**
     * 
     * @param pc
     */
    public void add(PageContent pc) {
        
        if(content.containsValue(pc) == false) {
            content.put(pc.getPageContentName(), pc);
            storeContentFiles();
        }
    }
    
    /**
     * 
     * @param pageContent
     */
    public void replace(PageContent pageContent) {
    
        if(content.containsKey(pageContent.getPageContentName())) {
            
            content.remove(pageContent.getPageContentName());
            content.put(pageContent.getPageContentName(), pageContent);
            saveContentFile(pageContent.getPageContentName());
        }
    }
    
    /**
     * 
     * @param pageContent
     */
    public void remove(String key) {
    
        if(content.containsKey(key)) {
            
            content.remove(key);
            File file = new File(workspacePath + FILE_SEPARATOR + "content_" + key + ".properties");
            file.delete();
        }
    }

    /**
     * 
     * @param pageContent
     */
    public void remove(PageContent pageContent) {
        remove(pageContent.getPageContentName());
    }
    
    /**
     * 
     */
    private void loadContentFiles() {
        
        PageContent pc = null;
        File ws = new File(workspacePath);
        File[] fileList = ws.listFiles(new PageContentFileFilter());
        
        for(File f : fileList) {
            
            pc = new PageContent(f);
            content.put(pc.getPageContentName(), pc);
        }
    }
    
    /**
     * 
     * @param name
     */
    public void reloadContentFile(String name) {
    
        if(content.containsKey(name)) {
            
            content.remove(name);
            
            File file = new File(workspacePath + FILE_SEPARATOR + "content_" + name + ".properties");
            PageContent pc = new PageContent(file);
            content.put(pc.getPageContentName(), pc);
        }
    }
    
    /**
     * 
     */
    private void storeContentFiles() {
        
        PageContent pageContent = null;
        File file = null;
        
        Iterator<String> iter = content.keySet().iterator();
        while(iter.hasNext()) {
            
            pageContent = content.get(iter.next());
            file = new File(workspacePath + "/content_" + pageContent.getPageContentName() + ".properties");
            pageContent.storeContentFile(file);
        }
    }
    
    /**
     * 
     * @param name
     */
    public void saveContentFile(String name) {
        
        PageContent pageContent = null;
        File file = null;
        
        pageContent = content.get(name);
        file = new File(workspacePath + "/content_" + pageContent.getPageContentName() + ".properties");
        pageContent.storeContentFile(file);
    }
    
    /**
     * 
     * @return
     */
    public ArrayList<PageContent> getAllPageContents() {
    
        ArrayList<PageContent> result = new ArrayList<PageContent>(content.size());
        
        Set<String> keys = content.keySet();
        Iterator<String> iter = keys.iterator();
        
        String key = null;
        
        while(iter.hasNext()) {
            
            key = iter.next();
            result.add(content.get(key));
        }
        
        return result;
    }
    
    /**
     * 
     * @param name
     * @return
     */
    public PageContent getPageContent(String name) {
        return content.get(name);
    }
    
    /**
     * 
     * @return
     */
    public String getWorkspacePath() {
        return workspacePath + FILE_SEPARATOR;
    }
}
