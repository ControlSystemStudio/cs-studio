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
package org.csstudio.domain.desy.file;

import java.io.File;
import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Recursive file path parser.
 * Can be configured to traverse directory structures up to a parameterizable depth.
 * Implementors may override one of the two methods invoked by hitting a file or hitting a
 * directory on traversal.
 *
 * @author bknerr
 * @since 27.04.2011
 */
public abstract class AbstractRecursiveFilePathParser {

    private static final Logger LOG =
            LoggerFactory.getLogger(AbstractRecursiveFilePathParser.class);

    public void startTraversal(@Nonnull final File fileOrDirectory,
                               final int finalDepth) throws FileNotFoundException {
        traverseDirectory(0, finalDepth, fileOrDirectory);
    }

    private void traverseDirectory(final int currentDepth,
                                   final int finalDepth,
                                   @Nonnull final File f) throws FileNotFoundException {
        if (!f.exists()) {
            LOG.error("Recursive call to file which an instant before existed...deleted in between?");
            throw new FileNotFoundException("File " + f.getAbsolutePath() + " does not exist for traversal.");
        }
        if (f.isFile()) {
            processFile(f, currentDepth);
        } else if (f.isDirectory()) {
            processDirectory(f, currentDepth);

            if (currentDepth >= finalDepth) {
                return;
            }
            final String[] filePaths = f.list();
            for (final String filePath : filePaths) {
                traverseDirectory(currentDepth + 1, finalDepth, new File (f, filePath));
            }
        } else {
            LOG.warn("{} is neither file nor directory.", f.getAbsolutePath());
        }
    }

    @SuppressWarnings("unused")
    protected void processFile(@Nonnull final File f, final int currentDepth) {
        // Nothing
    }

    @SuppressWarnings("unused")
    protected void processDirectory(@Nonnull final File f, final int currentDepth) {
        // Nothing
    }
}
