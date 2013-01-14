
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.ams.systemmonitor.file;

import java.io.File;
import java.io.FilenameFilter;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 05.12.2011
 */
public class FileCleaner {
    
    private static final Logger LOG = LoggerFactory.getLogger(FileCleaner.class);
    
    private String workspaceLocation;
    
    public FileCleaner() {
        
        // Retrieve the location of the workspace directory
        try {
            workspaceLocation = Platform.getLocation().toPortableString();
            if(workspaceLocation.endsWith("/") == false) {
                workspaceLocation = workspaceLocation + "/";
            }
        } catch(IllegalStateException ise) {
            LOG.warn("Workspace location could not be found. Using working directory '.'");
            workspaceLocation = "./";
        }

    }
    
    public void deleteStatusHandlerFiles() {
        File workspace = new File(workspaceLocation);
        File[] handlerFiles = workspace.listFiles(new StatusHandlerList());
        for (File file : handlerFiles) {
            if (file.delete()) {
                LOG.info("File deleted: {}", file.getAbsolutePath());
            } else {
                LOG.warn("Cannot delete file: {}", file.getAbsolutePath());
            }
        }
    }
    
    class StatusHandlerList implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.contains(".ser");
        }
    }
}
