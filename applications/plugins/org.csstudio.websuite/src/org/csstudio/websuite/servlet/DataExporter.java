
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

/**
 * This class is related to the servlet Halle55. It is responsible for exporting the data to a text file.
 * 
 * @author mmoeller
 * @version 
 * @since 26.08.2010
 */
public class DataExporter extends HttpServlet {
    
    /** Generated serial version id */
    private static final long serialVersionUID = 5913929027350370052L;

    /** Private logger for this class */
    private Logger logger;

    /** Path to the workspace folder */
    private String workspacePath;
    
    private String FILE_SEPARATOR;

    @Override
	public void init(ServletConfig config) throws ServletException {
        
        super.init(config);
        
        logger = CentralLogger.getInstance().getLogger(this);
        
        FILE_SEPARATOR = System.getProperty("file.separator");

        IPath location = Platform.getLocation();
        workspacePath = location.toOSString();
        if(workspacePath.endsWith(FILE_SEPARATOR) == false) {
            
            workspacePath += FILE_SEPARATOR;
        }
    }
    
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String data = "";
        byte[] buffer = new byte[512];
        
        FileInputStream dataFile = null;
        try {
            
            dataFile = new FileInputStream(workspacePath + "data.txt");
            while(dataFile.read(buffer) != -1) {
                
                data += new String(buffer).trim();
                Arrays.fill(buffer, (byte)0);
            }

        } catch(FileNotFoundException fnfe) {
            logger.warn("Cannot write to the data file: " + fnfe.getMessage());
        } catch(IOException ioe) {
            logger.warn("Cannot write to the data file: " + ioe.getMessage());
        } finally {
            if(dataFile != null) {
                try{dataFile.close();}catch(Exception e) {
                	// Can be ignored
                }
                dataFile = null;
            }
        }

        response.setContentType("text/plain");
        response.getOutputStream().write(data.getBytes());
    }
}
