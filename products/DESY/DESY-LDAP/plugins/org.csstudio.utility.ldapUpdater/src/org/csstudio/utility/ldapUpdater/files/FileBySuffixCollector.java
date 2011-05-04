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
package org.csstudio.utility.ldapUpdater.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.file.FilteredRecursiveFilePathParser;

import com.google.common.collect.Maps;

/**
 * Traverses a directory one level depth and filters all files not ending on
 * suffix.
 * On traversal a map from the simple file name (without suffix) to the file instance is
 * created.
 *
 * @author bknerr
 * @since 28.04.2011
 */
public class FileBySuffixCollector extends FilteredRecursiveFilePathParser {

    private final Map<String, File> _fileMap = Maps.newHashMap();
    private final String _suffix;

    /**
     * Constructor.
     * @throws FileNotFoundException
     */
    public FileBySuffixCollector(@Nonnull final File dir, @Nonnull final String suffix) throws FileNotFoundException {
        super(new SuffixBasedFileFilter(suffix, 1));
        _suffix = suffix;
        startTraversal(dir, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processFilteredFile(@Nonnull final File file,
                                                final int currentDepth) {
        _fileMap.put(file.getName().replace(_suffix, ""), file);
    }

    @Nonnull
    public Map<String, File> getFileMap() {
        return _fileMap;
    }

}
