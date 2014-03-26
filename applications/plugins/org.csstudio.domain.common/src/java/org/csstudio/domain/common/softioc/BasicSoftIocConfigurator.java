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
package org.csstudio.domain.common.softioc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.FileLocator;


/**
 * Basic configurator for the Soft IOC. 
 * 
 * @author bknerr
 * @since 27.05.2011
 */
public class BasicSoftIocConfigurator extends AbstractSoftIocConfigurator {

    /**
     * Constructor.
     * 
     * @throws URISyntaxException 
     * @throws IOException 
     */
    public BasicSoftIocConfigurator() throws URISyntaxException, IOException {
        // TODO (bknerr) : find out how to resolve resources both with AND without the eclipse framework without polluting this code with logic
        // about whether the framework is present or not
        super(new File(FileLocator.toFileURL(BasicSoftIocConfigurator.class.getClassLoader().getResource("win/demo.exe")).toURI()),
              new File(FileLocator.toFileURL(BasicSoftIocConfigurator.class.getClassLoader().getResource("st.cmd")).toURI()));
        //    new File(BasicSoftIocConfigurator.class.getClassLoader().getResource("st.cmd").toURI())); // <- works without eclipse framework
        // but throws IAE when used with framework, as getResource yields a URL/URI which is of scheme==bundleresource instead scheme==file                                                               
        
    }
    
}
