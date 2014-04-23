/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz Association, (DESY), HAMBURG,
 * GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER
 * ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN
 * ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS
 * NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING
 * FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.common.softioc;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;

/**
 * Abstract configurator for Soft IOC setup.
 *
 * @author bknerr
 * @since 27.05.2011
 */
public abstract class AbstractSoftIocConfigurator  implements ISoftIocConfigurator {

    private final File _exeFilePath;
    private final File _cmdCfgFilePath;
    private Set<File> _dbFiles;

    /**
     * Constructor.
     */
    public AbstractSoftIocConfigurator(@Nonnull final File executable,
                                       @Nonnull final File cmds) {
        _exeFilePath = executable;
        _cmdCfgFilePath = cmds;
        _dbFiles = Collections.emptySet();
    }

    /**
     * @return
     */
    @Override
    @Nonnull
    public String getDemoExecutableFilePath() {
        return _exeFilePath.toString();
    }

    /**
     * @return
     */
    @Override
    @Nonnull
    public File getSoftIocCmdFile() {
        return _cmdCfgFilePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Set<File> getDbFileSet() {
        return _dbFiles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ISoftIocConfigurator with(@Nonnull final File... dbFiles) {
        _dbFiles = Sets.newHashSet(dbFiles);
        return this;
    }

}
