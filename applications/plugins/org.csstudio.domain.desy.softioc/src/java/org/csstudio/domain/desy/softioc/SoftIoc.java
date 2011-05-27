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
 */
package org.csstudio.domain.desy.softioc;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Wraps access via Runtime to the configurable Soft IOC. 
 * 
import java.io.*;
 
public class Main {
 
       public static void main(String args[]) {
 
            try {
                Runtime rt = Runtime.getRuntime();
                //Process pr = rt.exec("cmd /c dir");
                Process pr = rt.exec("c:\\helloworld.exe");
 
                BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
 
                String line=null;
 
                while((line=input.readLine()) != null) {
                    System.out.println(line);
                }
 
                int exitVal = pr.waitFor();
                System.out.println("Exited with error code "+exitVal);
 
            } catch(Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
        }
} 
 * 
 * @author bknerr
 * @since 27.05.2011
 */
public class SoftIoc {
    
    private final ISoftIocConfigurator _cfg;
    private Process _process;
    
    /**
     * Constructor.
     */
    public SoftIoc() {
        this(new BasicSoftIocConfigurator());
    }
    
    /**
     * Constructor.
     */
    public SoftIoc(@Nonnull final ISoftIocConfigurator cfg) {
        _cfg = cfg;
        _process = null;
    }
    
    public void start() throws IOException {
        ProcessBuilder builder = new ProcessBuilder().command(_cfg.getDemoExecutableFilePath(), _cfg.getSoftIocCmdFileName())
                                                     .directory(_cfg.getSoftIocCmdFilePath());
        
        _process = builder.start();
    }
    
    public void stop() throws IOException {
        if (_process != null) {
            _process.destroy();
        }
    }
}
