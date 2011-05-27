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

import java.io.File;

import javax.annotation.Nonnull;

/**
 * TODO (bknerr) : 
 * 
 * @author bknerr
 * @since 27.05.2011
 */
public class SoftIocConfigurator {

    /**
     * Constructor.
     */
    public SoftIocConfigurator() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @return
     */
    @Nonnull
    public String getDemoExecutableFilePath() {
        return "d:\\development\\repo\\cs-studio\\applications\\plugins\\org.csstudio.domain.desy.softioc\\res\\win\\demo.exe";
//        return "d:\\development\\repo\\cs-studio\\applications\\plugins\\org.csstudio.domain.desy.softioc\\res\\old\\bin\\demo.exe";
    }



    /**
     * @return
     */
    @Nonnull
    public String getSoftIocCmdFileName() {
        return "st.cmd";
    }

    @Nonnull
    public File getSoftIocCmdFilePath() {
        return new File("d:\\development\\repo\\cs-studio\\applications\\plugins\\org.csstudio.domain.desy.softioc\\res\\");
//        return new File("d:\\development\\repo\\cs-studio\\applications\\plugins\\org.csstudio.domain.desy.softioc\\res\\old\\iocBoot\\iocdemo\\");
    }
    
}
